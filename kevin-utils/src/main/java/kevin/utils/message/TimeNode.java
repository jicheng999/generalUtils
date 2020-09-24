package kevin.utils.message;

import lombok.Data;

/**
 * Created by liujicheng on 2019/4/27.
 * 时间轮-时间槽
 */
@Data
public class TimeNode<T> {
    //上一个时间片
    private TimeNode<T> next;
    //当前时间片的任务链
    private TaskNode<T> task;
    //当前时间片在时间轮的第几个
    private int nodeNo;
}
