package kevin.utils.message;

import kevin.utils.threadpool.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

/**
 * Created by liujicheng on 2019/4/27.
 * 消息发送器
 */
//@Component
public class MessageSender implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);

    //间隔多久进行一次消息检测发送  单位：秒
    private static final int TIME_INTERVAL = 5;

    private static TimeWheel<MessageAndHandler> timeWheel = new TimeWheel<>(TIME_INTERVAL, 120);

    //添加任务消息
    public static synchronized void addTask(MessageAndHandler task, int expireTime) {
        timeWheel.addTask(task, expireTime);
    }

    @Override
    public void run() {
        try {
            while (true) {
                List<MessageAndHandler> messages;

                messages = timeWheel.getNextExpiredTask();

                if (messages.size() > 0) {
                    ThreadPoolManager.getExecutor().execute(() -> {
                        messages.forEach(item -> item.getHandler().handler(item.getMessage()));
                    });
                }

                Thread.sleep(TIME_INTERVAL * 1000);
            }
        } catch (Exception e) {
            logger.error("消息发送器异常:", e);
        }
    }
}
