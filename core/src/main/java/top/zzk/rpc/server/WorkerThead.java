package top.zzk.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.zzk.rpc.common.entity.RpcRequest;
import top.zzk.rpc.common.entity.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @author zzk
 * @date 2021/11/28
 * description
 */
public class WorkerThead implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(WorkerThead.class);

    private Socket socket;
    private Object service;

    public WorkerThead(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream((socket.getInputStream()))) {
            RpcRequest request = (RpcRequest) inputStream.readObject();
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            Object ret = method.invoke(service, request.getParams());
            outputStream.writeObject(RpcResponse.success(ret));
            outputStream.flush();
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("调用或发送时发生错误：", e);
        }

    }
}
