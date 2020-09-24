package kevin.utils.threadpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 全局线程池
 * Created by liujicheng on 2019/04/19.
 */
public class ThreadPoolManager {

    //线程池size
    private static final int POOL_SIZE = 20;

    private static final ThreadPoolExecutor tx = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE, 20L,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>(400));

    static {
        tx.allowCoreThreadTimeOut(true);
    }

    private ThreadPoolManager() {
    }

    public static ThreadPoolExecutor getExecutor() {
        return tx;
    }

}
