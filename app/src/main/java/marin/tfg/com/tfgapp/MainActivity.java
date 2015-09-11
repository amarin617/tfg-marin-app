package marin.tfg.com.tfgapp;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Random;

import marin.tfg.com.tfgapp.algorithm.Provider;
import marin.tfg.com.tfgapp.objects.Callback;
import marin.tfg.com.tfgapp.objects.Data;
import marin.tfg.com.tfgapp.objects.Types;
import marin.tfg.com.tfgapp.restService.RestService;
import marin.tfg.com.tfgapp.storeData.CountTime;
import marin.tfg.com.tfgapp.storeData.SaveData;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = "MainActivity";

    private RestService restService;
    private Provider mProvider;
    private Intent batteryStatus;
    private TextView info;
    private TextView textView2,textView3;
    private Context mContext;
    ImageView ivThumbnailPhoto;
    private EditText num_of_tests;
    private ToggleButton toggle;
    private boolean runOnServer;

    private int NUMBERTEST = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        restService = new RestService(this);
        info = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);

        toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    runOnServer = true;
                } else {
                    // The toggle is disabled
                    runOnServer = false;
                }
            }
        });

        num_of_tests = (EditText) findViewById(R.id.editText);
        num_of_tests.setText("" + NUMBERTEST);


        checkConnection();
        testConnectionButton();
        loadLibraryButtons();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = mContext.registerReceiver(null, ifilter);

        ivThumbnailPhoto = (ImageView) findViewById(R.id.imageView);

        testRTT();
    }

    private int signalStrength;

    public int getSignalStrength() {
        return signalStrength;
    }

    private void testRTT() {
        final Button bTestRTT = (Button) findViewById(R.id.button4);
        bTestRTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView3.setText("IN PROGRESS + NUMBERTEST");
                // start count test
                final CountTime totalTestTime = new CountTime();
                //new Random().nextBytes(RANDOMBYTE);
                totalTestTime.start();
                SaveData.startFileAndTest();
                final CountTime[] testTime = {null};

                NUMBERTEST = Integer.parseInt(num_of_tests.getText().toString());

                final boolean[] control = {true};
                isLast = false;

                mProvider = new Provider(mContext, new Callback() {
                    @Override
                    public void onSuccess(Data response) {
                        Log.d(TAG, response.getType().toString() + " Success with " + response.toString());
                        testTime[0].setPhoneInfo(mProvider.getStatus());
                        SaveData.saveToFile("", testTime[0].stop(), testTime[0].getInfo(), getSignalStrength());
                        control[0] = true;
                        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                        batteryStatus = mContext.registerReceiver(null, ifilter);
                        if(isLast){
                            saveFinal();
                        }
                    }

                    @Override
                    public void onFail(Data response) {
                        Log.d(TAG, response.getType().toString() + " FAIL!");
                        control[0] = true;
                    }
                }, true);

                new Thread(new Runnable() {
                    public void run() {
                        // while
                        int i = 0;
                        while (i < NUMBERTEST) {
                            while (!control[0]) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            control[0] = false;
                            // start count test
                            testTime[0] = new CountTime();
                            Data mData = new Data(Types.PROCESS, new byte[0]);
                            final int j = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textView3.setText("IN PROGRESS \n\r TEST " + j + " OF " + NUMBERTEST);
                                }
                            });
                            mProvider.doAction(mData, testTime[0]);
                            i++;

                            if(i == NUMBERTEST-1){
                                isLast = true;
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView3.setText("FINISHED " + NUMBERTEST + "TESTS");
                            }
                        });
                    }
                }).start();

            }
        });

    }

    private void testConnectionButton() {
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restService.postData(new Data(Types.RTT, new byte[]{0x00, 0x01}), new Callback() {
                            @Override
                            public void onSuccess(Data response) {
                                textView2.setText("Connected");
                            }

                            @Override
                            public void onFail(Data response) {
                                textView2.setText("NOT Connected");
                            }
                        }
                );
            }
        });
    }

    private void checkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            info.setText("Network is Available");
        } else {
            info.setText("Network is NOT Available");
        }
    }

    private byte[] RANDOMBYTE = new byte[1024];

    private boolean isLast = false;

    private void saveFinal(){
        SaveData.endFileAndTest();
    }

    private void loadLibraryButtons() {

        /**
         * Test B
         */
        final Button bTestB = (Button) findViewById(R.id.button3);
        bTestB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView3.setText("IN PROGRESS + NUMBERTEST");
                // start count test
                final CountTime totalTestTime = new CountTime();
                new Random().nextBytes(RANDOMBYTE);
                totalTestTime.start();
                SaveData.startFileAndTest();
                final CountTime[] testTime = {null};

                NUMBERTEST = Integer.parseInt(num_of_tests.getText().toString());

                final boolean[] control = {true};
                isLast = false;

                mProvider = new Provider(mContext, new Callback() {
                    @Override
                    public void onSuccess(Data response) {
                        Log.d(TAG, response.getType().toString() + " Success with " + response.toString());
                        testTime[0].setPhoneInfo(mProvider.getStatus());
                        SaveData.saveToFile("", testTime[0].stop(), testTime[0].getInfo(), getSignalStrength());
                        control[0] = true;
                        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                        batteryStatus = mContext.registerReceiver(null, ifilter);
                    }

                    @Override
                    public void onFail(Data response) {
                        Log.d(TAG, response.getType().toString() + " FAIL!");
                        control[0] = true;
                    }
                }, runOnServer);

                new Thread(new Runnable() {
                    public void run() {
                        // while
                        int i = 0;
                        while (i < NUMBERTEST) {
                            while (!control[0]) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            control[0] = false;
                            // start count test
                            testTime[0] = new CountTime();
                            Data mData = new Data(Types.PROCESS, RANDOMBYTE);
                            final int j = i;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textView3.setText("IN PROGRESS \n\r TEST " + j + " OF " + NUMBERTEST);
                                }
                            });
                            mProvider.doAction(mData, testTime[0]);
                            i++;

                            if(i == NUMBERTEST-1){
                                isLast = true;
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView3.setText("FINISHED " + NUMBERTEST + "TESTS");
                            }
                        });
                    }
                }).start();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
