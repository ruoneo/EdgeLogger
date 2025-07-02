package com.github.edgeLogger.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.sleep;

public class Utils {
    public static final Logger logger = LoggerFactory.getLogger("Utils.class");

    public static void displayWaitingInformation(long milliseconds) throws InterruptedException {
        logger.info("倒计时完成后开始下一次采集");
        new Thread(() -> {
            try {
                sleep(milliseconds % 1000);
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
                long l = 1000 - (System.currentTimeMillis() - start);

                try {
                    sleep(l);
                    totalSeconds--;
                } catch (IllegalArgumentException e) {
                    logger.error("时间差超过1秒", e);
                    try {
                        sleep(1000 + l % 1000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    totalSeconds -= 1 - (int) l / 1000;

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            logger.info(">>> 下一次采集开始！");
        }).start();
    }
}
