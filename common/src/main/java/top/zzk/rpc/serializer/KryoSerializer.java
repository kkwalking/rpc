package top.zzk.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.entity.RpcRequest;
import top.zzk.rpc.entity.RpcResponse;
import top.zzk.rpc.exception.SerializeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author zzk
 * @date 2021/12/10
 * description 使用kryo进行序列化
 */
@Slf4j
public class KryoSerializer implements Serializer {
    
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
       Kryo kryo = new Kryo();
       kryo.register(RpcRequest.class);
       kryo.register(RpcResponse.class);
       kryo.setReferences(true);
       kryo.setRegistrationRequired(false);
       return kryo;
    });
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output out = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(out, obj);
            kryoThreadLocal.remove();
            return out.toBytes();
            
        } catch (IOException e) {
            log.error("序列化时有错误发生:" ,e);
            throw new SerializeException("序列化时有错误发生", e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)){
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return o;
            
        } catch (IOException e) {
            log.error("反序列化时有错误发生:" ,e);
            throw new SerializeException("反序列化时有错误发生", e);
        }
    }

    @Override
    public int getCode() {
        return KRYO_SERIALIZER;
    }
}
