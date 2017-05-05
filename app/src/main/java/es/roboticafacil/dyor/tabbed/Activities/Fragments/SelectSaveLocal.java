package es.roboticafacil.dyor.tabbed.Activities.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.google.blockly.android.BlocklyActivityHelper;

/**
 * Created by Dragos Dunareanu on 19-Apr-17.
 */

public class SelectSaveLocal extends DialogFragment {

    BlocklyActivityHelper mActivity;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Test")
                .setItems(getArgs(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }

    public String[] getArgs(){
        String[] args = getArguments().getStringArray("list");

        if (args == null){
            args = new String[]{"element1","element2","element3"};
        }

        return args;
    }

    public String getArg(int i){
        String[] args = getArgs();
        return args[i];
    }
}
