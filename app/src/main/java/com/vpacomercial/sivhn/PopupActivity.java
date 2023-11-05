package com.vpacomercial.sivhn;

import static androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_DISABLED;
import static androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_ENABLED;
import static androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_WHITELISTED;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PopupActivity extends AppCompatActivity implements LogOutTimer.LogOutListener{
    private static final String TAG = PopupActivity.class.getSimpleName();
    private WebView webView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        webView = (WebView) findViewById(R.id.popupwebView);
        webView.setWebViewClient(new WebViewClient());
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width), (int) (height));


        WebSettings webSettings =webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setLoadsImagesAutomatically(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSaveFormData(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        //webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setSaveFormData(true);
        webSettings.setEnableSmoothTransition(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 8.1.0; Pixel Build/OPM4.171019.021.D1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.109 Mobile Safari/537.36 EdgA/42.0.0.2057");
        webView.setWebChromeClient(new WebChromeClient());
        CookieManager.getInstance().setAcceptCookie(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webSettings.setDefaultTextEncodingName("utf-8");
        //Software or hardware acceleration
        if(Build.VERSION.SDK_INT >= 21){
            android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
            webSettings.setMixedContentMode(0);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else if(Build.VERSION.SDK_INT >= 19){
            android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else if(Build.VERSION.SDK_INT < 19){
            android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (NetworkAvailable()){
            if(isBackgroundDataAccessAvailable()){
                Intent intent = getIntent();
                url = intent.getStringExtra("Url");
                webView.loadUrl(url);
            }else{
                Intent intent = new Intent(PopupActivity.this, No_Internet.class);
                startActivity(intent);
            }
        }else{
            Intent intent = new Intent(PopupActivity.this, No_Internet.class);
            startActivity(intent);
        }
        /*webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                request.setMimeType(mimeType);
                //------------------------COOKIE!!------------------------
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                //------------------------COOKIE!!------------------------
                request.addRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36");
                request.setDescription("Descargando archivo...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Descargando archivo...", Toast.LENGTH_LONG).show();
            }
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Toast.makeText(getApplicationContext(),"Downloading Complete",Toast.LENGTH_SHORT).show();
                }
            };
        });*/

        /*webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });*/
        /*webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url, final String userAgent, String contentDisposition, String mimetype, long contentLength)
            {
                //Checking runtime permission for devices above Marshmallow.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.v(TAG,"Permission is granted");
                        downloadDialog(url,userAgent,contentDisposition,mimetype);
                    } else {
                        Log.v(TAG,"Permission is revoked");
                        //requesting permissions.
                        ActivityCompat.requestPermissions(PopupActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }
                else {
                    //Code for devices below API 23 or Marshmallow
                    Log.v(TAG,"Permission is granted");
                    downloadDialog(url,userAgent,contentDisposition,mimetype);
                }
            }
        });*/
        /*webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));
                request.addRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36");
                request.setDescription("Descargando archivo...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                // Do whatever you want with the download manage request like setting the folder...

                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
            }
        });*/
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimetype);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", userAgent);

                request.setDescription("Downloading file...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Toast.makeText(getApplicationContext(), "Downloading Complete", Toast.LENGTH_SHORT).show();
                }
            };
        });
    }
    private void checkDownloadPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(PopupActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(PopupActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(PopupActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }
    /*public void downloadDialog(final String url,final String userAgent,String contentDisposition,String mimetype)
    {
        //getting filename from url.
        final String filename = URLUtil.guessFileName(url,contentDisposition,mimetype);
        //alertdialog
        AlertDialog.Builder builder=new AlertDialog.Builder(PopupActivity.this);
        //title of alertdialog
        builder.setTitle("Download");
        //message of alertdialog
        builder.setMessage("Do you want to save " +filename);
        //if Yes button clicks.
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //DownloadManager.Request created with url.
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                //cookie
                String cookie=CookieManager.getInstance().getCookie(url);
                //Add cookie and User-Agent to request
                request.addRequestHeader("Cookie",cookie);
                request.addRequestHeader("User-Agent",userAgent);
                //file scanned by MediaScannar
                request.allowScanningByMediaScanner();
                //Download is visible and its progress, after completion too.
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                //DownloadManager created
                DownloadManager downloadManager=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                //Saving files in Download folder
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                //download enqued
                downloadManager.enqueue(request);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //cancel the dialog if Cancel clicks
                dialog.cancel();
            }
        });
        //alertdialog shows.
        builder.create().show();
    }*/

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
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else{
            super.onBackPressed();
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        LogOutTimer.startLogoutTimer(this, this);
        Log.e(TAG, "OnStart () &&& Starting timer");
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if (NetworkAvailable()){
            if(isBackgroundDataAccessAvailable()){

            }else{
                Intent intent = new Intent(PopupActivity.this, No_Internet.class);
                startActivity(intent);
            }
        }else{
            Intent intent = new Intent(PopupActivity.this, No_Internet.class);
            startActivity(intent);
        }

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
}