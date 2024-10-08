import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class Test extends Thread {
    private static int count;

    private String name;

    public void run() {
        for (int i = 0; i < 1000; i++) {
            count();
        }
    }

    public synchronized void count() {
        count++;
        System.out.printf("name: %s, count: %d\n", this.name, count);
    }

    public Test(String name) {
        this.name = name;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Test mt1 = new Test("A");
        Test mt2 = new Test("B");

        mt1.start();
        mt2.start();
        mt1.join();
        mt2.join();

        Thread.startVirtualThread(() -> { System.out.println("hello"); });
        Thread.ofVirtual().name("a").start(() -> { System.out.println("world"); });

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

        // try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        //     IntStream.range(0, 10_000).forEach(i -> {
        //         executor.submit(() -> {
        //             Thread.sleep(Duration.ofSeconds(1));
        //             return i;
        //         });
        //     });
        // }

        // System.out.println("使用关键字synchronized");
        // SyncThread syncThread = new SyncThread();
        // Thread thread1 = new Thread(syncThread, "SyncThread1");
        // Thread thread2 = new Thread(syncThread, "SyncThread2");
        // thread1.start();
        // thread2.start();
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

class SyncThread implements Runnable {
    private static int count;
    public SyncThread() {
        count = 0;
    }
    public void run() {
        synchronized (this) {
            for (int i = 0; i < 5; i++) {
                try {
                    System.out.println(
                        "线程名:" + Thread.currentThread().getName() + ":" + (count++));
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public int getCount() {
        return count;
    }
}
