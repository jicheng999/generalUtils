package kevin.utils.message;

import lombok.Data;

/**
 * Created by liujicheng on 2019/4/27.
 * 时间轮-任务node
 */
@Data
public class TaskNode<T> {
    //下一个任务节点
    private TaskNode next;
    //需要等待的轮数，当前直接执行，取0
    private int delayCount;
    private T task;

    public TaskNode(T task) {
        this(task, 0);
    }

    public TaskNode(T task, int delayCount) {
        this.task = task;
        this.delayCount = delayCount;
    }
}
