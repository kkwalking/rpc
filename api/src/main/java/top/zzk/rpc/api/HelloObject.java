package top.zzk.rpc.api;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zzk
 * @date 2021/11/27 20:40
 * description 测试用API
 *
 */
@Data
@AllArgsConstructor
public class HelloObject implements Serializable {
    public HelloObject() {
    }

    private Integer id;
    private String message;
}
