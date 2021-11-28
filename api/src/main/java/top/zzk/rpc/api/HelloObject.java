package top.zzk.rpc.api;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zzk
 * @date 2021/11/27 20:40
 * description
 *
 */

@Data
@AllArgsConstructor
public class HelloObject implements Serializable {
    
    private Integer id;
    private String message;
}
