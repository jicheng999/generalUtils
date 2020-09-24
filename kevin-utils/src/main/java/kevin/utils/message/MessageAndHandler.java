package kevin.utils.message;

import lombok.Data;

/**
 * Created by liujicheng on 2019/4/28.
 * 消息和处理器 vo
 */
@Data
public class MessageAndHandler {
    private SPmMessage message;
    private IMessageHandler handler;
}
