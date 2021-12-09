package top.zzk.rpc.common.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.common.entity.RpcRequest;

import java.io.IOException;

/**
 * @author zzk
 * @date 2021/12/8
 * description
 */
@Slf4j
public class JsonSerializer implements Serializer {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public byte[] serialize(Object obj) {
        byte[] res;
        try {
            res = objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            log.error("序列化时有错误发生:{}", e.getMessage());
            e.printStackTrace();
            res = null;
        }
        return res;
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            if (obj instanceof RpcRequest) {
                obj = handleRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            log.error("反序列化时有错误发生: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }

    }
    /*
    对对象中的数组进行重新序列化处理，避免序列化出错
     */
    private Object handleRequest(Object obj) throws IOException {
        RpcRequest request = (RpcRequest) obj;
        for (int i = 0; i < request.getParamTypes().length; i++) {
            Class<?> paramType = request.getParamTypes()[i];
            if(!paramType.isAssignableFrom(request.getParams()[i].getClass())) {
                byte[] bytes = objectMapper.writeValueAsBytes(request.getParams()[i]);
                request.getParams()[i] = objectMapper.readValue(bytes, paramType);
            }
        }
        return request;
    }

    @Override
    public int getCode() {
        return JSON_SERIALIZER;
    }
}
