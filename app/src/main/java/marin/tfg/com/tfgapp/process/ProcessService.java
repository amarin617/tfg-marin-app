package marin.tfg.com.tfgapp.process;

import marin.tfg.com.tfgapp.objects.Callback;
import marin.tfg.com.tfgapp.objects.Data;
import marin.tfg.com.tfgapp.storeData.CountTime;

/**
 * Created by Adrián on 03/08/2015.
 */
public class ProcessService {

    private Callback callBack;
    CountTime mTime;

    public ProcessService(Callback callBack) {
        this.callBack = callBack;
    }

    public void process(Data data, CountTime testTime) {
        mTime = testTime;
        callBack.onSuccess(doAction(data));
    }

    private Data doAction(Data data){
        mTime.start();
        JavaDummyProcess provider = new JavaDummyProcess();
        provider.start();
        return data;
    }

}
