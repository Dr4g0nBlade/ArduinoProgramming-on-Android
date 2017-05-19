package es.roboticafacil.dyor.arduinosp.Utils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dragos Dunareanu on 15-May-17.
 */

public class BluetoothSendTask extends AsyncTask<Void, Void, Void> {
    private List<BluetoothDevice> mDevices;
    private OutputStream outputStream;
    private InputStream inStream;
    private BluetoothSocket socket;
    private int p;
    private Runnable hide;

    public BluetoothSendTask(List<BluetoothDevice> mDevices, OutputStream outputStream, InputStream inStream, BluetoothSocket socket, int p, Runnable hide) {
        this.mDevices = mDevices;
        this.outputStream = outputStream;
        this.inStream = inStream;
        this.socket = socket;
        this.p = p;
        this.hide = hide;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (mDevices.get(p) != null) {
            try {
                String s = "wow, you recivead a message";
                outputStream.write(s.getBytes());
                inStream.close();
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else Log.d("BluetoothDebug", "bt is null");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        hide.run();
    }
}
