package es.roboticafacil.dyor.arduinosp.Activities.Fragments;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.roboticafacil.dyor.arduinosp.Adaptors.DeviceListAdaptor;
import es.roboticafacil.dyor.arduinosp.R;
import es.roboticafacil.dyor.arduinosp.Utils.BluetoothConnectTask;

/**
 * Created by Dragos Dunareanu on 11-May-17.
 */

public class BluetoothDevicesListAlreadyPaired extends DialogFragment implements AdapterView.OnItemClickListener {

    private String TAG = "Bluetooth Fragment";

    private BluetoothAdapter mBluetooth;
    private List<BluetoothDevice> mDevices;
    private DeviceListAdaptor mDevicesAdaptor;
    private ListView lvDevices;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inStream;

    private ProgressDialog mProgressDialog;
    private boolean bluetoothConnected;
    private Runnable connBT;
    private Runnable sendBT;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_bt_devices, container);


        getDialog().setTitle("Select your arduino: ");

        mBluetooth = BluetoothAdapter.getDefaultAdapter();
        mDevices = new ArrayList<>();

        assert container != null;
        lvDevices = (ListView) rootView.findViewById(R.id.lv_list_devices);
        lvDevices.setOnItemClickListener(this);

        mDevices = new ArrayList<>(mBluetooth.getBondedDevices());
        Log.d("BluetoothDebug", "Devices: " + mDevices);
        mDevicesAdaptor = new DeviceListAdaptor(getContext(), R.layout.device_adaptor_view, mDevices);
        lvDevices.setAdapter(mDevicesAdaptor);

        Log.d("BluetoothDebug", "Created Fragment");

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int p = position;

        BluetoothConnectTask task = new BluetoothConnectTask(mDevices, p, new Runnable() {
            @Override
            public void run() {
                showProgressDialog();
            }
        }, new Runnable() {
            @Override
            public void run() {
                hideProgressDialog();
            }
        });
        task.execute();

//        connectToArduino(p);
//        Log.d("BluetoothDebug", "conneted to ard");
//        sendBytesToArduino("wow, message", p);
    }

    private void connectToArduino(int p) {
        final BluetoothDevice device = mDevices.get(p);

        connBT = new Runnable() {
            @Override
            public void run() {
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
                        bluetoothConnected = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else Log.d("BluetoothDebug", "bt is null");
            }
        };
    }

    private void sendBytesToArduino(final String s, final int p) {

        Log.d("BluetoothDebug", "seding data");
        sendBT = new Runnable() {
            @Override
            public void run() {

            }
        };
        if (mDevices.get(p) != null) {
            try {

                outputStream.write(s.getBytes());
                inStream.close();
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else Log.d("BluetoothDebug", "bt is null");


    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
