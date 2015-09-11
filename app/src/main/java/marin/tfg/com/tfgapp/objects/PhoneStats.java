package marin.tfg.com.tfgapp.objects;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.provider.BaseColumns;
import android.provider.CallLog;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import marin.tfg.com.tfgapp.phoneData.entities.BatteryPlugged;
import marin.tfg.com.tfgapp.phoneData.entities.Network;

/**
 * Created by Adrian on 14/06/2015.
 */
public class PhoneStats {
    public BatteryPlugged mPlugged;
    public Network mNetwork;
    public int mBatteryLevel;
    public int mLoadCPU;
    private int mSignal;



    private String cmd = "su -c dumpsys batterystats marin.tfg.com.tfgapp";

    private boolean isCharging, acCharge;

    private Context mContext;

    public PhoneStats(Context context, boolean[] data) {
        mContext = context;
        data[0] = isCharging;
        data[1] = acCharge;
        processData();
    }

    private void processData() {
        processBatteryLevel();
        processBattery();
        processNetwork();
        processLoadCPU();
        processSignal();
    }

    private void processSignal() {
        switch(getmNetwork()){
            case WIFI: getWifiSignal();
                break;
            default: getGsmSignal();
                break;
        }
    }

    private void getWifiSignal() {
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        setmSignal(wifiManager.getConnectionInfo().getRssi());
    }
    public class myPhoneStateListener extends PhoneStateListener {
        public int signalStrengthValue;

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            if (signalStrength.isGsm()) {
                if (signalStrength.getGsmSignalStrength() != 99)
                    signalStrengthValue = signalStrength.getGsmSignalStrength() * 2 - 113;
                else
                    signalStrengthValue = signalStrength.getGsmSignalStrength();
            } else {
                signalStrengthValue = signalStrength.getCdmaDbm();
            }
        }
    }

    TelephonyManager telephonyManager;
    //myPhoneStateListener psListener;

    private void getGsmSignal() {

        //psListener = new myPhoneStateListener();
        telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
//        telephonyManager.listen(new PhoneStateReceiver((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE), mContext),PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);


        //TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);


        List<CellInfo> list = telephonyManager.getAllCellInfo();
        CellLocation cLocation;
        if(list==null){
            cLocation = telephonyManager.getCellLocation();
        }
        if (list != null) {
            if (list.get(0) instanceof CellInfoGsm) {
                CellInfoGsm cellinfogsm = (CellInfoGsm) list.get(0);
                CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
                setmSignal(cellSignalStrengthGsm.getDbm());
            }
            if (list.get(0) instanceof CellInfoLte) {
                CellInfoLte cellInfoLte = (CellInfoLte) list.get(0);
                CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                setmSignal(cellSignalStrengthLte.getDbm());
            }
            if (list.get(0) instanceof CellInfoCdma) {
                CellInfoCdma cellInfoCdma = (CellInfoCdma) list.get(0);
                CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                setmSignal(cellSignalStrengthCdma.getDbm());
            }
            if (list.get(0) instanceof CellInfoWcdma) {
                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) list.get(0);
                CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                setmSignal(cellSignalStrengthWcdma.getDbm());
            }
        }
    }



    private void processLoadCPU() {
        // TODO: Check CPU Load
        //executeCommand();
        setmLoadCPU(45);
    }

    private String executeCommand() {
        try {
            Process su = Runtime.getRuntime().exec(cmd);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(su.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line + "\n");
            }
            return log.toString();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    private void processNetwork() {
        setmNetwork(networkType((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE),
                ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo()));
    }

    public Network networkType(TelephonyManager teleMan, NetworkInfo active_network) {

        if (active_network != null && active_network.isConnectedOrConnecting()) {
            if (active_network.getType() == ConnectivityManager.TYPE_WIFI) {
                return Network.WIFI;
            } else if (active_network.getType() == ConnectivityManager.TYPE_MOBILE) {
                int networkType = teleMan.getNetworkType();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_1xRTT: return Network.RTT;
                    case TelephonyManager.NETWORK_TYPE_CDMA: return Network.CDMA;
                    case TelephonyManager.NETWORK_TYPE_EDGE: return Network.EDGE;
                    case TelephonyManager.NETWORK_TYPE_EHRPD: return Network.eHRPD;
                    case TelephonyManager.NETWORK_TYPE_EVDO_0: return Network.EVDOrev0;
                    case TelephonyManager.NETWORK_TYPE_EVDO_A: return Network.EVDOrevA;
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: return Network.EVDOrevB;
                    case TelephonyManager.NETWORK_TYPE_GPRS: return Network.GPRS;
                    case TelephonyManager.NETWORK_TYPE_HSDPA: return Network.HSDPA;
                    case TelephonyManager.NETWORK_TYPE_HSPA: return Network.HSPA;
                    case TelephonyManager.NETWORK_TYPE_HSPAP: return Network.HSPAplus;
                    case TelephonyManager.NETWORK_TYPE_HSUPA: return Network.HSUPA;
                    case TelephonyManager.NETWORK_TYPE_IDEN: return Network.iDen;
                    case TelephonyManager.NETWORK_TYPE_LTE: return Network.LTE;
                    case TelephonyManager.NETWORK_TYPE_UMTS: return Network.UMTS;
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN: return checkWifi(active_network);
                }
                return Network.UNKNOWN;
            }
        }
        return Network.NETWORK_UNAVAILABLE;


//        int networkType = teleMan.getNetworkType();
//        switch (networkType) {
//            case TelephonyManager.NETWORK_TYPE_1xRTT: return Network.RTT;
//            case TelephonyManager.NETWORK_TYPE_CDMA: return Network.CDMA;
//            case TelephonyManager.NETWORK_TYPE_EDGE: return Network.EDGE;
//            case TelephonyManager.NETWORK_TYPE_EHRPD: return Network.eHRPD;
//            case TelephonyManager.NETWORK_TYPE_EVDO_0: return Network.EVDOrev0;
//            case TelephonyManager.NETWORK_TYPE_EVDO_A: return Network.EVDOrevA;
//            case TelephonyManager.NETWORK_TYPE_EVDO_B: return Network.EVDOrevB;
//            case TelephonyManager.NETWORK_TYPE_GPRS: return Network.GPRS;
//            case TelephonyManager.NETWORK_TYPE_HSDPA: return Network.HSDPA;
//            case TelephonyManager.NETWORK_TYPE_HSPA: return Network.HSPA;
//            case TelephonyManager.NETWORK_TYPE_HSPAP: return Network.HSPAplus;
//            case TelephonyManager.NETWORK_TYPE_HSUPA: return Network.HSUPA;
//            case TelephonyManager.NETWORK_TYPE_IDEN: return Network.iDen;
//            case TelephonyManager.NETWORK_TYPE_LTE: return Network.LTE;
//            case TelephonyManager.NETWORK_TYPE_UMTS: return Network.UMTS;
//            case TelephonyManager.NETWORK_TYPE_UNKNOWN: return checkWifi(active_network);
//        }
//        return Network.UNKNOWN;
    }
    private Network checkWifi(NetworkInfo active_network) {
        if (active_network != null && active_network.isConnectedOrConnecting()) {
            if (active_network.getType() == ConnectivityManager.TYPE_WIFI) {
                return Network.WIFI;
            } else if (active_network.getType() == ConnectivityManager.TYPE_MOBILE) {
                return Network.UNKNOWN;
            }
        }
        return Network.NETWORK_UNAVAILABLE;
    }


    private void processBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float)scale;
        setmBatteryLevel(Math.round(batteryPct * 100));
    }

    private void processBattery() {
        // TODO: Chaeck battery isCharging, usbCharge, acCharge;
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        if(isCharging) {
            if(acCharge) {
                setmPlugged(BatteryPlugged.CHARGING_AC);
            } else {
                setmPlugged(BatteryPlugged.CHARGING_USB);
            }
        } else {
            setmPlugged(BatteryPlugged.DISCHARGING);
        }
    }

    public int getmBatteryLevel() {
        return mBatteryLevel;
    }

    private void setmBatteryLevel(int mBatteryLevel) {
        this.mBatteryLevel = mBatteryLevel;
    }

    public int getmLoadCPU() {
        return mLoadCPU;
    }

    private void setmLoadCPU(int mLoadCPU) {
        this.mLoadCPU = mLoadCPU;
    }

    public BatteryPlugged getmPlugged() {
        return mPlugged;
    }

    private void setmPlugged(BatteryPlugged mPlugged) {
        this.mPlugged = mPlugged;
    }

    public Network getmNetwork() {
        return mNetwork;
    }

    private void setmNetwork(Network mNetwork) {
        this.mNetwork = mNetwork;
    }

    public int getmSignal() {
        return mSignal;
    }

    private void setmSignal(int signal) {
        mSignal = signal;
    }

    @Override
    public String toString() {
        return " "+getmBatteryLevel()+" "+getmNetwork()
                +" "+getmPlugged()+" "+getmSignal();
    }
}
