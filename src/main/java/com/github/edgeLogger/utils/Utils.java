package com.github.edgeLogger.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Utils {
    public static final Logger logger = LoggerFactory.getLogger("Utils.class");

    public static void displayWaitingInformation(long collectIntervalMs, long startTime) throws InterruptedException {
        new Thread(() -> {
            long milliseconds = collectIntervalMs - (System.currentTimeMillis() - startTime);
            try {
                TimeUnit.MILLISECONDS.sleep(milliseconds % 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int totalSeconds = (int) (milliseconds / 1000);
            while (totalSeconds >= 0) {
                long start = System.currentTimeMillis();
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;
                String time = String.format("%02d:%02d", minutes, seconds);
                System.out.printf("\r预计%s秒后开始下一次采集 ", time);
                System.out.flush();
                long l = System.currentTimeMillis() - start;
                try {
                    Thread.sleep(l);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException e) {
                    logger.error("超时值为负数", e);
                    try {
                        Thread.sleep(l % 1000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    totalSeconds = totalSeconds + (int) l / 1000;
                }
                totalSeconds--;
            }

            logger.info(">>> 下一次采集开始！");
        }).start();
    }
}
