package marin.tfg.com.tfgapp.phoneData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import marin.tfg.com.tfgapp.objects.PhoneStats;
import marin.tfg.com.tfgapp.phoneData.entities.BatteryPlugged;
import marin.tfg.com.tfgapp.phoneData.entities.Network;

/**
 * Created by Adri�n on 08/06/2015.
 */
public class Provider {

    private boolean isCharging, acCharge;

    public Provider() {
    }

    public PhoneStats getPhoneStatus(Context context) {
        return getData(context);
    }

    private PhoneStats getData(Context mContext) {
        return new PhoneStats(mContext, new boolean[] {isCharging,acCharge});
    }

    public class PowerConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        }
    }
}
