
package com.vpacomercial.sivhn;

import static androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_DISABLED;
import static androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_ENABLED;
import static androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_WHITELISTED;

import android.Manifest;
import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Check_Splash extends AppCompatActivity implements LogOutTimer.LogOutListener {
    private ProgressBar progressBarId;
    private ProgressBar progressBarSophos;
    private ProgressBar progressDeviceName;
    private TextView textviewID;
    private TextView textviewSophos;
    private TextView textviewDeviceName;
    private Handler hdlr = new Handler();
    private int i = 0;
    private Context mContext;
    private Activity mActivity;
    private static final String TAG = "MainActivity";
    public String actualUrl;

    //    mContext = getApplicationContext();
//    mActivity = Check_Splash.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check__splash);
        progressBarId = findViewById(R.id.progressBarId);
        progressBarId.setVisibility(View.INVISIBLE);
        progressBarSophos = findViewById(R.id.progressBarSophos);
        progressBarSophos.setVisibility(View.INVISIBLE);
        progressDeviceName = findViewById(R.id.progressBarDeviceName);
        progressDeviceName.setVisibility(View.INVISIBLE);
        textviewID = findViewById(R.id.textviewID);
        textviewID.setVisibility(View.INVISIBLE);
        textviewSophos = findViewById(R.id.textViewSophos);
        textviewSophos.setVisibility(View.INVISIBLE);
        textviewDeviceName = findViewById(R.id.textViewDeviceName);
        textviewDeviceName.setVisibility(View.INVISIBLE);
        Button btn = findViewById(R.id.btnacceder);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Log.d("No ha tronado","aun no truena");
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to WRITE_EXTERNAL_STORAGE - requesting it");
                String[] permissions = {Manifest.permission.READ_PHONE_STATE};
                requestPermissions(permissions, 1);
            }
        }
        final List<String> installedPackages = getInstalledAppsPackageNameList();
        btn.setOnClickListener(v -> {


            run();
            textviewSophos.setText("Sophos Verificado");
            String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            System.out.println(id.toString());
            String build = Build.MODEL;
            //BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
            //String dn = myDevice.getName();
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            textviewID.setText(id.toString());
            textviewSophos.setText("Sophos Verificado");
            textviewDeviceName.setText("Dispositivo Registrado");
            if (NetworkAvailable()) {
                if (isBackgroundDataAccessAvailable()) {
                    Intent intent = new Intent(Check_Splash.this, MainActivity.class);
                    intent.putExtra("AndroidId", id);
                    //intent.putExtra("IMEI", IMEI);
                    //intent.putExtra("DeviceName", dn);
                    intent.putExtra("Build", build);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Check_Splash.this, No_Internet.class);
                    startActivity(intent);
                }

            } else {
                Intent intent = new Intent(Check_Splash.this, No_Internet.class);
                startActivity(intent);
            }
            /*String packageNameFacebook = "com.facebook.katana";
            String packageNameSophos = "com.sophos.mobilecontrol.client.android";
            String packageNameBacMovil = "net.bac.sbe.android";
            String packageNameOpenCamera = "net.sourceforge.opencamera";
            String packageNameChrome = "com.android.chrome";
            String packageNameYoutube = "com.google.android.youtube";*/
            /*if (installedPackages.contains(packageNameSophos)) {
               textviewSophos.setText("Sophos Verificado");
                String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                String build = Build.MODEL;
                BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
                String dn = myDevice.getName();
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                String IMEI = telephonyManager.getImei();
                String Number = telephonyManager.getSimOperatorName();
                String SIMSerial = telephonyManager.getSimSerialNumber();
                textviewID.setText("ID Obtenida.");
                textviewSophos.setText("Sophos Verificado");
                textviewDeviceName.setText("Dispositivo Registrado");
                    textviewID.setText(id);
                    textviewSophos.setText(build);
                    textviewDeviceName.setText(dn);
                boolean installed = true;


                if (NetworkAvailable()) {
                    if (isBackgroundDataAccessAvailable()) {
                        Intent intent = new Intent(Check_Splash.this, MainActivity.class);
                        intent.putExtra("AndroidId", id);
                        //intent.putExtra("IMEI", IMEI);
                        intent.putExtra("DeviceName", dn);
                        intent.putExtra("Build", build);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Check_Splash.this, No_Internet.class);
                        startActivity(intent);
                    }

                } else {
                    Intent intent = new Intent(Check_Splash.this, No_Internet.class);
                    startActivity(intent);
                }

            } else {
                progressBarId.setVisibility(View.INVISIBLE);
                progressBarSophos.setVisibility(View.INVISIBLE);
                progressDeviceName.setVisibility(View.INVISIBLE);
                textviewID.setVisibility(View.INVISIBLE);
                textviewSophos.setVisibility(View.INVISIBLE);
            }*/
//                if(installedPackages.contains(packageNameSophos)){
//                    textviewSophos.setText("Sophos instalado");
//                    startActivity(new Intent(Check_Splash.this, MainActivity.class));
//                }else {
//                    textviewSophos.setText("Sophos no instalado");
//                }
        });


    }


    protected List<String> getInstalledAppsPackageNameList() {
        // Initialize a new intent
        Intent intent = new Intent(Intent.ACTION_MAIN, null);

        // Set intent category
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // Set intent flags
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        // Initialize a new list of resolve info
        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent, 0);

        // Initialize a new list of package name
        List<String> packageNameList = new ArrayList<>();

        for (ResolveInfo resolveInfo : resolveInfoList) {

            // Get the activity info from resolve info
            ActivityInfo activityInfo = resolveInfo.activityInfo;

            // Get the package name from activity info's application info
            // Add the package name to the list
            packageNameList.add(activityInfo.applicationInfo.packageName);
        }

        // Return the package name list
        return packageNameList;
    }

    public void run() {
        progressBarId.setVisibility(View.VISIBLE);
        progressBarSophos.setVisibility(View.VISIBLE);
        progressDeviceName.setVisibility(View.VISIBLE);
        textviewID.setVisibility(View.VISIBLE);
        textviewSophos.setVisibility(View.VISIBLE);
        textviewDeviceName.setVisibility(View.VISIBLE);
        while (i < 30) {
            i += 1;
            hdlr.post(new Runnable() {
                @Override
                public void run() {
                    progressBarId.setProgress(i);
                    progressBarId.setProgress(100);
                    //textviewID.setText("ID Obtenida.");
                    progressBarSophos.setProgress(i);
                    progressBarSophos.setProgress(100);
                    progressDeviceName.setProgress(100);
                }
            });

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //Inicio de Logout
    @Override
    protected void onStart() {
        super.onStart();
        LogOutTimer.startLogoutTimer(this, this);
        Log.e(TAG, "OnStart () &&& Starting timer");
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        LogOutTimer.startLogoutTimer(this, this);
        Log.e(TAG, "User interacting with screen");
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

    /**
     * Performing idle time logout
     */
    @Override
    public void doLogout() {
        finish();
        System.exit(0);
    }

    public boolean clearCache() {
        try {

            // create an array object of File type for referencing of cache files
            File[] files = getBaseContext().getCacheDir().listFiles();

            // use a for etch loop to delete files one by one
            for (File file : files) {

                /* you can use just [ file.delete() ] function of class File
                 * or use if for being sure if file deleted
                 * here if file dose not delete returns false and condition will
                 * will be true and it ends operation of function by return
                 * false then we will find that all files are not delete
                 */
                if (!file.delete()) {
                    return false;         // not success
                }
            }

            // if for loop completes and process not ended it returns true
            return true;      // success of deleting files

        } catch (Exception e) {
        }

        // try stops deleting cache files
        return false;       // not success
    }

    public boolean NetworkAvailable() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
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
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

}

