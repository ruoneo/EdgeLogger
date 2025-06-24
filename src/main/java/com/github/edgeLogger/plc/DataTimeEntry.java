package com.github.edgeLogger.plc;

import java.util.Arrays;
import java.util.Objects;

public class DataTimeEntry {
    public double[] doubles;
    public String timestamp;

    public DataTimeEntry(double[] doubles, String timestamp) {
        this.doubles = doubles;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataTimeEntry that = (DataTimeEntry) o;
        // 比较时间字符串和 float 数组
        return Arrays.equals(doubles, that.doubles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, Arrays.hashCode(doubles));
    }

    public double[] getDoubles() {
        return doubles;
    }

    public String getTimestamp() {
        return timestamp;
    }
}