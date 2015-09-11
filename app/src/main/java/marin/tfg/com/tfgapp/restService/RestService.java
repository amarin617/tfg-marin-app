package marin.tfg.com.tfgapp.restService;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import marin.tfg.com.tfgapp.objects.Callback;
import marin.tfg.com.tfgapp.objects.Data;
import marin.tfg.com.tfgapp.objects.Types;
import marin.tfg.com.tfgapp.storeData.CountTime;

/**
 * Created by Adrian on 31/05/2015.
 */
public class RestService {

    String TAG = RestService.class.getName();

    private RestUtils restUtils;
    private Callback callBack;

    CountTime mTime;

    public RestService(Context context) {
        restUtils = new RestUtils(context);
    }

    public void postData(Data toSend, Callback callBack) {
        this.callBack = callBack;
        mTime = new CountTime();
        Log.d(TAG, "Request to Send " + toSend.toString());
        new RestTask().execute(toSend);
    }

    public void postData(Data toSend, Callback callBack, CountTime testTime) {
        this.callBack = callBack;
        Log.d(TAG, "Request to Send " + toSend.toString());
        mTime = testTime;
        new RestTask().execute(toSend);
    }

    private class RestTask extends AsyncTask<Data, Void, String> {
        @Override
        protected String doInBackground(Data... urls) {

            try {
                return restUtils.downloadUrl(urls[0], mTime);
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject response = new JSONObject(result);
                Data toResponse = new Data(Enum.valueOf(Types.class,
                        (String) response.get("type")),
                        Base64.decode((String) response.get("data"),
                        Base64.DEFAULT));
                callBack.onSuccess(toResponse);
            } catch (JSONException e) {
                callBack.onFail(new Data(Types.ERROR, null));
            }
        }
    }
}

