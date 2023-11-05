package com.vpacomercial.sivhn;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.app.DownloadManager;
import android.net.Uri;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.widget.Toast.makeText;
import static androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_DISABLED;
import static androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_ENABLED;
import static androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_WHITELISTED;
import static java.lang.String.format;
import com.vpacomercial.sivhn.R;

import com.vpacomercial.sivhn.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements LogOutTimer.LogOutListener {
    // Start of Declaration of global variables
    ActivityMainBinding binding;

    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private LinearLayout mRootLayout;
    private WebView webView;
    private WebSettings webSettings;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String filePath;
    private String url;
    private Context context = this;
    String cookies = "";

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
//            Manifest.permission.BLUETOOTH_SCAN,
//            Manifest.permission.BLUETOOTH_CONNECT,
//            Manifest.permission.BLUETOOTH_PRIVILEGED,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
    };
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
    };
    // End of Declaration of global variables

    // Start of Request of Permissions
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES);
        int cameraPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int locationPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
//         int BluetoothPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT);
        // Manifest.permission.MODIFY_AUDIO_SETTINGS);
        // int recordPermission = ActivityCompat.checkSelfPermission(activity,
        // Manifest.permission.RECORD_AUDIO);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED
                || cameraPermission != PackageManager.PERMISSION_GRANTED
                || locationPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }



    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            // Log.d(Tag, "Using clearCookies code for API >=" +
            // String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            // Log.d(Tag, "Using clearCookies code for API <" +
            // String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;

            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[] { Uri.parse(mCameraPhotoPath) };
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[] { Uri.parse(dataString) };
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;

        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            if (requestCode == FILECHOOSER_RESULTCODE) {

                if (null == this.mUploadMessage) {
                    return;
                }

                Uri result = null;

                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }

                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;

            }
        }

        return;
    }
    // End of Request of Permissions

    // Start of settings declaration WebViewClient and WebChromeClient
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //verifyStoragePermissions(this);
        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        //webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);

        // improve webview performance
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSaveFormData(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        webView.clearCache(true);
        webView.clearHistory();
        webView.clearFormData();
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.setAcceptFileSchemeCookies(true);
        CookieManager.allowFileSchemeCookies();
        webView.setWebViewClient(new PQClient());
        webView.setWebChromeClient(new MyChrome() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }

        });
        // Device recognition
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        if (width > 1440 || height > 3220) {
            Toast.makeText(MainActivity.this, "Se ha detectado el uso de una Tablet.", Toast.LENGTH_SHORT).show();
            webView.getSettings().setUserAgentString(
                    "Mozilla/5.0 (Linux; Android 8.1.0; Pixel Build/OPM4.171019.021.D1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.109 Mobile Safari/537.36 EdgA/42.0.0.2057");
            setDesktopMode(webView, true);
        } else {
            Toast.makeText(MainActivity.this, "Se ha detectado el uso de un télefono móvil.", Toast.LENGTH_SHORT)
                    .show();
            webView.getSettings().setUserAgentString(
                    "Mozilla/5.0 (Linux; Android 8.1.0; Pixel Build/OPM4.171019.021.D1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.109 Mobile Safari/537.36 EdgA/42.0.0.2057");
            setDesktopMode(webView, false);
        }
        clearCookies(webView.getContext());
        if (savedInstanceState == null) {
            webView.post(new Runnable() {
                @Override

                public void run() {
                    // Variables de Verificacion
                    Intent intent = getIntent();
                     //String AndroidId = intent.getStringExtra("AndroidId");
                    //String AndroidId = Settings.Secure.getString(MainActivity.this.getContentResolver(),Settings.Secure.ANDROID_ID);
                    String AndroidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    String IMEI = intent.getStringExtra("IMEI");
                    String DeviceName = intent.getStringExtra("DeviceName");
                    String build = intent.getStringExtra("Build");
                    String App = "Movil";
                    String APK = "2.6";

                    url = format("https://ventas.baccredomatic.hn/SistemaVentas/Site/Guest/Login.aspx?App=%s&Id=%s&Device=%s&Build=%s&Imei=%s&Apk=%s",
                            App, AndroidId, DeviceName, build, IMEI, APK);
                    if (NetworkAvailable()) {
                        if (isBackgroundDataAccessAvailable()) {
                            webView.loadUrl(url);
                        } else {
                            intent = new Intent(MainActivity.this, No_Internet.class);
                            startActivity(intent);
                        }

                    } else {
                        intent = new Intent(MainActivity.this, No_Internet.class);
                        startActivity(intent);
                    }

                }
            });
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null || url.startsWith("http://") || url.startsWith("https://"))
                    return false;
                try {
                    if (NetworkAvailable()) {
                        if (isBackgroundDataAccessAvailable()) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            view.getContext().startActivity(intent);
                        } else {
                            Intent intent = new Intent(MainActivity.this, No_Internet.class);
                            startActivity(intent);
                        }

                    } else {
                        Intent intent = new Intent(MainActivity.this, No_Internet.class);
                        startActivity(intent);
                    }

                    return true;
                } catch (Exception e) {

                    return true;
                }

            }
        });
        // Software or hardware acceleration
        if (Build.VERSION.SDK_INT >= 21) {
            android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
            webSettings.setMixedContentMode(0);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT < 19) {
            android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        /*
         * webView.setDownloadListener(new DownloadListener() {
         * 
         * @Override
         * public void onDownloadStart(String url, String userAgent, String
         * contentDisposition, String mimeType, long contentLength) {
         * DownloadManager.Request request = new
         * DownloadManager.Request(Uri.parse(url));
         * 
         * request.setMimeType(mimeType);
         * //------------------------COOKIE!!------------------------
         * String cookies = CookieManager.getInstance().getCookie(url);
         * request.addRequestHeader("cookie", cookies);
         * //------------------------COOKIE!!------------------------
         * request.addRequestHeader("User-Agent", userAgent);
         * request.setDescription("Descargando archivo...");
         * request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
         * request.allowScanningByMediaScanner();
         * request.setNotificationVisibility(DownloadManager.Request.
         * VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
         * request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
         * URLUtil.guessFileName(url, contentDisposition, mimeType));
         * DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
         * dm.enqueue(request);
         * Toast.makeText(getApplicationContext(), "Descargando archivo...",
         * Toast.LENGTH_LONG).show();
         * }
         * });
         */

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                    long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimetype);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", userAgent);

                request.setDescription("Downloading file...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        URLUtil.guessFileName(url, contentDisposition, mimetype));
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

    // End of settings declaration WebViewClient and WebChromeClient
    private void checkDownloadPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this,
                    "Write External Storage permission allows us to save files. Please allow this permission in App Settings.",
                    Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 100);
        }
    }

    // Start of WebViewClient
    public class PQClient extends WebViewClient {
        ProgressDialog progressDialog;

        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // If url contains tel link then open Mail Intent
            if (url.contains("tel:")) {

                // Could be cleverer and use a regex
                // Open links in new browser
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_DIAL, Uri.parse(url)));

                // Here we can open new activity

                return true;
                // If url contains a Microsoft Teams Meetings, tries to open MS Teams
            } else if (url.contains("https://web.vortex.data.microsoft.com/")) {
                if (NetworkAvailable()) {
                    if (isBackgroundDataAccessAvailable()) {

                        view.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } else {
                        Intent intent = new Intent(MainActivity.this, No_Internet.class);
                        startActivity(intent);
                    }

                } else {
                    Intent intent = new Intent(MainActivity.this, No_Internet.class);
                    startActivity(intent);
                }
                return true;
            } else {
                // Stay within this webview and load url
                if (NetworkAvailable()) {
                    if (isBackgroundDataAccessAvailable()) {
                        view.loadUrl(url);

                    } else {
                        Intent intent = new Intent(MainActivity.this, No_Internet.class);
                        startActivity(intent);
                    }

                } else {
                    Intent intent = new Intent(MainActivity.this, No_Internet.class);
                    startActivity(intent);
                }
                return true;

            }
        }

        public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
            try {
                webView.stopLoading();
            } catch (Exception e) {

            }
            if (webView.canGoBack()) {
                webView.goBack();
            }
            webView.loadUrl("about:blank");
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("No hay conexión a Internet. Revisa tu conexión a Internet.");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Reintentar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    startActivity(getIntent());
                }
            });
            alertDialog.show();
            super.onReceivedError(webView, errorCode, description, failingUrl);
        }
    }

    // End of WebClient
    // Start of WebChromeClient
    private class MyChrome extends WebChromeClient {

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {
        }

        @Override
        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        @Override
        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        @Override
        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, android.os.Message resultMsg) {
            WebView newWebView = new WebView(MainActivity.this);
            newWebView.getSettings().setJavaScriptEnabled(true);
            // webView.getSettings().setSupportZoom(true);
            // webView.getSettings().setBuiltInZoomControls(true);
            // webView.getSettings().setDisplayZoomControls(false);
            newWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
            newWebView.getSettings().setSupportMultipleWindows(true);

            view.addView(newWebView);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();

            newWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (NetworkAvailable()) {
                        if (isBackgroundDataAccessAvailable()) {
                            Intent intent = new Intent(MainActivity.this, PopupActivity.class);
                            intent.putExtra("Url", url);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MainActivity.this, No_Internet.class);
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(MainActivity.this, No_Internet.class);
                        startActivity(intent);
                    }
                    return true;
                }
            });
            /*
             * Intent intent = new Intent(MainActivity.this,
             * bac.net.sivmovil.PopupActivity.class);
             * intent.putExtra("Url", url);
             * startActivity(intent);
             */
            return true;

        }

        // File upload picker
        // For Android 5.0
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath,
                WebChromeClient.FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", ex);
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("*/*");

            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[] { takePictureIntent };
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "File Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

            return true;

        }

    }

    // End of WebChromeClient
    // Start of File Image creation
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );
        return imageFile;
    }

    // Start of File Image creation
    // Start of extras
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the
        // default
        // system behavior (probably exit the activity)

        return super.onKeyDown(keyCode, event);
    }

    public void setDesktopMode(WebView webView, boolean enabled) {
        String newUserAgent = webView.getSettings().getUserAgentString();
        if (enabled) {
            try {
                String ua = webView.getSettings().getUserAgentString();
                String androidOSString = webView.getSettings().getUserAgentString().substring(ua.indexOf("("),
                        ua.indexOf(")") + 1);
                newUserAgent = webView.getSettings().getUserAgentString().replace(androidOSString,
                        "(X11; Linux x86_64)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            newUserAgent = null;
        }

        webView.getSettings().setUserAgentString(newUserAgent);
        webView.getSettings().setUseWideViewPort(enabled);
        webView.getSettings().setLoadWithOverviewMode(enabled);
        webView.reload();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // End of extras
    // Start of Ignition of Logout
    @Override
    protected void onStart() {
        super.onStart();
        LogOutTimer.startLogoutTimer(this, this);
        Log.e(TAG, "OnStart () &&& Starting timer");
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if (NetworkAvailable()) {
            if (isBackgroundDataAccessAvailable()) {

            } else {
                Intent intent = new Intent(MainActivity.this, No_Internet.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(MainActivity.this, No_Internet.class);
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

    // End of Ignition of Logout
    public boolean NetworkAvailable() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
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

        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
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
            // You can replace it with your name
            return !ipAddr.equals("www.baccredomatic.com");

        } catch (Exception e) {
            return false;
        }
    }
}
