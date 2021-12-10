package top.zzk.rpc.common.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import lombok.extern.slf4j.Slf4j;
import top.zzk.rpc.common.exception.SerializeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author zzk
 * @date 2021/12/10
 * description
 */
@Slf4j
public class HessianSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        HessianOutput output = null;
        try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream()){
            output = new HessianOutput(arrayOutputStream);
            output.writeObject(obj);
            return arrayOutputStream.toByteArray();
            
        } catch (IOException e) {
            log.error("序列化时有错误发生：", e);
            throw new SerializeException("序列化时有错误发生", e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    log.error("关闭流时有错误发生：",e);
                }
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        HessianInput input = null;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            input = new HessianInput(inputStream);
            return input.readObject(clazz);
        } catch (IOException e) {
            log.error("序列化时有错误发生：", e);
            throw new SerializeException("序列化时有错误发生", e);
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    @Override
    public int getCode() {
        return HESSIAN_SERIALIZER;
    }
}
