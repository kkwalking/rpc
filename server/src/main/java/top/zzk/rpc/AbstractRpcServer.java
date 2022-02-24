package top.zzk.rpc;

import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.annotation.Service;
import top.zzk.rpc.annotation.ServiceScan;
import top.zzk.rpc.common.enumeration.RpcError;
import top.zzk.rpc.common.exception.RpcException;
import top.zzk.rpc.common.registry.ServiceRegistry;
import top.zzk.rpc.common.serializer.*;
import top.zzk.rpc.serviceprovider.ServiceProvider;
import top.zzk.rpc.utils.ReflectUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author zzk
 * @date 2022/2/8
 * description
 */
@Slf4j
public abstract class AbstractRpcServer implements RpcServer {
    protected String host;
    protected int port;
    protected Serializer serializer;
    protected String registryHost;
    protected int registryPort;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;
    @Override
    public <T> void publishService(T service, String serviceName) {
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }

    @Override
    public void startup() {
        scanServices();
        start();
    }

    @Override
    public void config() {
        Properties pro = new Properties();
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("conf.properties");
            pro.load(in);

            if(pro.get("serializer") != null) {
                String serializerStr = pro.getProperty("serializer");
//                #可选[kryo, json, hessian, protobuf]
                switch (serializerStr){
                    default: //默认kryo
                    case "kryo":
                        this.serializer = new KryoSerializer();
                        break;
                    case "json":
                        this.serializer = new JsonSerializer();
                        break;
                    case "hessian":
                        this.serializer = new HessianSerializer();
                        break;
                    case "protobuf":
                        this.serializer = new ProtobufSerializer();
                        break;
                }
                log.info("序列化器为{}", serializerStr);
            } else {
                //默认为kryo
                this.serializer = this.serializer = new KryoSerializer();
            }
            this.registryHost = pro.getProperty("registry_address", "127.0.0.1");
            log.info("注册中心地址为{}", registryHost);
            this.registryPort = Integer.parseInt(pro.getProperty("registry_host", "8848"));
            log.info("注册中心端口为{}", registryPort);
        } catch (FileNotFoundException e) {
            log.error("读取配置文件失败");
        } catch (IOException e) {
            log.error("位置文件加载失败");
        }
    }

    public void scanServices() {
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if(!startClass.isAnnotationPresent(ServiceScan.class)) {
                log.error(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND.getErrorMessage());
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            log.error(RpcError.UNKNOWN_ERROR.getErrorMessage());
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for(Class<?> clazz : classSet) {
            if(clazz.isAnnotationPresent(Service.class)) {
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface: interfaces){
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                } else {
                    publishService(obj, serviceName);
                }
            }
        }
    }
}
