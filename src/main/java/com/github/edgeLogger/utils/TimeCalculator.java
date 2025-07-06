package com.github.edgeLogger.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimeCalculator {
    public static final Logger logger = LoggerFactory.getLogger("TimeCalculator");

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static String logFutureTime(long milliseconds) {
        // 计算未来时间点
        ZonedDateTime futureTime = ZonedDateTime.now().plus(milliseconds, ChronoUnit.MILLIS);

        // 使用 logger 打印结果
        return futureTime.format(FORMATTER);
    }
}

