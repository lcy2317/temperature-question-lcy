import java.util.concurrent.Semaphore;

public class TestSemaphore {

    // 设置信号量大小
    private static final Semaphore semaphore = new Semaphore(1);

    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            try {
                semaphore.acquire();
                System.out.println(2);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        });
        Thread thread1 = new Thread(() -> {
            try {
                semaphore.acquire();
                System.out.println(1);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        });
        thread1.start();
        thread.start();
    }

}
