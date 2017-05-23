package es.roboticafacil.dyor.arduinosp.Utils;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import es.roboticafacil.dyor.arduinosp.Adaptors.FoundBTDevicesAdaptor;
import es.roboticafacil.dyor.arduinosp.Models.BluetoothObject;

/**
 * Created by Dragos Dunareanu on 08-May-17.
 */

public class FoundBTDevices extends ListActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothObject> arrayOfFoundBTDevices;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        displayListOfFoundDevices();
    }

    private void displayListOfFoundDevices() {
        arrayOfFoundBTDevices = new ArrayList<>();

        mBluetoothAdapter.startDiscovery();

        final BroadcastReceiver mRecivier = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                    BluetoothObject bluetoothObject = new BluetoothObject();
                    bluetoothObject.setBluetooth_name(device.getName());
                    bluetoothObject.setBluetooth_address(device.getAddress());
                    bluetoothObject.setBluetooth_state(device.getBondState());
                    bluetoothObject.setBluetooth_type(device.getType());
                    bluetoothObject.setBluetooth_uuids(device.getUuids());
                    bluetoothObject.setBluetooth_rssi(rssi);

                    arrayOfFoundBTDevices.add(bluetoothObject);

                    FoundBTDevicesAdaptor adaptor = new FoundBTDevicesAdaptor(getApplicationContext(), arrayOfFoundBTDevices);

                    setListAdapter(adaptor);
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mRecivier, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.cancelDiscovery();
    }
}
