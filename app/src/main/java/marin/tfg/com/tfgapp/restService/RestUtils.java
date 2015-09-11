package marin.tfg.com.tfgapp.restService;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import marin.tfg.com.tfgapp.objects.Data;
import marin.tfg.com.tfgapp.storeData.CountTime;

/**
 * Created by Adrian on 31/05/2015.
 */
public class RestUtils {

    private Context mContext;

    public RestUtils(Context context) {
        this.mContext = context;
    }

    private String getURL() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String address = sharedPref.getString("address_settings", "");
        String port = sharedPref.getString("port_settings", "");
        return "http://" + address + ":" + port + "/";
    }

    public String downloadUrl(Data toSend) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(getURL());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoInput(true);
            JSONObject JSONtoSend = new JSONObject();
            JSONtoSend.put("type", toSend.getType().toString());
            JSONtoSend.put("data", Base64.encodeToString(toSend.getData(), 0));
            OutputStream os = conn.getOutputStream();
            os.write(JSONtoSend.toString().getBytes("UTF-8"));
            os.close();
            conn.connect();
            is = conn.getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return null;
    }

    public String downloadUrl(Data toSend, CountTime testTime) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(getURL());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoInput(true);
            JSONObject JSONtoSend = new JSONObject();
            JSONtoSend.put("type", toSend.getType().toString());
            JSONtoSend.put("data", Base64.encodeToString(toSend.getData(), 0));
            OutputStream os = conn.getOutputStream();
            testTime.start();
            os.write(JSONtoSend.toString().getBytes("UTF-8"));
            os.close();
            conn.connect();
            is = conn.getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return null;
    }

}
