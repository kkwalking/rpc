package top.zzk.rpc.api;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class EchoMessage implements Serializable {
    public EchoMessage() {
    }

    private Integer id;
    private String echoContent;
}
