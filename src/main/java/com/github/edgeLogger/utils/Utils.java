package com.github.edgeLogger.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Utils {
    public static final Logger logger = LoggerFactory.getLogger("Utils.class");

    public static long displayWaitingInformation(long milliseconds, ScheduledExecutorService scheduleExecutor) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        CompletableFuture<Void> completionFuture = new CompletableFuture<>();
        long millisRemainder = milliseconds % 1000;
        int totalSeconds = (int) (milliseconds / 1000);

        // 处理不足 1 秒的余数时间
        scheduleExecutor.schedule(() -> {
            final AtomicInteger remainingSeconds = new AtomicInteger(totalSeconds);
            final ScheduledFuture<?>[] futureHolder = new ScheduledFuture[1];

            // 每秒执行一次的任务
            futureHolder[0] = scheduleExecutor.scheduleAtFixedRate(() -> {
                if (Thread.currentThread().isInterrupted()) return;
                int current = remainingSeconds.getAndDecrement();
                if (current <= 0) {
                    futureHolder[0].cancel(false);  // 取消任务
                    System.out.printf("\r预计%s后开始下一次采集 ", String.format("%02d:%02d", 0, 0));
                    System.out.flush();
                    logger.info(">>> 下一次采集开始！");
                    completionFuture.complete(null); // 通知主线程：任务完成
                    return;
                }

                // 格式化并输出倒计时
                System.out.printf("\r预计%s后开始下一次采集 ", String.format("%02d:%02d", current / 60, current % 60));
                System.out.flush();
            }, 0, 1000, TimeUnit.MILLISECONDS); // 立即开始，间隔 1 秒

        }, millisRemainder, TimeUnit.MILLISECONDS); // 初始延迟为余数时间

        completionFuture.get();
        return System.currentTimeMillis() - start - milliseconds;
    }
}
