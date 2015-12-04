package com.github.loafer.timer;

import java.util.Date;

/**
 * @author zhaojh.
 */
public class UnixTime {
    private final int value;

    public UnixTime(int value) {
        this.value = value;
    }

    public UnixTime(){
        this.value = (int) (System.currentTimeMillis()/1000L + 2208988800L);
    }

    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return new Date((value() - 2208988800L) * 1000L).toString();
    }
}
