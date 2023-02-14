package com.onemsg.datafaker;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 延迟对象, 单位毫秒
 */
public record Delay(
        long min,
        long max) {

    public static final Delay ZERO = new Delay(0, 0);

    /**
     * Create from a string like "500-1000" or "500"
     * 
     * @param delay not be null
     * @return
     * @exception RuntimeException If parse string failed
     */
    public static Delay create(String delay) throws RuntimeException {
        
        Objects.requireNonNull(delay);

        if ( delay.matches("\\d+") ) {
            long min = Long.parseLong(delay, 10);
            return new Delay(min, min);
        } else if ( delay.matches("\\d+-\\d+") ) {
            String[] blocks = delay.split("-");
            long min = Long.parseLong(blocks[0], 10);
            long max = Long.parseLong(blocks[1], 10);
            return new Delay(min, max);
        }
        throw new IllegalArgumentException("delay " + delay + "无效");
    }

    public long next() {
        return ThreadLocalRandom.current().nextLong(min, max+1);
    }

    public String toText() {
        return min == max ? String.valueOf(min) : min + "-" + max;
    }    
}
