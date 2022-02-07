package top.zzk.rpc.common.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zzk
 * @date 2022/2/7
 * description 单例工厂
 */
public class SingletonFactory {
    private static Map<Class, Object> objectMap = new HashMap<>();

    private SingletonFactory() {
    }

    public static <T> T getInstance(Class<T> clazz) {
        Object instance = objectMap.get(clazz);
        synchronized (clazz) {
            if (instance == null) {
                try {
                    instance = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                objectMap.put(clazz, instance);
            }
        }
        return clazz.cast(instance);
    }
}
