package surabastos.pedro.granero.com.el_granero_pedro.util;

import android.app.Application;
import android.os.SystemClock;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Utilss extends Application{

    //private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();

        SystemClock.sleep(3000);
    }


}
