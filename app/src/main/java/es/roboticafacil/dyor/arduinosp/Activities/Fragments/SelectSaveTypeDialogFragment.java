package es.roboticafacil.dyor.arduinosp.Activities.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.google.blockly.android.BlocklyActivityHelper;

import es.roboticafacil.dyor.arduinosp.Activities.BlocklyActivity;
import es.roboticafacil.dyor.arduinosp.R;

/**
 * Created by Dragos Dunareanu on 13-Apr-17.
 */

public class SelectSaveTypeDialogFragment extends DialogFragment {

    protected BlocklyActivityHelper mBlockly;
    int userChoice;
    private BlocklyActivity ba = new BlocklyActivity();


    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final String code = getArguments().getString("code");

        builder.setTitle("Select where you want to save")
                .setSingleChoiceItems(R.array.save_choices, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userChoice = which;
                    }
                })
                .setNegativeButton("Cancel", null)
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        switch (userChoice) {
                            case 0:
                                ((BlocklyActivity) getActivity()).showProgressDialog();
                                ((BlocklyActivity) getActivity()).uploadCode();
                                break;
                            case 1:

                                break;
                            case 2:

                                break;
                        }

                    }
                });


        return builder.create();
    }
}
