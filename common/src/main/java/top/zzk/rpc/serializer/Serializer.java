package top.zzk.rpc.serializer;


/**
 * @author zzk
 * @date 2021/12/8
 * description  自定义序列化器接口
 */
public interface Serializer {
    int KRYO_SERIALIZER = 0;
    int JSON_SERIALIZER = 1;
    int HESSIAN_SERIALIZER = 2;
    int PROTOBUF_SERIALIZER = 3;
    
    int DEFAULT_SERIALIZER = 0;
    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static Serializer getByCode(int code) {
        switch (code) {
            case HESSIAN_SERIALIZER:
                return new HessianSerializer();
            case KRYO_SERIALIZER:
                return  new KryoSerializer();
            case JSON_SERIALIZER:
                return new JsonSerializer();
            case PROTOBUF_SERIALIZER:
                return new ProtobufSerializer();
            default:
                return null;
        }
    }
}
