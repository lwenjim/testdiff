import java.time.Duration;

public class Test {
    public static void main(String[] args) {
        try {
            Thread[] list = new Thread[10];
            for (int i = 0; i < 10; i++) {
                final int f = i;
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(Duration.ofSeconds(2));
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        System.out.println(String.format("第 %d 现成结束", f));
                    }
                }, String.format("%d", i));
                list[i] = t;
            }
            for (Thread t : list) {
                t.wait();
            }
            System.out.println("the end");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
