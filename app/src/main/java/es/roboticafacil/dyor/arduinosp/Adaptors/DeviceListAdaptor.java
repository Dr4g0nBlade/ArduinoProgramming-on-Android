package es.roboticafacil.dyor.arduinosp.Adaptors;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import es.roboticafacil.dyor.arduinosp.R;

/**
 * Created by Dragos Dunareanu on 11-May-17.
 */

public class DeviceListAdaptor extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater mLayoutInflater;
    private List<BluetoothDevice> mDevices;
    private int mViewResourceId;

    public DeviceListAdaptor(Context context, int tvResourceId, List<BluetoothDevice> devices){
        super(context, tvResourceId, devices);
        this.mDevices = devices;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = tvResourceId;
    }

    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        convertView = mLayoutInflater.inflate(mViewResourceId, null);

        BluetoothDevice device = mDevices.get(position);

        if (device != null){
            TextView deviceName = (TextView) convertView.findViewById(R.id.tv_btd_name);
            TextView deviceAddress = (TextView) convertView.findViewById(R.id.tv_btd_address);

            if (deviceName != null){
                deviceName.setText(device.getName());
            }
            if (deviceAddress != null){
                deviceAddress.setText(device.getAddress());
            }
        }
        return convertView;
    }
}
