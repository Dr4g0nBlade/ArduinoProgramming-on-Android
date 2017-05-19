package es.roboticafacil.dyor.arduinosp.Adaptors;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import es.roboticafacil.dyor.arduinosp.R;

/**
 * Created by Dragos Dunareanu on 16-May-17.
 */

public class SearchBTAdaptor extends BaseAdapter {

    SharedPreferences preferences;
    Gson gson = new Gson();

    Context mContext;
    BluetoothAdapter bluetoothAdapter;
    List<BluetoothDevice> mDevices;
    BluetoothDevice bluetoothDevice;
    List<Boolean> inRange;


    public SearchBTAdaptor(Context mContext, List<BluetoothDevice> mDevices, SharedPreferences preferences, List<Boolean> inRange) {
        this.mContext = mContext;
        this.mDevices = mDevices;
        this.preferences = preferences;
        this.inRange = inRange;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.bluetoothDevice = gson.fromJson(preferences.getString("defaultDevice", null), BluetoothDevice.class);
    }

    @Override
    public int getCount() {
        return this.mDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        BluetoothDevice device = mDevices.get(position);
        if (v == null) {
            v = inflater.inflate(R.layout.row_bt_scan_new_devices, parent, false);
            TextView tvDeviceName = (TextView) v.findViewById(R.id.tv_bt_name);
            TextView tvDeviceAddress = (TextView) v.findViewById(R.id.tv_bt_address);
            ImageView ivDeviceStatus = (ImageView) v.findViewById(R.id.iv_bt_status);

            tvDeviceName.setText(device.getName());
            tvDeviceAddress.setText(device.getAddress());
            if (!bluetoothAdapter.getBondedDevices().contains(device)){
                ivDeviceStatus.setImageResource(R.drawable.ic_bt_unknown);
            } else if (!device.equals(bluetoothDevice)) {
                ivDeviceStatus.setImageResource(R.drawable.ic_bt_paired);
            } else ivDeviceStatus.setImageResource(R.drawable.ic_bt_connected);
        }
        return v;
    }
}
