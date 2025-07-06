package com.github.edgeLogger;

import com.github.edgeLogger.utils.Utils;
import org.junit.Test;

import java.util.concurrent.*;

public class MainTest {
    @Test
    public void test1() throws ExecutionException, InterruptedException {
        final ScheduledExecutorService scheduleExecutor1 = Executors.newSingleThreadScheduledExecutor();
        CompletableFuture<Void> completionFuture = new CompletableFuture<>();
        scheduleExecutor1.schedule(() -> {
            System.out.println("开始执行任务");
            // 立即开始，间隔 1 秒
            completionFuture.complete(null); // 通知主线程：任务完成
        }, 123, TimeUnit.MILLISECONDS); // 初始延迟为余数时间
        completionFuture.get();

        int a = 30;
        while (a > 0) {

            System.out.println("第一次:"+System.currentTimeMillis());
            long start = System.currentTimeMillis();
            long displayTime = Utils.displayWaitingInformation(3000, scheduleExecutor1);// 主线程在此阻塞，直到倒计时结束
            System.out.println(System.currentTimeMillis() - start + " " + displayTime);
            a--;
        }
    }
}
