package marin.tfg.com.tfgapp.storeData;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import marin.tfg.com.tfgapp.MainActivity;
import marin.tfg.com.tfgapp.objects.PhoneStats;

/**
 * Created by Adri�n on 16/07/2015.
 */
public class SaveData {

    private static File file;

    public static boolean startFileAndTest() {
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + "/test");
        directory.mkdirs();
        String filename = new SimpleDateFormat("yyyyMMdd_HH_mm_ss").format(new Date()) + ".txt";
        file = new File(directory, filename);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        try {
            osw.write("TIME | BAT | NETWORK | PLUGGED | SIGNAL LEVEL\r\n");
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public static int[] endFileAndTest() {
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        int media = getMedia();
        int battery = getBattery();
        try {
            osw.write("MEDIA: "+ media + " BATTERY: " + battery + "\r\n");
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new int[] {media,battery};
    }

    private static List<Long> mediaList = new ArrayList<>();
    private static List<Integer> mediaBattery = new ArrayList<>();

    private static int getMedia(){
        return Integer.parseInt(""+ calculateMedia());
    }

    private static long calculateMedia(){
        long result = 0;
        for(Long ln: mediaList){
            result=result+ln;
        }
        return result/mediaList.size();
    }

    private static int getBattery() {
        return Integer.parseInt("" + calculateBattery());
    }

    private static long calculateBattery() {
        long max = Collections.max(mediaBattery);
        long min = Collections.min(mediaBattery);
        return (max-min)/mediaBattery.size();
    }

    private static void addData(PhoneStats data, long time){
        mediaList.add(time);
        mediaBattery.add(data.getmBatteryLevel());
    }

    public static boolean saveToFile(String prefix, long time, PhoneStats data, int signal) {
        String datatest;
        addData(data, time);
        if(data!=null) {
            datatest = prefix + time + data.toString() + signal;
        }else{
            // start or final test
            datatest = prefix + time;
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        try {
            osw.write(datatest + "\r\n");
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
