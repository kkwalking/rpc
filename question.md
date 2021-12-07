#### 为什么分RpcError和 RpcException,并且其中的RpcException是继承RuntimeException?
#### 2021年12月6日 遇到奇怪的BUG，启动服务器和客户端进行通信，结果两边都卡住没反应，疑似客户端出现了点问题
解决：发现是在服务端的getInputStream()产生了阻塞，当时的写法：

```java
//服务端
ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//客户端
ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
ObjectOutputStream out = new ObjectOutputStream((socket.getOutputStream()));
```

第一遍解决是通过调换服务端的输入输出流两行代码的位置，可以解决

后面发现只要调换客户端的输入输出流两行代码的位置，无论服务端两行代码的位置怎么换都可以。

```java
//客户端修改如下
ObjectOutputStream out = new ObjectOutputStream((socket.getOutputStream()));
ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
```

目前的猜想是，当在客户端使用错误的方式（先打开输入流再打开输出流），客户端会在输入流这里产生阻塞，而服务端由于还没有收到客户端的请求，等待客户端发送请求，也在`socket.getInputStream()`处产生了阻塞而无法处理消息。结果就是客户端也一直阻塞在`socket.getInputStram()`方法上，两边都发生了阻塞。

查阅资料发现可能是`ObjectOutputStream`和`ObjectInputStream`的问题，使用原生字节流做了实验，发现确实与原生字节流代码顺序无关，那么大概率就是`ObjectOutputStream`和`ObjectInputStream`有关。在stackoverflow上有一篇问答，也提到了`ObjectOutputStream`和`ObjectInputStream`的问题（header）[getInputStream blocks?](https://stackoverflow.com/questions/8088557/getinputstream-blocks)

> yes, this question has been asked many times before. the object stream format has a header, and the ObjectInputStream reads that header on construction. therefore, it is blocking until it receives that header over the socket. assuming your client/server code looks similar, after you construct the ObjectOutputStream, flush it. this will force the header data over the wire.

对`ObjectOutputStream`和`ObjectInputStream`加上测试之后发现确实与客户端new出`ObjectOutputStream`和`ObjectInputStream`的顺序有关，先output再input则正常使用。

最后stackoverflow上找到解答 [java - new ObjectInputStream() blocks - Stack Overflow](https://stackoverflow.com/questions/14110986/new-objectinputstream-blocks/14111047)

> You need to create the `ObjectOutputStream` before the `ObjectInputStream` at both sides of the connection(!). When the `ObjectInputStream` is created, it tries to read the object stream header from the `InputStream`. So if the `ObjectOutputStream` on the other side hasn't been created yet there is no object stream header to read, and it will block indefinitely.
>
> Or phrased differently: If both sides first construct the `ObjectInputStream`, both will block trying to read the object stream header, which won't be written until the `ObjectOutputStream` has been created (on the other side of the line); which will never happen because both sides are blocked in the constructor of `ObjectInputStream`.
>
> This can be inferred from the Javadoc of [`ObjectInputStream(InputStream in)`](http://docs.oracle.com/javase/7/docs/api/java/io/ObjectInputStream.html#ObjectInputStream(java.io.InputStream)):

java文档内容如下：

> Creates an ObjectInputStream that reads from the specified InputStream. A serialization stream header is read from the stream and verified. This constructor will block until the corresponding ObjectOutputStream has written and flushed the header

总结：当错误使用`new ObjectOutputStream(OutputStream out)`和new `ObjectInputStream(InputStream out)`的顺序时,会导致服务端和客户端都阻塞等待对方发消息从而产生死锁。