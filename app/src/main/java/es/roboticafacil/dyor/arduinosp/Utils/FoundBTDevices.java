package es.roboticafacil.dyor.arduinosp.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import es.roboticafacil.dyor.arduinosp.Models.BluetoothObject;

/**
 * Created by Dragos Dunareanu on 08-May-17.
 */

public class FoundBTDevices {
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothObject> arrayOfFoundBTDevices;
}
