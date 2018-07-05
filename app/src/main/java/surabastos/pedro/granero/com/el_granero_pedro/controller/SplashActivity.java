package surabastos.pedro.granero.com.el_granero_pedro.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import surabastos.pedro.granero.com.el_granero_pedro.R;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        updateUI(currentUser);

        finish();
    }

    private void updateUI(FirebaseUser user) {

        if(!sharedPreferences.getString("RegitryComplete", "").equals("") && user != null){

            Toast.makeText(SplashActivity.this, getString(R.string.saplashActivity_welcome), Toast.LENGTH_SHORT).show();
            changeActivity(SplashActivity.this, PrincipalActivity.class);
        }
        else if(user != null) {

            Toast.makeText(SplashActivity.this, getString(R.string.saplashActivity_please_complete_registry), Toast.LENGTH_SHORT).show();
            changeActivity(SplashActivity.this, MainActivity.class);
        }
        else{

            Toast.makeText(SplashActivity.this, getString(R.string.saplashActivity_please_sign_up), Toast.LENGTH_SHORT).show();
            changeActivity(SplashActivity.this, LogIn.class);
        }
        Log.e(TAG, "Shared " + sharedPreferences.getString("RegitryComplete", "") + " user " + user);
    }

    private void changeActivity (Context context, Class classs){

        Intent intent = new Intent(context, classs);
        startActivity(intent);
    }
}
