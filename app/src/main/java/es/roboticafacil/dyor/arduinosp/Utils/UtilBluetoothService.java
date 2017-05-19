package es.roboticafacil.dyor.arduinosp.Utils;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dragos Dunareanu on 15-May-17.
 */

public class UtilBluetoothService extends Service {

    private static final String BROADCAST_STREAMS = "es.roboticafacil.dyor.arduinosp.broadcast_streams";
    final String TAG = "UtilBluetoothService";

    public static final String SEND_DEVICE = "send_device";

    public static final String CMD_SEARCH_DEVICES = "search_devices";
    public static final String CMD_SET_DEFAULT_DEVICE = "set_default";
    public static final String CMD_CHECK_RANGE = "check_range";
    public static final String CMD_PAIR_WITH = "pari_with";

    private SharedPreferences preferences;
    private Gson gson = new Gson();

    private BluetoothAdapter mBluetoothAdaptor = null;
    private List<BluetoothDevice> mDevices = null;
    private BluetoothDevice bluetoothDevice = null;

    private boolean inRange = false;
    private BluetoothSocket socket;
    final private Handler handler = new Handler();
    private OutputStream outputStream;
    private InputStream inStream;
    private Intent intent;

    private Runnable sendUpdates = new Runnable() {
        @Override
        public void run() {
            DisplayLoggingInfo();
            handler.postDelayed(this, 5000);
        }
    };
    private boolean btConnected = false;

    private BroadcastReceiver receiveCommands = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("testingt", "Got broadcast");
            String command = intent.getAction();
            Bundle extras = intent.getExtras();
            switch (command){
                case CMD_SEARCH_DEVICES:
                    searchDevices();
                    break;
                case CMD_SET_DEFAULT_DEVICE:
                    bluetoothDevice = (BluetoothDevice) extras.get("device");
                    Log.d("testingt", "set default: " + bluetoothDevice);
                    connectTo(bluetoothDevice);
                    preferences.edit().putString("defaultDevice", gson.toJson(bluetoothDevice)).apply();
                    break;
                case CMD_CHECK_RANGE:
                    checkBTRange((BluetoothDevice) extras.get("device"));
                    break;
                case CMD_PAIR_WITH:
                    BluetoothDevice toPair = (BluetoothDevice) extras.get("device");
                    pairWith(toPair);
                    break;
            }
        }
    };

    private void pairWith(BluetoothDevice toPair) {
        if (!mBluetoothAdaptor.getBondedDevices().contains(toPair)){
            if (checkBTRange(toPair)){
                toPair.createBond();
            }
        }
    }

    private void searchDevices() {
        checkBTAndEnable();
        mBluetoothAdaptor.startDiscovery();
        final Handler handler = new Handler();

        final BroadcastReceiver discoveryBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    Intent found = new Intent();
                    found.setAction(SEND_DEVICE);
                    found.putExtra("device", gson.toJson(foundDevice));
                    sendBroadcast(found);
                }
            }
        };
        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryBroadcast, foundFilter);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdaptor.cancelDiscovery();
                unregisterReceiver(discoveryBroadcast);
            }
        }, 1000);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences("bluetooth", MODE_PRIVATE);

        Log.d(TAG, "Service started");
        mBluetoothAdaptor = BluetoothAdapter.getDefaultAdapter();
        mDevices = new ArrayList<>(mBluetoothAdaptor.getBondedDevices());
        bluetoothDevice = gson.fromJson(preferences.getString("defaultDevice", null), BluetoothDevice.class);

        setupBT();
        startListeningForBTConnection();
        startBroadcast();

        IntentFilter commandsFilter = new IntentFilter(UtilBluetoothService.CMD_SET_DEFAULT_DEVICE);
        this.registerReceiver(receiveCommands, commandsFilter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiveCommands);
        mBluetoothAdaptor.cancelDiscovery();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void checkBTAndEnable() {

        if (!mBluetoothAdaptor.isEnabled()) {
            Log.d(TAG, "Bluetooth is disable... ENABLING");
            mBluetoothAdaptor.enable();
        }

        if (mBluetoothAdaptor == null) {
            Log.d(TAG, "There is no Bluetooth Adaptor");
        }
    }

    private boolean checkBTRange(final BluetoothDevice mDevice) {
        Log.d(TAG, "Checking if " + mDevice + " is in range...");
        mBluetoothAdaptor.startDiscovery();
        Log.d(TAG, "Discovery started");

        final BroadcastReceiver discoveryBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Broadcast received");
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)){
                    Log.d(TAG, "Testing device");
                    BluetoothDevice foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (foundDevice == mDevice){
                        Log.d(TAG, foundDevice + " is the same device with "+mDevice);
                        inRange = true;
                        unregisterReceiver(this);
                    } else Log.d(TAG, foundDevice + " isn't the same device with "+mDevice);
                }
                Log.d(TAG, "Didn't find the device");
            }
        };
        IntentFilter discoveryFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryBroadcast, discoveryFilter);

        return inRange;
    }

    private void connectTo(final BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "Connecting to: " + bluetoothDevice);
        Runnable connect = new Runnable() {
            @Override
            public void run() {
                ParcelUuid[] uuids = bluetoothDevice.getUuids();
                try {
                    Log.d(TAG, "Trying to connect...");
                    socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                    Log.d(TAG, "Socket connection created");
                    socket.connect();
                    if (socket.isConnected()) {
                        Log.d(TAG, "Socket connected");
                        outputStream = socket.getOutputStream();
                        inStream = socket.getInputStream();
                    } else Log.d(TAG, "Something went wrong with socket connection");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Something went wrong with socket connection");
                }
                Log.d(TAG, "Connection finished");
            }
        };
        connect.run();
    }

    public void setupBT(){
        Log.d(TAG, "Setting up bluetooth");
        if (!btConnected){
            Log.d(TAG, "Bluetooth connection: " + btConnected);
            Log.d(TAG, "Checking BT enable...");
            checkBTAndEnable();
            Log.d(TAG, "BT is for sure on");
            if (bluetoothDevice != null) {
                connectTo(bluetoothDevice);
            } else Log.d(TAG, "No default device set");
        } else Log.d(TAG, "Already connected");

    }

    private void startListeningForBTConnection() {
        final BroadcastReceiver bState = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)){
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    switch (state){
                        case BluetoothAdapter.STATE_CONNECTED:
                            btConnected = true;
                            Log.d(TAG, "Connected");
                            break;
                        case BluetoothAdapter.STATE_DISCONNECTED:
                            btConnected = false;
                            break;
                        default:
                            break;
                    }
                }
            }
        };
        IntentFilter connectedFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bState, connectedFilter);
        Log.d(TAG, "Listening to bt connection");
    }

    private void startBroadcast() {
        intent = new Intent(BROADCAST_STREAMS);
        handler.removeCallbacks(sendUpdates);
        handler.postDelayed(sendUpdates, 1000);
    }

    private void DisplayLoggingInfo() {
        intent.putExtra("connected", btConnected);
        if (btConnected) {
            intent.putExtra("outputStream", (Parcelable) outputStream);
            intent.putExtra("inStream", (Parcelable) inStream);
        }
        sendBroadcast(intent);
    }
}
