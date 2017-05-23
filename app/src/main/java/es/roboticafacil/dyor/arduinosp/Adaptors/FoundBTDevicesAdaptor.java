package es.roboticafacil.dyor.arduinosp.Adaptors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import es.roboticafacil.dyor.arduinosp.R;

/**
 * Created by Dragos Dunareanu on 08-May-17.
 */

public class FoundBTDevicesAdaptor extends ArrayAdapter<BluetoothObject> {
    private Context context;
    private List<BluetoothObject> arrayFoundDevices;

    public FoundBTDevicesAdaptor(Context context, List<BluetoothObject> arrayOfAlreadyPairedDevices) {
        super(context, R.layout.row_bt_scan_new_devices, arrayOfAlreadyPairedDevices);

        this.context = context;
        this.arrayFoundDevices = arrayOfAlreadyPairedDevices;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BluetoothObject bluetoothObject = arrayFoundDevices.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.row_bt_scan_new_devices, parent, false);

//        TextView bt_name = (TextView) rowView.findViewById(R.id.textview_bt_scan_name);
//        TextView bt_address = (TextView) rowView.findViewById(R.id.textview_bt_scan_address);
//        TextView bt_bondState = (TextView) rowView.findViewById(R.id.textview_bt_scan_state);
//        TextView bt_type = (TextView) rowView.findViewById(R.id.textview_bt_scan_type);
//        TextView bt_uuid = (TextView) rowView.findViewById(R.id.textview_bt_scan_uuid);
//        TextView bt_signal_strength = (TextView) rowView.findViewById(R.id.textview_bt_scan_signal_strength);

//        assert bluetoothObject != null;
//        bt_name.setText(bluetoothObject.getBluetooth_name());
//        bt_address.setText(context.getString(R.string.bt_address, bluetoothObject.getBluetooth_address()));
//        bt_bondState.setText(context.getString(R.string.bt_state, bluetoothObject.getBluetooth_state()));
//        bt_type.setText(context.getString(R.string.bt_type, bluetoothObject.getBluetooth_type()));
//        bt_signal_strength.setText("RSSI: " + bluetoothObject.getBluetooth_rssi() + "dbm");
//
//        ParcelUuid uuid[] = bluetoothObject.getBluetooth_uuids();
//        if (uuid != null)
//            bt_uuid.setText("uuid" + uuid[0]);

        return rowView;
    }
}
