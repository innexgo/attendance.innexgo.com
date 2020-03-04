package innexgo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService extends ThreadPoolTaskScheduler {

  private final static ConcurrentMap<String, Runnable> runnableTasks = new ConcurrentHashMap<>();
  private final static ConcurrentMap<String, Long> delayTasks = new ConcurrentHashMap<>();
  private final static ConcurrentMap<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

  @Override
  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
    ScheduledFuture<?> future = super.scheduleWithFixedDelay(task, delay);

    String mname = ((ScheduledMethodRunnable) task).getMethod().getName();

    delayTasks.put(mname, delay);
    runnableTasks.put(mname, task);
    scheduledTasks.put(mname, future);

    return future;
  }

  public void restartTask(String mname) {

    ScheduledFuture<?> future = scheduledTasks.get(mname);
    Runnable task = runnableTasks.get(mname);
    Long delay = delayTasks.get(mname);

    // Kill the currently running task
    future.cancel(true);

    // Restart
    scheduleWithFixedDelay(task, delay);
  }

  @Bean
  public TaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler customScheduler = new SchedulerService();
    customScheduler.setPoolSize(4);
    return customScheduler;
  }
}
