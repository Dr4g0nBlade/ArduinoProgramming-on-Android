package es.roboticafacil.dyor.arduinosp.Utils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dragos Dunareanu on 15-May-17.
 */

public class BluetoothConnectTask extends AsyncTask<Void, Void, Void> {
    private List<BluetoothDevice> mDevices;
    private OutputStream outputStream;
    private InputStream inStream;
    private BluetoothSocket socket;
    private int p;
    private Runnable show;
    private Runnable hide;

    public BluetoothConnectTask(List<BluetoothDevice> mDevices, int p, Runnable show, Runnable hide) {
        this.mDevices = mDevices;
        this.p = p;
        this.show = show;
        this.hide = hide;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        show.run();
    }



    @Override
    protected Void doInBackground(Void... params) {
        final BluetoothDevice device = mDevices.get(p);
        Log.d("BluetoothDebug", "Devices is: " + device);

        if (device != null) {
            ParcelUuid[] uuids = device.getUuids();
            Log.d("BluetoothDebug", "Device: " + device + " Uuids: " + Arrays.toString(uuids));
            try {
                socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                Log.d("BluetoothDebug", "Socket: " + socket);
                socket.connect();
                outputStream = socket.getOutputStream();
                Log.d("BluetoothDebug", "output: " + outputStream);
                inStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else Log.d("BluetoothDebug", "bt is null");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        BluetoothSendTask task = new BluetoothSendTask(mDevices,outputStream,inStream,socket,p, hide);
        task.execute();
    }
}
