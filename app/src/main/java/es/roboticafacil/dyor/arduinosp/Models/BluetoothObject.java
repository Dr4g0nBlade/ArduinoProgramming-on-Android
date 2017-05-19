package es.roboticafacil.dyor.arduinosp.Models;

import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;

/**
 * Created by Dragos Dunareanu on 08-May-17.
 */

public class BluetoothObject implements Parcelable {

    public static final Parcelable.Creator<BluetoothObject> CREATOR = new Parcelable.Creator<BluetoothObject>() {
        public BluetoothObject createFromParcel(Parcel in) {
            return new BluetoothObject();
        }

        public BluetoothObject[] newArray(int size) {
            return new BluetoothObject[size];
        }
    };
    private String bluetooth_name;
    private String bluetooth_address;
    private int bluetooth_state;
    private int bluetooth_type;
    private ParcelUuid[] bluetooth_uuids;
    private int bluetooth_rssi;

    public BluetoothObject() {
    }

    public BluetoothObject(Parcel in) {
        super();
        readFromParcel(in);
    }

    public String getBluetooth_name() {
        return bluetooth_name;
    }

    public void setBluetooth_name(String bluetooth_name) {
        this.bluetooth_name = bluetooth_name;
    }

    public String getBluetooth_address() {
        return bluetooth_address;
    }

    public void setBluetooth_address(String bluetooth_address) {
        this.bluetooth_address = bluetooth_address;
    }

    public int getBluetooth_state() {
        return bluetooth_state;
    }

    public void setBluetooth_state(int bluetooth_state) {
        this.bluetooth_state = bluetooth_state;
    }

    public int getBluetooth_type() {
        return bluetooth_type;
    }

    public void setBluetooth_type(int bluetooth_type) {
        this.bluetooth_type = bluetooth_type;
    }

    public ParcelUuid[] getBluetooth_uuids() {
        return bluetooth_uuids;
    }

    public void setBluetooth_uuids(ParcelUuid[] bluetooth_uuids) {
        this.bluetooth_uuids = bluetooth_uuids;
    }

    public int getBluetooth_rssi() {
        return bluetooth_rssi;
    }

    public void setBluetooth_rssi(int bluetooth_rssi) {
        this.bluetooth_rssi = bluetooth_rssi;
    }

    public void readFromParcel(Parcel in) {
        bluetooth_name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bluetooth_name);
    }
}
