package top.zzk.rpc.common.serializer;


/**
 * @author zzk
 * @date 2021/12/8
 * description
 */
public interface Serializer {
    int KRYO_SERIALIZER = 0;
    int JSON_SERIALIZER = 1;
    int HESSIAN_SERIALIZER = 2;
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
            default:
                return null;
        }
    }
}
