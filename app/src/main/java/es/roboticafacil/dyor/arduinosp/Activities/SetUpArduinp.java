package es.roboticafacil.dyor.arduinosp.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import es.roboticafacil.dyor.arduinosp.Adaptors.DeviceListAdaptor;
import es.roboticafacil.dyor.arduinosp.R;

public class SetUpArduinp extends AppCompatActivity implements AdapterView.OnItemClickListener{

    BluetoothAdapter mBluetooth;
    List<BluetoothDevice> mDevices;
    DeviceListAdaptor mDevicesAdaptor;
    private ListView lvNewDevices;


    private final BroadcastReceiver mBroadcastReceiverSwitchOnOff = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetooth.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetooth.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                            Log.d("BT", "BT turned off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d("BT", "BT turning off");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d("BT", "BT turned on");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d("BT", "BT turning on");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiverSwitchDiscovery = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetooth.SCAN_MODE_CONNECTABLE_DISCOVERABLE)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, mBluetooth.ERROR);

                switch (state){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d("BT", "BT connection discoverable");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d("BT", "BT connectable");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d("BT", "BT no scan");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d("BT", "BT connecting");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d("BT", "BT connected");
                    break;
                }
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiverDiscovery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDevices.add(device);
                mDevicesAdaptor = new DeviceListAdaptor(context, R.layout.device_adaptor_view, mDevices);
                lvNewDevices.setAdapter(mDevicesAdaptor);
            }
        }
    };
    private BroadcastReceiver mBroadcastReceiverBond = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()){
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_arduinp);

        mBluetooth = BluetoothAdapter.getDefaultAdapter();

        mDevices = new ArrayList<>();
        lvNewDevices = (ListView) findViewById(R.id.lv_devices);
        lvNewDevices.setOnItemClickListener(this);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(mBroadcastReceiverBond, filter);

        Button bt = (Button) findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableDisableBT();
            }
        });
    }

    private void enableDisableBT() {
        if (mBluetooth == null){
            Log.d("BT", "Device doesn't have BT");
        }

        if (!mBluetooth.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiverSwitchOnOff, BTIntent);
        }

        if (mBluetooth.isEnabled()){
            mBluetooth.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiverSwitchOnOff, BTIntent);
        }
    }


    public void onDiscav(View view) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(intent);

        IntentFilter intentFilter = new IntentFilter(mBluetooth.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiverSwitchDiscovery, intentFilter);
    }

    public void startDisc(View view) {
        
        if (mBluetooth.isDiscovering()){
            mBluetooth.cancelDiscovery();

            checkBTPermision();

            mBluetooth.startDiscovery();
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiverDiscovery, intentFilter);
        }
        if (!mBluetooth.isDiscovering()){
            mBluetooth.startDiscovery();
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiverDiscovery, intentFilter);
        }
    }

    private void checkBTPermision() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mBluetooth.cancelDiscovery();
        mDevices.get(position).createBond();
    }
}
