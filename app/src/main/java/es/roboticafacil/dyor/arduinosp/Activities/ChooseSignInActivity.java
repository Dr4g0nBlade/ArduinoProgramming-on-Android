package es.roboticafacil.dyor.arduinosp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import es.roboticafacil.dyor.arduinosp.R;

//import android.support.v7.app.AppCompatActivity;

public class ChooseSignInActivity extends BaseActivity {

    private FirebaseProfile fp;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sign_in);

        mAuth = FirebaseAuth.getInstance();

        final Intent mainActivity = new Intent(this, MainActivity.class);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //Change Acitivy
                    startActivity(mainActivity);
                } else {
                    //Dunno
                }
            }
        };

//        final TextInputEditText tiEmail = (TextInputEditText)findViewById(R.id.ti_si_email);
//        final TextInputEditText tiPass = (TextInputEditText)findViewById(R.id.ti_si_pass);
//
//        Button btSignIn = (Button)findViewById(R.id.bt_si);
//        Button btSignUp = (Button)findViewById(R.id.bt_su);
//
//        btSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signIn(tiEmail.getText().toString(),tiPass.getText().toString());
//            }
//        });
//
//        btSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createAccount(tiEmail.getText().toString(),tiPass.getText().toString());
//            }
//        });


    }

    private void createAccount(final String email, String password) {
        //if (!validateForm()){
//
        //}
//
        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        Log.e("CreatingUser", "Creating");
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(ChooseSignInActivity.this, R.string.auth_reg_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                        fp = new FirebaseProfile();
                        String uid = task.getResult().getUser().getUid();
                        String email = task.getResult().getUser().getEmail();
                        assert email != null;
                        int i = email.indexOf("@");
                        String username = email.substring(0, i);
                        String photoUri = task.getResult().getUser().getPhotoUrl().toString();
                        Log.e("CreatingUser", "Setting no channels");
                        List<String> noChannels = new ArrayList<>();
                        noChannels.add("Welcome");
                        User newUser = new User(uid, email, username, photoUri, noChannels);

                        Log.e("CreatingUser", "no channel set, uploading");
                        fp.getUsers().child(uid).setValue(newUser);

                        Log.e("CreatingUser", "done uploadinng");

                    }
                });

    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        fp = new FirebaseProfile();
                        fp.setLoggedIn(true);
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signinWithEmail:failed", task.getException());
                            Toast.makeText(ChooseSignInActivity.this, R.string.auth_sign_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
