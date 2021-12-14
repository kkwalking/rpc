package top.zzk.rpc.api;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zzk
 * @date 2021/11/27 20:40
 * description 测试用API
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelloObject implements Serializable {

    private Integer id;
    private String message;
}
