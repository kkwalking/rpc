package top.zzk.rpc.common.serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zzk
 * @date 2021/12/14
 * description
 */
public class ProtobufSerializer implements Serializer{
    private static final ThreadLocal<LinkedBuffer> bufferThreadLocal = ThreadLocal.withInitial(() ->{
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        return buffer;
    });
    
    @Override
    public byte[] serialize(Object obj) {
        Schema schema = RuntimeSchema.getSchema(obj.getClass());
        LinkedBuffer buffer = bufferThreadLocal.get();
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
//            bufferThreadLocal.remove();
            buffer.clear();
            bufferThreadLocal.remove();
        }

        return data;
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        Schema schema = RuntimeSchema.getSchema(clazz);
        Object obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }

    @Override
    public int getCode() {
        return Serializer.PROTOBUF;
    }
}
