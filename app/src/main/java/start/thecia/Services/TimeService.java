package start.thecia.Services;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

import android.util.Log;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraService;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;

import start.thecia.Receivers.MyReceiver;



import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class TimeService extends HiddenCameraService implements SensorEventListener,Runnable {

   TimeService1 timeService;
    Handler S=new Handler();
    Handler a = new Handler();
    File imageFile;
    Thread B;
    Thread mThread;
    Runnable r;
    Runnable m;
    CameraManager aCamera;
    int rCounter=0;
    int mCounter=0;
    String CameraId;
    // stopSelf();
    //picgettter();
    Bundle savedInstanceState;
    Timer timer=new Timer();
    private boolean isBusy;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    CameraDevice cDevice;
    Intent ab=new Intent();
    IntentFilter AB=new IntentFilter("start.spylog.2mins");
    IntentFilter AC=new IntentFilter("start.spylog.turnOff");
int amtOfPics;
  static  boolean startedSecondActivity;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(this).start();
        registerReceiver(new MyReceiver(),AB);
        registerReceiver(new TimeService1(),AC);
        startedSecondActivity=false;
        CameraConfig cameraConfig = new CameraConfig()
                .getBuilder(getApplicationContext())
                .setCameraFacing(CameraFacing.REAR_FACING_CAMERA)
                .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                .build();
        Log.i("TimeService", "k");
        isBusy=false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI, new Handler());
        return START_STICKY;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i("snsr","sensor changed");
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];


        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter

        if (mAccel >= 2) {
            if(!isBusy){
                showNotification();

if(startedSecondActivity!=true){
    sendBroadcast(ab.setAction("start.spylog.2mins"));
    startedSecondActivity=true;}
    }}}

    /**
     * show notification when Accel is more then the given int.
     */
    private void showNotification() {
        isBusy=true;


        m = new Runnable() {

            @Override
            public void run() {

                stopCamera();
                CameraConfig cameraConfig = getRearConfig(aCamera,cDevice);
                startCamera(cameraConfig);

                try{takePicture();}catch(RuntimeException e){
                    a.removeCallbacks(this);

                };
                Log.i("REAR", "");
                cameraConfig = null;;
                rCounter++;
                if(rCounter<=3){

                    a.postDelayed(r,5000);
                }
                else{a.removeCallbacks(this);isBusy=false;rCounter=0; }
            }};
        r=new Runnable() {
            @Override
            public void run() {

                stopCamera();
                final CameraConfig cameraConfig1 = getFrontConfig(aCamera,cDevice);
                startCamera(cameraConfig1);
                try{takePicture();}catch(RuntimeException e) {
                    a.removeCallbacks(this);
                }
                final Thread thread=new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    { mCounter++;
                        if(mCounter<=3){

                            a.postDelayed(m,4000);}
                        else{a.removeCallbacks(this);isBusy=false;mCounter=0;  }
                    }});
                thread.start();//thread.run();
            }};
        final Runnable runner = new Runnable() {
            @Override
            public void run() {
                Thread ab = new Thread(new Runnable() {
                    public void run() {
                        a.post(m);
                    }

                    ;
                });
                ab.start();
            }};

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        {

            if (HiddenCameraUtils.canOverDrawOtherApps(this))
            {
                a.post(runner);

            }
            else {
                //Open settings to grant permission for "Draw other apps".
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
            }
        }}

    @Override
    public void onImageCapture(@NonNull final File imageFile)
    {
        B=new Thread(new Runnable(){
            @Override
            public void run(){
                File file = openFileForImage();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

                Log.d("Image capture", imageFile.length() + "");
                if (bitmap != null) {
                    FileOutputStream outStream = null;
                    try {
                        outStream = new FileOutputStream(file);
                        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)) {
                            Log.i("", "Unable to save image to file.");

                        } else {
                            Log.i("save image to file.", file.getPath());

                        }
                        outStream.close();

                    }
                    catch (Exception e) {
                    }
                }}});//end of thread
        B.start();//call the thread;
    }

    public CameraConfig getRearConfig(final CameraManager a, final CameraDevice cDevice)throws RuntimeException {
        try{
            CameraConfig cameraConfig = new CameraConfig()
                    .getBuilder(getApplicationContext())
                    .setCameraFacing(CameraFacing.REAR_FACING_CAMERA)
                    .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                    .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                    .build();
            startCamera(cameraConfig);
            return cameraConfig;}catch(RuntimeException e){

            a.registerAvailabilityCallback(new CameraManager.AvailabilityCallback() {
                @Override
                public void onCameraUnavailable(@NonNull String cameraId) {
                    super.onCameraUnavailable(cameraId);cDevice.close();
                    a.registerAvailabilityCallback( new CameraManager.AvailabilityCallback(){
                        @Override
                        public void onCameraAvailable(String cameraId){
                            super.onCameraAvailable(cameraId);
                            getRearConfig(a,cDevice); Log.i("Camera isn't null","nit");}},new android.os.Handler());};},new android.os.Handler());;
        }return this.getRearConfig(aCamera,cDevice);}

    public CameraConfig getFrontConfig(final CameraManager a, final CameraDevice cDevice)throws RuntimeException {
        try{
            CameraConfig cameraConfig = new CameraConfig()
                    .getBuilder(getApplicationContext())
                    .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                    .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                    .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                    .build();
            startCamera(cameraConfig);
            return cameraConfig;}catch(RuntimeException e){

            a.registerAvailabilityCallback(new CameraManager.AvailabilityCallback() {
                @Override
                public void onCameraUnavailable(@NonNull String cameraId) {
                    super.onCameraUnavailable(cameraId);cDevice.close();
                    a.registerAvailabilityCallback( new CameraManager.AvailabilityCallback(){
                        @Override
                        public void onCameraAvailable(String cameraId){
                            super.onCameraAvailable(cameraId);
                            getRearConfig(a,cDevice); Log.i("Camera isn't null","not null");}},new android.os.Handler());};},new android.os.Handler());;
        }return this.getFrontConfig(aCamera,cDevice);}

    private File openFileForImage() {
        File imageDirectory = null;
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            imageDirectory = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "aLog");
            if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
                imageDirectory = null;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_mm_dd_hh_mm_ss",
                        Locale.getDefault());

                return new File(imageDirectory.getPath() +
                        File.separator + "image_" +
                        dateFormat.format(new Date()) + ".jpeg");
            }
        }
        return null;
    }

    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera

                Toast.makeText(this, "Cannot open camera.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camra permission before initializing it.
                Toast.makeText(this, "Camera permission not available.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Toast.makeText(this, "Your device does not have front camera.", Toast.LENGTH_LONG).show();
                break;
        }

        ;
    }

    @Override
    public void run() {

    }

    public static class TimeService1 extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equalsIgnoreCase("com.thecia.turnOff")){
                startedSecondActivity=false;
            }
        }
    }

}