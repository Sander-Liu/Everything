package task;



import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public class FileScanTask {

    private static final ExecutorService POOL
            = Executors.newFixedThreadPool(4);
    //private static volatile int COUNT;

    private static AtomicInteger COUNT =new AtomicInteger();

    private static final CountDownLatch LATCH = new CountDownLatch(1);

    private FileScanCallback callback;

    public FileScanTask(FileScanCallback callback){
        this.callback=callback;
    }

//    public static void main(String[] args) throws InterruptedException {
//        FileScanTask task=new FileScanTask();
//        task.startScan(new File("D:\\Sander"));//多线程运行程序扫描任务
////                task.waitFinish();
//        synchronized (task){
//            task.wait();
//        }
//        System.out.println("执行完毕");
//    }
    //启动根目录的扫描任务
    public void startScan(File root) {
//        synchronized (this) {
//            COUNT++;
//        }
        COUNT.incrementAndGet();
        POOL.execute(new Runnable() {
            @Override
            public void run() {
                list(root);
            }
        });

    }
    //等待扫面人物执行结束
    public void waitFinish() {
//        try {
//            synchronized (this) {
//                this.wait();
//            }
//        } catch (InterruptedException e){
//            e.printStackTrace();
//        }
        try {
            LATCH.await();
        } catch (InterruptedException e) {
            //中断所有线程的操作
            POOL.shutdown();//调用每个线程的interrupt()中断
            //POOL.shutdownNow();//调用每个线程的stop()关闭
        }
    }
    public void list(File dir) {
        if (!Thread.interrupted()) {
            try {
                callback.execute(dir);
             //   System.out.println(dir.getPath());
                if (dir.isDirectory()) {
                    File[] children = dir.listFiles();
                    if (children != null && children.length > 0) {
                        for (File child : children) {
                            //启动子线程子文件夹的扫描任务
                            if (child.isDirectory()) {
//                            synchronized (this) {
//                                COUNT++;
//                            }
                                COUNT.incrementAndGet();
                                POOL.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        list(child);
                                    }
                                });
                            } else {
                                callback.execute(child);
                                //System.out.println(child.getPath());
                            }
                        }
                    }
                }
            } finally {
//            synchronized (this) {
////                COUNT--;
////                if(COUNT==0){
////                    //notify
////                    this.notifyAll();
////                }
////            }
                //所有线程执行完毕
                if (COUNT.decrementAndGet() == 0) {
                    LATCH.countDown();
                }
            }
        }
    }

}
