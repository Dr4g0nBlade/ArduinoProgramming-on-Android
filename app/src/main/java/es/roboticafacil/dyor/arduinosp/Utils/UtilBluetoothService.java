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
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
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
    public static final String CMD_PAIR_WITH = "pair_with";
    public static final String CMD_SEND = "send";

    private SharedPreferences preferences;

    private BluetoothAdapter mBluetoothAdaptor = null;
    private List<BluetoothDevice> mDevices = null;
    private String defaultDeviceAddress = null;

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
            String command = intent.getAction();
            Bundle extras = intent.getExtras();
            Log.d(TAG, "Got command: " + command);
            switch (command){
                case CMD_SEARCH_DEVICES:
                    searchDevices();
                    break;
                case CMD_SET_DEFAULT_DEVICE:
                    defaultDeviceAddress = extras.getString("default_device_address");
                    Log.d(TAG, "set default: " + defaultDeviceAddress);
                    connectTo(defaultDeviceAddress);
                    preferences.edit().putString("default_device_address", defaultDeviceAddress).apply();
                    break;
                case CMD_PAIR_WITH:
                    String toPair = extras.getString("device_address");
                    pairWith(toPair);
                    break;
//                case CMD_SEND:
//                    String data = extras.getString("data");
//                    writeToDevice(data);
//                    break;
            }
        }
    };

//    private void writeToDevice(String data) {
//        if (btConnected){
//            if (socket.isConnected()){
//                try {
//                    outputStream.write(data.getBytes());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    final BroadcastReceiver discoveryBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "I found something");
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Intent found = new Intent();
                found.setAction(SEND_DEVICE);
                found.putExtra("device_address", foundDevice.getAddress());
                getApplicationContext().sendBroadcast(found);
            }
        }
    };

    private void pairWith(String  toPairAddress) {
        BluetoothDevice toPair = mBluetoothAdaptor.getRemoteDevice(toPairAddress);
        if (!mBluetoothAdaptor.getBondedDevices().contains(toPair)){
            toPair.createBond();
        }
    }

    private void searchDevices() {
        checkBTAndEnable();
        Log.d(TAG, "Starting discovery");
        mBluetoothAdaptor.startDiscovery();
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Canceling discovery");
                mBluetoothAdaptor.cancelDiscovery();

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
        defaultDeviceAddress = preferences.getString("default_device_address", null);

        setupBT();
        startListeningForBTConnection();
        startBroadcast();

        IntentFilter commandsFilter = new IntentFilter();
        commandsFilter.addAction(CMD_SEARCH_DEVICES);
        commandsFilter.addAction(CMD_PAIR_WITH);
        commandsFilter.addAction(CMD_SET_DEFAULT_DEVICE);
        //commandsFilter.addAction(CMD_SEND);
        getApplicationContext().registerReceiver(receiveCommands, commandsFilter);

        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getApplicationContext().registerReceiver(discoveryBroadcast, foundFilter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getApplicationContext().unregisterReceiver(receiveCommands);
        getApplicationContext().unregisterReceiver(discoveryBroadcast);
        mBluetoothAdaptor.cancelDiscovery();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void checkBTAndEnable()  {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Checking BT");
                if (!mBluetoothAdaptor.isEnabled()) {
                    Log.d(TAG, "Bluetooth is disable... ENABLING");
                    mBluetoothAdaptor.enable();
                } else Log.d(TAG, "BT is already enabled");

                if (mBluetoothAdaptor == null) {
                    Log.d(TAG, "There is no Bluetooth Adaptor");
                }
            }
        };
        runnable.run();
    }

    private void connectTo(final String btAddress) {
        Log.d(TAG, "Connecting to: " + btAddress);
        final BluetoothDevice bluetoothDevice = mBluetoothAdaptor.getRemoteDevice(btAddress);
        Runnable connect = new Runnable() {
            @Override
            public void run() {
                ParcelUuid[] uuids = bluetoothDevice.getUuids();
                try {
                    Log.d(TAG, "Trying to connect...");
                    socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                    socket =(BluetoothSocket) bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(bluetoothDevice,1);
                    Log.d(TAG, "Socket connection created");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Something went wrong with socket creation");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                try {
                    socket.connect();
                    if (socket.isConnected()) {
                        Log.d(TAG, "Socket connected");
                        btConnected = true;
                        outputStream = socket.getOutputStream();
                        inStream = socket.getInputStream();
                    } else Log.d(TAG, "Something went wrong with socket connection");
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        socket =(BluetoothSocket) bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(bluetoothDevice,1);
                        if (socket.isConnected()) {
                            Log.d(TAG, "Socket connected");
                            btConnected = true;
                            outputStream = socket.getOutputStream();
                            inStream = socket.getInputStream();
                        } else Log.d(TAG, "Something went wrong with socket connection");
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException e1) {
                        e1.printStackTrace();
                    }
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
            if (defaultDeviceAddress != null) {
                connectTo(defaultDeviceAddress);
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
        getApplicationContext().registerReceiver(bState, connectedFilter);
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
        }
        getApplicationContext().sendBroadcast(intent);
    }

    public BluetoothSocket getSocket(){
        return socket;
    }
}
