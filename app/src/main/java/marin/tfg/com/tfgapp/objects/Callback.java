package marin.tfg.com.tfgapp.objects;

/**
 * Created by Adrian on 01/06/2015.
 */
public interface Callback {
    void onSuccess(Data response);
    void onFail(Data response);
}
