import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Test {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // MyThread mt1 = new MyThread();
        // MyThread mt2 = new MyThread();
        // mt1.start();
        // mt2.start();
        // System.out.println("Test.main()");

        // MyThread2 mtt2 = new MyThread2();
        // Thread td = new Thread(mtt2);
        // td.start();

        // System.out.println("--------程序开始运行--------");
        // Date date1 = new Date();
        // int taskSize = 5;
        // ExecutorService pool = Executors.newFixedThreadPool(taskSize);
        // List<Future> list = new ArrayList<Future>();

        // for (int i = 0; i < taskSize; i++) {
        //     Callable c = new MyCallable(i + " ");
        //     Future f = pool.submit(c);
        //     list.add(f);
        // }
        // pool.shutdown();
        // for (Future future : list) {
        //     System.out.println(">>>" + future.get().toString());
        // }

        // Date date2 = new Date();
        // System.out.println("-------程序结束运行--------, 程序运行时间[" + (date2.getTime() -
        // date1.getTime()) + "] 毫秒");

        System.out.println(0.1 + 0.2);
    }
}

class MyThread extends Thread {
    public void run() {
        System.out.println("MyThread.run()");
    }
}

class MyThread2 implements Runnable {
    public void run() {
        System.out.println("MyThread2.run()");
    }
}

class MyCallable implements Callable<Object> {
    private String taskNum;
    MyCallable(String taskNum) {
        this.taskNum = taskNum;
    }
    public Object call() throws Exception {
        System.out.println(">>>" + taskNum + "任务启动");
        Date dateTmpl = new Date();
        Thread.sleep(1000);
        Date dateTmpl2 = new Date();
        long time = dateTmpl2.getTime() - dateTmpl.getTime();
        System.out.println(">>> " + taskNum + "任务终止");
        return taskNum + "任务返回运行结果,当前任务时间[" + time + "]毫秒";
    }
}
