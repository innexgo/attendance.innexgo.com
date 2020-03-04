package innexgo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService extends ThreadPoolTaskScheduler {

  private final Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();

  @Override
  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
    ScheduledFuture<?> future = super.scheduleWithFixedDelay(task, delay);
    scheduledTasks.put(((ScheduledMethodRunnable) task).getMethod().getName(), future);
    return future;
  }

  public void restartTask(String mname) {
    ScheduledFuture<?> task = scheduledTasks.get(mname);
    task.cancel(true);
    scheduleWithFixedDelay(((Runnable)task), task.getDelay(TimeUnit.MILLISECONDS));
  }

  @Bean
  public TaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler customScheduler = new SchedulerService();
    customScheduler.setPoolSize(4);
    return customScheduler;
  }
}
