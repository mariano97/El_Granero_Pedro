package surabastos.pedro.granero.com.el_granero_pedro.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;

import surabastos.pedro.granero.com.el_granero_pedro.R;
import surabastos.pedro.granero.com.el_granero_pedro.util.Md5;

public class LogIn extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    private Button btnLogin;
    private TextView btnSignUp;
    private TextView btnRecoPass;
    private EditText textEmail;
    private EditText textPass;
    private FirebaseAuth mAuth;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        btnLogin = (Button) findViewById(R.id.btnlogin);
        btnSignUp = (TextView) findViewById(R.id.btnSignUp);
        btnRecoPass = (TextView) findViewById(R.id.btnrecoverPass);

        textEmail = (EditText) findViewById(R.id.label_login_email);
        textPass = (EditText) findViewById(R.id.label_login_password);
        mAuth = FirebaseAuth.getInstance();



        //Log.i(TAG, "email " + textEmail.getText().toString() + " pass " + textPass.getText().toString());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(textEmail.getText().toString().equals("") && textPass.getText().toString().equals("")){
                    Toast.makeText(LogIn.this, R.string.login_alert_email_pass_empty, Toast.LENGTH_LONG).show();
                }
                else{
                    loginAccount(textEmail.getText().toString().trim(), textPass.getText().toString().trim(), LogIn.this, PrincipalActivity.class);
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(textEmail.getText().toString().equals("") && textPass.getText().toString().equals("")){
                    Toast.makeText(LogIn.this, R.string.login_alert_email_pass_empty, Toast.LENGTH_LONG).show();
                }
                else{
                    createAccount(textEmail.getText().toString().trim(), textPass.getText().toString().trim(), LogIn.this, MainActivity.class);
                }

            }
        });

        btnRecoPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recoveryPassword(textEmail.getText().toString().trim());
            }
        });
    }

    private void createAccount(String email, String password, final Context context, final Class classs){

        if(!isValidEmail(email) || !isValidPassword(password)){
            Toast.makeText(LogIn.this, R.string.login_alert_invalid_email_pass, Toast.LENGTH_LONG).show();
        }
        else {
            //Se deja de usar "newPassword" ya que aun no se sabe como guarda la contraseña google
            String newPassword = btnMD5(password);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                user.sendEmailVerification().addOnCompleteListener(LogIn.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(!task.isSuccessful()){
                                            Toast.makeText(LogIn.this, R.string.login_err_verif_send_email + task.getException().getMessage(), Toast.LENGTH_LONG).show();


                                        }else {
                                            Toast.makeText(LogIn.this, R.string.select_login_verif_send_email, Toast.LENGTH_SHORT).show();
                                            changeActivity(context, classs);
                                        }
                                    }
                                });

                                //changeActivity(context, classs);
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(LogIn.this, R.string.select_login_auth_failed,
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }

                            // ...
                        }
                    });
        }
    }

    private void loginAccount(String email, String password, final Context context, final Class classs) {

        if(!isValidEmail(email) || !isValidPassword(password)){
            Toast.makeText(LogIn.this, R.string.login_alert_invalid_email_pass, Toast.LENGTH_LONG).show();
        }
        else {
            //Se deja de usar "newPassword" ya que aun no se sabe como guarda la contraseña google
            String newPassword = btnMD5(password);
            String userUid = mAuth.getUid();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                checkUser(mAuth.getUid());

                                changeActivity(context, classs);
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                //Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LogIn.this, R.string.select_login_auth_failed,
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });
        }
    }

    private void checkUser(final String uid){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("users").child(uid).getChildrenCount() == 10){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("RegitryComplete", "true");
                    editor.apply();
                }

                Log.e(TAG, "getChi " + dataSnapshot.child("users").child(uid).getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //myRef.child("users").child(uid)
    }

    //Metodo para encriptar contraseña en formate MD5
    public String btnMD5(String pass){

        // Se instancia una variable de tipo 'byte' y se obtienen los bytes de la contraseña y
        byte[] md5Input = pass.getBytes();
        BigInteger md5Data = null;

        //Se intenta realizar el encriptamiento de los bytes de la cadena de texto
        try {
            md5Data = new BigInteger(1, Md5.encryptMD5(md5Input));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String md5Str = md5Data.toString(16);
        if(md5Str.length() < 32){
            md5Str = 0 + md5Str;
        }
        return md5Str;

    }

    private boolean isValidEmail(String email){
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String pass){
        return pass.length() > 5;
    }

    private void changeActivity (Context context, Class classs){

        Intent intent = new Intent(context, classs);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void recoveryPassword(String email){

        if(!email.isEmpty()){
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(LogIn.this, R.string.select_login_verif_send_email, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LogIn.this, R.string.login_err_verif_send_email + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else {
            Toast.makeText(LogIn.this, R.string.select_login_field_email_empty, Toast.LENGTH_LONG).show();
        }
    }
}