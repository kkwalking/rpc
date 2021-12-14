package top.zzk.rpc.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/*
测试用API，可忽略
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EchoMessage implements Serializable {

    private Integer id;
    private String echoContent;
}
