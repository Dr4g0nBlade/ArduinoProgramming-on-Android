package es.roboticafacil.dyor.arduinosp.Activities.Fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import es.roboticafacil.dyor.arduinosp.R;

/**
 * Created by Dragos Dunareanu on 11-May-17.
 */

public class BluetoothDevicesListPairing extends DialogFragment implements AdapterView.OnItemClickListener {

    private BluetoothAdapter mBluetooth;
    private List<BluetoothDevice> mDevices;
    private DeviceListAdaptor mDevicesAdaptor;
    private ListView lvDevices;
    private BroadcastReceiver mBroadcastReceiverDiscovery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDevices.add(device);
                mDevicesAdaptor = new DeviceListAdaptor(context, R.layout.device_adaptor_view, mDevices);
                lvDevices.setAdapter(mDevicesAdaptor);
            }
        }
    };
    private BroadcastReceiver mBroadcastReceiverBond = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDED:

                        break;
                    case BluetoothDevice.BOND_BONDING:

                        break;
                    case BluetoothDevice.BOND_NONE:

                        break;
                    case BluetoothDevice.PAIRING_VARIANT_PIN:
                        break;
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_bt_devices, container);


        getDialog().setTitle("Bluetooth Devices discovered");

        mBluetooth = BluetoothAdapter.getDefaultAdapter();
        mDevices = new ArrayList<>();

        assert container != null;
        lvDevices = (ListView) rootView.findViewById(R.id.lv_list_devices);
        lvDevices.setOnItemClickListener(this);

        if (mBluetooth.isDiscovering()) {
            mBluetooth.cancelDiscovery();

            mBluetooth.startDiscovery();
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getActivity().registerReceiver(mBroadcastReceiverDiscovery, intentFilter);

        }
        if (!mBluetooth.isDiscovering()) {
            mBluetooth.startDiscovery();
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getActivity().registerReceiver(mBroadcastReceiverDiscovery, intentFilter);
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(mBroadcastReceiverBond, filter);

        searchDevices();
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mBluetooth.cancelDiscovery();
        mDevices.get(position).createBond();
    }

    private void searchDevices() {
        if (mBluetooth.isDiscovering()) {
            mBluetooth.cancelDiscovery();


            mBluetooth.startDiscovery();
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getActivity().registerReceiver(mBroadcastReceiverDiscovery, intentFilter);
        }
        if (!mBluetooth.isDiscovering()) {
            mBluetooth.startDiscovery();
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getActivity().registerReceiver(mBroadcastReceiverDiscovery, intentFilter);
        }
    }

}
