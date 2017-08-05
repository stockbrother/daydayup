package daydayup.openstock;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BackGroundTaskScheduler {

   ExecutorService executor = Executors.newCachedThreadPool();

    public <T> Future<T> runTask(Callable<T> task) {
        return executor.submit(task);
    }

}
