package es.roboticafacil.dyor.arduinosp.Activities;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.blockly.android.AbstractBlocklyActivity;
import com.google.blockly.android.WorkspaceFragment;
import com.google.blockly.android.codegen.CodeGenerationRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import es.roboticafacil.dyor.arduinosp.Activities.Fragments.SelectSaveLocal;
import es.roboticafacil.dyor.arduinosp.R;
import es.roboticafacil.dyor.arduinosp.Utils.FoundBTDevices;

public class BlocklyActivity extends AbstractBlocklyActivity {

    Intent intent;
    String xml;

    String mCode;
    BaseActivity ba;
    WorkspaceFragment mFragmentWorkshop;
    String[] fileList;

    private ProgressDialog mProgressDialog;
    String xmlName;

    SelectSaveLocal mSelectDialog;

    private static final String TAG = "Jon";
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private static String address = "XX:XX:XX:XX:XX:XX";
    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private InputStream inStream = null;
    Handler handler = new Handler();

    byte delimiter = 10;
    boolean stopWorker = false;
    int readBufferPosition = 0;
    byte[] readBuffer = new byte[1024];

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        ba = new BaseActivity();
        mFragmentWorkshop = new WorkspaceFragment();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    protected String getToolboxContentsXmlPath() {
        return "default/toolbox.xml";
    }

    @NonNull
    @Override
    protected List<String> getBlockDefinitionsJsonPaths() {
        return Arrays.asList(
                "default/list_blocks.json",
                "default/logic_blocks.json",
                "default/loop_blocks.json",
                "default/math_blocks.json",
                "default/text_blocks.json",
                "default/variable_blocks.json",
                "default/colour_blocks.json"
        );

    }

    private static final List<String> JAVASCRIPT_GENERATORS = Arrays.asList(
            // Custom block generators go here. Default blocks are already included.

    );
    private Handler mHandler = new Handler();

    @NonNull
    @Override
    protected List<String> getGeneratorsJsPaths() {
        return JAVASCRIPT_GENERATORS;
    }

    //  CodeGenerationRequest.CodeGeneratorCallback mCodeGeneratorCallback =
    //         new LoggingCodeGeneratorCallback(this, "code");

    CodeGenerationRequest.CodeGeneratorCallback mCodeGeneratorCallback =
            new CodeGenerationRequest.CodeGeneratorCallback() {
                @Override
                public void onFinishCodeGeneration(final String generatedCode) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCode = generatedCode;

                            Toast.makeText(BlocklyActivity.this, mCode, Toast.LENGTH_LONG).show();
                            FirebaseDatabase.getInstance().getReference("/saves/").push().setValue("Testing DB" + mCode).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    hideProgressDialog();
                                }
                            });


                        }
                    });
                }
            };


    @NonNull
    @Override
    protected CodeGenerationRequest.CodeGeneratorCallback getCodeGenerationCallback() {


        return mCodeGeneratorCallback;
    }

    @Override
    protected void onInitBlankWorkspace() {

        intent = getIntent();
        xml = intent.getStringExtra("xml");
        if (xml.equals("null")) {
            super.onInitBlankWorkspace();
            xmlName = "null";
        } else {
            mBlockly.loadWorkspaceFromAppDirSafely(xml);
            xmlName = xml;
        }
        Log.e("File", "Setting file name");

    }


    @Override
    protected void onRunCode() {
        super.onRunCode();
    }

    @Override
    public void onSaveWorkspace() {

//        //showProgressDialog();
//        //mBlockly.requestCodeGeneration(this.getBlockDefinitionsJsonPaths(),this.getGeneratorsJsPaths(),this.getCodeGenerationCallback());
//        DialogFragment saveChoiceDialog = new SelectSaveTypeDialogFragment();
//        Bundle args = new Bundle();
//        args.putString("code", mCode);
//
//
//
//        saveChoiceDialog.setArguments(args);
//        saveChoiceDialog.show(getSupportFragmentManager(), "Save Dialog");
        if (fileExists(xmlName) && !xmlName.equals("")) {
            mBlockly.saveWorkspaceToAppDirSafely(xmlName);
        } else {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Title");

            LayoutInflater inflator = getLayoutInflater();
            final View viewInflated = inflator.inflate(R.layout.create_dialog, null);

            builder.setView(viewInflated)
                    .setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText etXmlName = (EditText) viewInflated.findViewById(R.id.et_name);
                            String name = etXmlName.getText().toString();
                            xmlName = name + ".xml";
                            mBlockly.saveWorkspaceToAppDirSafely(xmlName);
                            Log.e("File", "Saved as: " + xmlName);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builder.show();
        }


        CheckBt();

        //super.onSaveWorkspace();
    }

    @Override
    public void onLoadWorkspace() {
        //mSelectDialog = new SelectSaveLocal();
        //Bundle args = new Bundle();
        //args.putStringArray("list", fileList());
        //mSelectDialog.setArguments(args);
        //mSelectDialog.show(getSupportFragmentManager(), "Dialog");


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title")
                .setItems(fileList(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String[] list = fileList();
                        mBlockly.loadWorkspaceFromAppDirSafely(list[which]);

                    }
                });
        builder.show();

        //super.onLoadWorkspace();
    }

    public void loadLocalFile(int i) {
        fileList = fileList();
        Log.e("File", "Loading list file");
        mBlockly.loadWorkspaceFromAppDirSafely(fileList[i]);
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);

            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void uploadCode() {
        mBlockly.requestCodeGeneration(this.getBlockDefinitionsJsonPaths(), this.getGeneratorsJsPaths(), this.getCodeGenerationCallback());
    }

    public boolean fileExists(String filename) {
        File f = new File(filename + ".xml");
        if (f.isFile() && f.canRead()) {
            return true;
        } else return false;

    }

    public String getReturn(String s) {
        return s;
    }
    
    private void CheckBt() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "Bluetooth Disabled !",
                    Toast.LENGTH_SHORT).show();
                   /* It tests if the bluetooth is enabled or not, if not the app will show a message. */
            mBluetoothAdapter.enable();
            scanForBluetoothDevices();
        }

        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),
                    "Bluetooth null !", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void scanForBluetoothDevices() {
        Intent intent = new Intent(this, FoundBTDevices.class);
        startActivity(intent);
    }


    public void Connect() {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        Log.d("Bluetooth", "Connecting to ... " + device);
        mBluetoothAdapter.cancelDiscovery();
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
/* Here is the part the connection is made, by asking the device to create a RfcommSocket (Unsecured socket I guess), It map a port for us or something like that */
            btSocket.connect();
            Log.d("Bluetooth", "Connection made.");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                Log.d("Bluetooth", "Unable to end the connection");
            }
            Log.d("Bluetooth", "Socket creation failed");
        }

        beginListenForData();
               /* this is a method used to read what the Arduino says for example when you write Serial.print("Hello world.") in your Arduino code */
    }

    private void writeData(String data) {
        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Log.d(TAG, "Bug BEFORE Sending stuff", e);
        }

        String message = data;
/* In my example, I put a button that invoke this method and send a string to it */
        byte[] msgBuffer = message.getBytes();

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            Log.d(TAG, "Bug while sending stuff", e);
        }
    }

    public void beginListenForData() {
        try {
            inStream = btSocket.getInputStream();
        } catch (IOException e) {
        }

        Thread workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = inStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable() {
                                        public void run() {

                                            Log.d("Bluetooth", "Reading Data");
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }


}
