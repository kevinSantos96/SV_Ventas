package com.vpacomercial.sivhn;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.widget.Toast;


import static androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_DISABLED;
import static androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_ENABLED;
import static androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_WHITELISTED;

import java.net.InetAddress;

public class No_Internet extends AppCompatActivity implements LogOutTimer.LogOutListener {

    private static final String TAG = No_Internet.class.getSimpleName();
    int mlinfuture = 20000;
    @Override

    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        TextView timer = findViewById(R.id.no_internet_text);
        Button btn = findViewById(R.id.try_again_button);
        //btn copy text
        Button btn_copy = findViewById(R.id.button_copy);

        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("Revisa la conexión a internet de tus dispositivo e intenta de nuevo en " + millisUntilFinished /1000+ " segundos, tu id es: "+ androidId.toString());
                btn.setClickable(false);
                btn.setEnabled(false);
            }

            public void onFinish() {
                btn.setEnabled(true);
                btn.setClickable(true);
            }

        }.start();
        btn.setOnClickListener(v -> {
            String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String build = Build.MODEL;
            BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
            String dn = myDevice.getName();
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (NetworkAvailable()){
                /*Intent intent = new Intent(No_Internet.this, MainActivity.class);
                intent.putExtra("AndroidId", id);
                //intent.putExtra("IMEI", IMEI);
                intent.putExtra("DeviceName", dn);
                intent.putExtra("Build", build);
                startActivity(intent);*/
                if (isBackgroundDataAccessAvailable()){
                    finish();
                }else{
                    Intent intent = new Intent(No_Internet.this, No_Internet.class);
                    startActivity(intent);
                }
                /*if(isBackgroundDataAccessAvailable()){
                    finish();
                }else{
                    Intent intent = new Intent(No_Internet.this, No_Internet.class);
                    startActivity(intent);
                }*/

            }else{
                Intent intent = new Intent(No_Internet.this, No_Internet.class);
                startActivity(intent);
            }
        });
        //copy id
        btn_copy.setOnClickListener(v ->{
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("copiado!",androidId);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this,"Texto copiado al portapapeles",Toast.LENGTH_SHORT).show();
        });
    }

    public boolean NetworkAvailable(){
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        }catch (Exception e){
            Log.e("Connectivity Exception", e.getMessage());
        }
        return  connected;
    }
    @Override
    protected void onStart() {
        super.onStart();
        LogOutTimer.startLogoutTimer(this, this);
        Log.e(TAG, "OnStart () &&& Starting timer");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e(TAG, "onResume()");
    }
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        /*if (NetworkAvailable()){
            if(isBackgroundDataAccessAvailable()){

            }else{
                Intent intent = new Intent(No_Internet.this, No_Internet.class);
                startActivity(intent);
            }
        }else{
            Intent intent = new Intent(No_Internet.this, No_Internet.class);
            startActivity(intent);
        }*/

        LogOutTimer.startLogoutTimer(this, this);
        Log.e(TAG, "User interacting with screen");
    }
    @Override
    public void doLogout() {
        finish();
        System.exit(0);
    }
    public boolean isBackgroundDataAccessAvailable() {

        boolean isBackgroundDataAccessAvailable = true;

        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr != null) {
            // Checks if the device is on a metered network
            if (connMgr.isActiveNetworkMetered()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    // Checks user’s Data Saver settings.
                    switch (connMgr.getRestrictBackgroundStatus()) {

                        case RESTRICT_BACKGROUND_STATUS_DISABLED:
                            // Data Saver is disabled. Since the device is connected to a
                            // metered network, the app should use less data wherever possible.
                            isBackgroundDataAccessAvailable = true;
                            break;

                        case RESTRICT_BACKGROUND_STATUS_WHITELISTED:
                            // The app is whitelisted. Wherever possible,
                            // the app should use less data in the foreground and background.
                            isBackgroundDataAccessAvailable = true;
                            break;

                        case RESTRICT_BACKGROUND_STATUS_ENABLED:
                            // Background data usage is blocked for this app. Wherever possible,
                            // the app should also use less data in the foreground.
                            isBackgroundDataAccessAvailable = false;
                            break;
                    }
                } else {
                    NetworkInfo.State state = connMgr.getActiveNetworkInfo().getState();
                    isBackgroundDataAccessAvailable = state != NetworkInfo.State.DISCONNECTED;
                }

            } else {
                // The device is not on a metered network.
                // Use data as required to perform syncs, downloads, and updates.
                isBackgroundDataAccessAvailable = true;
            }
        } else {
            isBackgroundDataAccessAvailable = true;
        }

        return isBackgroundDataAccessAvailable;
    }
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("www.baccredomatic.com");
            //You can replace it with your name
            return !ipAddr.equals("www.baccredomatic.com");

        } catch (Exception e) {
            return false;
        }
    }
}