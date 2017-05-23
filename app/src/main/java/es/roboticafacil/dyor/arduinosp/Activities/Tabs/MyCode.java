package es.roboticafacil.dyor.arduinosp.Activities.Tabs;

//import android.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import es.roboticafacil.dyor.arduinosp.Activities.BlocklyActivity;
import es.roboticafacil.dyor.arduinosp.Activities.Fragments.SelectSaveLocal;
import es.roboticafacil.dyor.arduinosp.R;

import static es.roboticafacil.dyor.arduinosp.Activities.MainActivity.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;

/**
 * Created by Dragos Dunareanu on 22-Mar-17.
 */

public class MyCode extends android.support.v4.app.Fragment {

    //int permissionStorageCheck = ContextCompat.checkSelfPermission(this,
    //      android.Manifest.permission.READ_EXTERNAL_STORAGE);

    boolean access = false;
    SelectSaveLocal mDialog;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    access = true;

                } else {

                    access = false;
                }
                return;
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.my_code, container, false);

        Button btCreate = (Button) v.findViewById(R.id.bt_create_blockly);
        Button btLoadLocal = (Button) v.findViewById(R.id.bt_load_local);

        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCode.this.getContext(), BlocklyActivity.class);
                intent.putExtra("xml", "null");
                startActivity(intent);
            }
        });

        if (!access) {
            btLoadLocal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Title")
                            .setItems(getContext().fileList(), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String[] list = getContext().fileList();
                                    Intent intent = new Intent(MyCode.this.getContext(), BlocklyActivity.class);
                                    intent.putExtra("xml", list[which]);
                                    startActivity(intent);
                                    //mBlockly.loadWorkspaceFromAppDirSafely(list[which]);

                                }
                            });
                    builder.show();
                }
            });
        } else {
            btLoadLocal.setEnabled(false);
        }

        return v;
    }
}
