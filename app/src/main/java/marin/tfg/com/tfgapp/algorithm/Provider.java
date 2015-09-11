package marin.tfg.com.tfgapp.algorithm;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import marin.tfg.com.tfgapp.objects.Callback;
import marin.tfg.com.tfgapp.objects.Data;
import marin.tfg.com.tfgapp.objects.PhoneStats;
import marin.tfg.com.tfgapp.process.ProcessService;
import marin.tfg.com.tfgapp.restService.RestService;
import marin.tfg.com.tfgapp.storeData.CountTime;

/**
 * Created by Adrian on 03/06/2015.
 */
public class Provider {

    public static final String TAG = "AlgorithmProvider";

    private enum STATE {
        LOCAL,
        SERVER
    }

    private STATE mState;
    private Callback mCallback;
    private RestService restService;
    private ProcessService mProcess;
    private Context mContext;

    private marin.tfg.com.tfgapp.phoneData.Provider mProvider;

    public Provider(Context context, Callback callback, boolean runOnServer) {
        mCallback = callback;
        mContext = context;
        restService = new RestService(context);
        mProcess = new ProcessService(mCallback);
        if (runOnServer){
            mState = STATE.SERVER;
        } else {
            mState = STATE.LOCAL;
        }
    }

    public void doAction(Data mData) {
        mState = checkStatus();
        Log.d(TAG, "Running on " + mState.toString());
        switch (mState) {
            case LOCAL:
                // process with library
                break;
            case SERVER:
                // process with server
                restService.postData(mData, mCallback);
                break;
        }
    }


    public void doAction(Data mData, CountTime testTime) {
        checkStatus();
        Log.d(TAG, "Running on " + mState.toString());
        switch (mState) {
            case LOCAL:
                testTime.setPhoneInfo(mProvider.getPhoneStatus(mContext));
                // process with library
                mProcess.process(mData,testTime);
                break;
            case SERVER:
                testTime.setPhoneInfo(mProvider.getPhoneStatus(mContext));
                // process with server
                restService.postData(mData, mCallback, testTime);
                break;
        }
    }

    private STATE checkStatus() {
        mProvider = new marin.tfg.com.tfgapp.phoneData.Provider();
        return decisor(mProvider.getPhoneStatus(mContext));
    }

    public PhoneStats getStatus() {
        mProvider = new marin.tfg.com.tfgapp.phoneData.Provider();
        return mProvider.getPhoneStatus(mContext);
    }

    private STATE decisor(PhoneStats info) {
        // TODO: Main decisor
        return STATE.LOCAL;
    }

}