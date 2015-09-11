package marin.tfg.com.tfgapp.storeData;

import java.math.BigDecimal;

import marin.tfg.com.tfgapp.objects.PhoneStats;

/**
 * Created by Adrián on 16/07/2015.
 */
public class CountTime {

    long tStart;

    private PhoneStats mInfo;
    public CountTime() {
    }

    public double start() {
        tStart = System.nanoTime();
        return tStart;
    }

public void setPhoneInfo(PhoneStats info) {
    mInfo = info;
}

    public PhoneStats getInfo() {
        return mInfo;
    }

    public long stop() {
        long tEnd = System.nanoTime();
        return ((tEnd - tStart) / 1000000);
    }
}
