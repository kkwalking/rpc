package top.zzk.rpc.common.serializer;


/**
 * @author zzk
 * @date 2021/12/8
 * description
 */
public interface Serializer {
    int JSON_SERIALIZER = 1;
    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static Serializer getByCode(int code) {
        switch (code) {
            case JSON_SERIALIZER:
                return new JsonSerializer();
            default:
                return null;
        }
    }
}
