package es.roboticafacil.dyor.arduinosp.Activities.Tabs;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import es.roboticafacil.dyor.arduinosp.R;

/**
 * Created by Dragos Dunareanu on 22-Mar-17.
 */

public class ProfileSettings extends android.support.v4.app.Fragment {

    private static final int REQUEST_ENABLE_BT = 99;
    FirebaseAuth mAuth;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<BluetoothObject> mAdaptor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.profile_settings, container, false);

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d("Profile", "Checked login:" + user);

                final ImageView ivAvatar = (ImageView) v.findViewById(R.id.iv_avatar);
                TextView tvDisplayName = (TextView) v.findViewById(R.id.tv_disp_name);
                if (user == null || user.isAnonymous()) {
                    tvDisplayName.setEnabled(false);
                } else {
                    ivAvatar.setEnabled(true);
                    Picasso.with(getContext())
                            .load(user.getPhotoUrl().toString().replace("/s96-c/", "/s300-c/"))
                            .into(ivAvatar, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Bitmap imageBitmap = ((BitmapDrawable) ivAvatar.getDrawable()).getBitmap();
                                    RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                    imageDrawable.setCircular(true);
                                    imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()));
                                    ivAvatar.setImageDrawable(imageDrawable);
                                }

                                @Override
                                public void onError() {

                                }
                            });

                    tvDisplayName.setEnabled(true);
                    tvDisplayName.setText(user.getDisplayName());
                }
            }
        });

        Button btSetupArduino = (Button) v.findViewById(R.id.bt_setup_arduino);
        btSetupArduino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SetUpArduinp.class);
                startActivity(intent);
            }
        });

        Button btPairBT = (Button) v.findViewById(R.id.bt_pair_bt);
        btPairBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentManager fm = getChildFragmentManager();
//                BluetoothDevicesListPairing dialog = new BluetoothDevicesListPairing();
//                dialog.show(fm, "Bluetooth Dialog");
                Intent intent = new Intent(getActivity(), BTConnectActivity.class);
                startActivity(intent);
            }
        });

        TextView tvBTStatus = (TextView) v.findViewById(R.id.tv_bt_status);



        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProfileSettingsFragment childFragment = new ProfileSettingsFragment();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == 0) {
                // If the resultCode is 0, the user selected "No" when prompt to
                // allow the app to enable bluetooth.
                // You may want to display a dialog explaining what would happen if
                // the user doesn't enable bluetooth.
                Toast.makeText(getActivity(), "The user decided to deny bluetooth access", Toast.LENGTH_LONG).show();
            } else
                Log.i("BT", "User allowed bluetooth access!");
        }
    }

    private void CheckBt() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getContext(), "Bluetooth Disabled !",
                    Toast.LENGTH_SHORT).show();
                   /* It tests if the bluetooth is enabled or not, if not the app will show a message. */
            mBluetoothAdapter.enable();
        }

        if (mBluetoothAdapter == null) {
            Toast.makeText(getContext(),
                    "Bluetooth null !", Toast.LENGTH_SHORT)
                    .show();
        }


        scanForBluetoothDevices();
    }


    private List<BluetoothObject> getArrayOfAlreadyPairedBluetoothDevices() {
        List<BluetoothObject> arrayOfAlreadyPairedBTDevices = null;

        // Query paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are any paired devices
        if (pairedDevices.size() > 0) {
            arrayOfAlreadyPairedBTDevices = new ArrayList<>();

            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Create the device object and add it to the arrayList of devices
                BluetoothObject bluetoothObject = new BluetoothObject();
                bluetoothObject.setBluetooth_name(device.getName());
                bluetoothObject.setBluetooth_address(device.getAddress());
                bluetoothObject.setBluetooth_state(device.getBondState());
                bluetoothObject.setBluetooth_type(device.getType());    // requires API 18 or higher
                bluetoothObject.setBluetooth_uuids(device.getUuids());

                arrayOfAlreadyPairedBTDevices.add(bluetoothObject);
            }
        }

        return arrayOfAlreadyPairedBTDevices;
    }

    private void scanForBluetoothDevices() {
        Intent intent = new Intent(getContext(), FoundBTDevices.class);
        startActivity(intent);
    }


}

