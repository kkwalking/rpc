import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import top.zzk.rpc.api.HelloObject;
import top.zzk.rpc.common.entity.RpcRequest;
import top.zzk.rpc.common.entity.RpcResponse;
import top.zzk.rpc.common.enumeration.RpcError;
import top.zzk.rpc.common.enumeration.RpcResponseCode;
import top.zzk.rpc.common.exception.RpcException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

/**
 * @author zzk
 * @date 2021/12/7
 * description
 */
@Slf4j
public class ObjectInputStreamBug {
    private static final int BUFSIZE = 32;

    /*
    原生字节流顺序测试，测试结果：无关
     */
    @Test
    public void ByteStreamServer() throws IOException {
        ServerSocket serSock = new ServerSocket(9090);
        byte[] receiveBuf = new byte[BUFSIZE];
        int reveMsgSize = 0;
        while (true) {
            Socket clientSock = serSock.accept();
            SocketAddress clientAddress = clientSock.getRemoteSocketAddress();
            System.out.println("Handing client at " + clientAddress);
            InputStream in = clientSock.getInputStream();
            log.info("server成功打开输入流");
            OutputStream out = clientSock.getOutputStream();
            log.info("server成功打开输出流");

            while ((reveMsgSize = in.read(receiveBuf)) != -1) {
                out.write(receiveBuf, 0, reveMsgSize);
            }
            clientSock.close();
        }
    }


    /*
     测试inputstream 和outstream的相对位置是否是产生bug的原因
     */
    @Test
    public void ByteStreamServerClient() throws IOException {
        Socket socket = new Socket("127.0.0.1", 9090);
        byte[] revData;
        String sendMessage = "welcome zzk";
        byte[] data = sendMessage.getBytes();
        revData = new byte[data.length];
        System.out.println("Connected to server...sending echo string");
        InputStream in = socket.getInputStream();
        log.info("成功打开输入流");
        OutputStream out = socket.getOutputStream();
        log.info("成功打开输出流");

        out.write(data);

        int totalBytesRcvd = 0;
        int bytesRcvd = 0;
        while (totalBytesRcvd < data.length) {
            if ((bytesRcvd = in.read(revData, totalBytesRcvd, data.length - totalBytesRcvd))
                    == -1)
                throw new SocketException("Connection colsed prematurely");
            totalBytesRcvd += bytesRcvd;
        }
        System.out.println("Received: " + new String(revData));
        socket.close();

    }

    @Test
    public void ObjectServer() throws IOException {
        ServerSocket serSock = new ServerSocket(9090);
        Socket socket = serSock.accept();
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())
        ) {
            log.info("成功打开输入输出流");
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            log.info("请求服务:{},请求方法{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
            HelloObject result = new HelloObject(1, "hello zzk");
            objectOutputStream.writeObject(RpcResponse.success(result));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            log.error("调用或发送时有错误发生：", e);
        }
    }

    @Test
    public void ObjectClient() {
        RpcRequest rpcRequest = RpcRequest.builder().interfaceName("HelloService")
                .methodName("hello")
                .paramTypes(new Class[]{String.class})
                .params(new Object[]{"zzk"})
                .build();
        String host = "127.0.0.1";
        int port = 9090;
        try (Socket socket = new Socket(host, port)) {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream((socket.getOutputStream()));
            


            out.writeObject(rpcRequest);
            log.info("write request to socket({}:{})", host, port);
            out.flush();
            RpcResponse response = (RpcResponse) in.readObject();
            log.info("write response from socket({}:{}), response:{}", host, port, response);
            if (response == null) {
                log.error("服务调用失败,service:{}", rpcRequest.getInterfaceName());

            }
            assert response != null;
            if (response.getStatusCode() == null || response.getStatusCode() != RpcResponseCode.SUCCESS.getCode()) {
                log.error("服务调用失败，service:{}, response:{}", rpcRequest.getInterfaceName(),
                        response);
            }
            log.info("{}", response.getData());
        } catch (IOException | ClassNotFoundException e) {
            log.error("调用时发生错误：");
        }
    }


}
