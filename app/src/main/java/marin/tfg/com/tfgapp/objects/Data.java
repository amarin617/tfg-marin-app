package marin.tfg.com.tfgapp.objects;

import java.util.Arrays;

/**
 * Created by Adrian on 27/05/2015.
 */
public class Data {

    private Types type;
    private byte[] data;

    public Data(Types type, byte[] data) {
        super();
        this.type = type;
        this.data = data;
    }

    public Types getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Data [type=" + type + ", data=" + data.length + "]";
    }
}
