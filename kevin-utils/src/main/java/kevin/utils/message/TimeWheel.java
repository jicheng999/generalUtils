package kevin.utils.message;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by liujicheng on 2019/4/27.
 * 简单时间轮
 */
@Data
public class TimeWheel<T> {
    private static final Logger logger = LoggerFactory.getLogger(TimeWheel.class);
    //时间间隔 /秒
    private int interval;
    //时间槽 最大数量
    private int timeCount;

    private TimeNode<T> headTimeNode;

    private TimeNode<T> currentTimeNode;

    //时间轮转动，取任务 ， 添加任务 ，不可同时发生 --- 乐观锁信号
    private static AtomicBoolean signal = new AtomicBoolean(false);


    public TimeWheel(int interval, int timeCount) {
        this.interval = interval;
        this.timeCount = timeCount;
        ini();
    }

    //初始化时间轮
    private void ini() {
        if (this.timeCount < 1) {
            logger.info("时间轮槽数小于1，不初始化");
            return;
        }

        headTimeNode = new TimeNode();
        headTimeNode.setNodeNo(0);
        TimeNode thisTimeNode = headTimeNode;
        for (int i = 0; i < this.timeCount - 1; i++) {
            thisTimeNode.setNext(new TimeNode());
            thisTimeNode = thisTimeNode.getNext();
            thisTimeNode.setNodeNo(i + 1);
        }
        thisTimeNode.setNext(headTimeNode);
        currentTimeNode = headTimeNode;
    }

    //获取第一个时间节点
    public TimeNode getHeadTimeNode() {
        return this.headTimeNode;
    }

    public List<T> getNextExpiredTask(){
        //加乐观锁
        optimisticLock();

        moveToNextTimeNode();
        List<T> list = getCurrentExpireTask();

        //释放乐观锁
        optimisticUnlock();
        return list;
    }

    //当前指针移动到下一个时间节点，并且返回节点
    private TimeNode moveToNextTimeNode() {
        currentTimeNode = currentTimeNode.getNext();
        return currentTimeNode;
    }

    public TimeNode getCurrentTimeNode() {
        return currentTimeNode;
    }

    //返回当前时间槽过期的任务,没有过期的轮转次数减1
    private List<T> getCurrentExpireTask() {


        List<T> taskList = new ArrayList<>();

        TaskNode<T> currentTaskNode = currentTimeNode.getTask();
        TaskNode<T> preTaskNode = null;
        while (null != currentTaskNode) {
            T task = currentTaskNode.getTask();
            if (null != task && currentTaskNode.getDelayCount() < 1) {
                taskList.add(task);
                //从任务列表中删除
                //删除当前node的task
                currentTaskNode.setTask(null);
                //如果有下一个节点，则将下一个节点拉上来
                if (currentTaskNode.getNext() != null) {
                    //如果记录的上一个节点是null则当前currentTaskNode是时间片上的第一个节点，则要将下一个节点直接挂在时间片上
                    if (null == preTaskNode) {
                        currentTimeNode.setTask(currentTaskNode.getNext());
                        preTaskNode = currentTaskNode.getNext();
                    } else {
                        preTaskNode.setNext(currentTaskNode.getNext());
                    }
                    currentTaskNode = currentTaskNode.getNext();
                }
            } else {//如果当前节点没有过期，则继续向下遍历
                currentTaskNode.setDelayCount(currentTaskNode.getDelayCount() - 1);
                preTaskNode = currentTaskNode;
                currentTaskNode = currentTaskNode.getNext();
            }
        }

        return taskList;
    }

    //设置任务 ， 过期时间单位--秒
    public void addTask(T task, int expireTime) {
        //加乐观锁
        optimisticLock();

        //转一轮总共时间
        int totalTime = this.interval * this.timeCount;
        int mod = Math.floorMod(expireTime, totalTime);//取余数
        int count = expireTime / totalTime;//倍数越大，需要轮转次数越多
        //构造任务节点
        TaskNode<T> taskNode = new TaskNode<>(task, count);
        //余数和时间片间隔比较，决定放在哪个timeNode 的偏移量
        int timeNo = mod / this.interval + 1;

        TimeNode saveTimeNode = this.currentTimeNode;
        //存任务node
        for (int i = 0; i < timeNo; i++) {//偏移
            saveTimeNode = saveTimeNode.getNext();
        }

        //放在第一个
        taskNode.setNext(saveTimeNode.getTask());
        saveTimeNode.setTask(taskNode);

        //释放乐观锁
        optimisticUnlock();
    }

    private void optimisticLock(){
        while(!signal.compareAndSet(false,true)){
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                logger.error("时间轮乐观锁等待时，sleep放弃cpu失败",e);
            }
        }
    }

    private void optimisticUnlock(){
        signal.set(false);
    }
}



//
//@Component
//public class MessageSendShedule implements ApplicationRunner {
//
//    private static final Logger logger = LoggerFactory.getLogger(com.xyebank.payment.shedule.MessageSendShedule.class);
//
//    @Autowired
//    MessageSender messageSender;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        logger.info("消息发送器，开始启动......");
//        Thread senderT = new Thread(messageSender);
//        senderT.start();
//    }
//}
