package es.roboticafacil.dyor.arduinosp.Activities;

//import android.app.Dialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import es.roboticafacil.dyor.arduinosp.R;

public class BaseActivity extends AppCompatActivity {

    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    public AlertDialog.Builder mSignInDialog;
    public AlertDialog.Builder mChangeLoginDialog;

    //final Intent welcomeScreen = new Intent(this, ChooseSignInActivity.class);

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void showSignInDialog(final Intent screen) {
        if (mSignInDialog == null) {
            mSignInDialog = new AlertDialog.Builder(this);
            mSignInDialog.setTitle("Information about accounts")
                    .setMessage("To be able to use every function of the application, you are requiered to sign")
                    .setNegativeButton("Continue as guest", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //hideSignInDialog();
                        }
                    })
                    .setPositiveButton("Sign in", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(screen);
                        }
                    }).show();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}