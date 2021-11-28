package top.zzk.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.zzk.rpc.common.entity.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author zzk
 * @date 2021/11/28
 * description  rpc客户端
 */
public class RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    public Object sendRequest(RpcRequest rpcRequest, String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream((socket.getOutputStream()));
            out.writeObject(rpcRequest);
            out.flush();
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用时发生错误：");
            return null;
        }
    }
}
