package start.thecia.Services;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

import android.util.Log;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.HiddenCameraUtils;
import com.github.piasy.rxandroidaudio.AudioRecorder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import start.thecia.Receivers.MyReceiver;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import androidx.annotation.Nullable;

/**
 * Created by temp on 4/10/2017.
 */
public class A extends Service implements Runnable {
    Handler S=new Handler();
    Handler a = new Handler();
    File imageFile;
    Thread B;
    Thread mThread;
    // stopSelf();
    //picgettter();
        BeeperControl aBeep;
                 boolean inService;
    private final int serviceId=0;
    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
   // private int NOTIFICATION = start.thecia.R.string.local_service_started;
    private Notification mNM=new Notification();
    Timer timer=new Timer();
    AudioRecorder  mAudioRecorder;
    CameraConfig cameraConfig;
    boolean isBusy=false;
private String timeToRecrd;
private int aTime;
    private String interval;
    private int aTime2;
private boolean preventKill;
String getNoKill;
private String intentAction="com.thecia.turnOff";
    private IntentFilter aNewFilter=new IntentFilter(intentAction);
private final Intent sendThis=new Intent().setAction(intentAction);
    IntentFilter newFilter=new IntentFilter("start.thecia.Specific");


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) throws NullPointerException{
       // registerReceiver(new MyReceiver(),newFilter);
        preventKill=true;
   try{
if (intent.hasExtra("interval")){
        aTime2=intent.getIntExtra("interval",0);
       aTime=intent.getIntExtra("RunTime",0);

}}

//else {       aTime2=5;
   // aTime=5;}

  // }

   catch(NullPointerException  e){
  Log.i("error!!!!!!","null error ib===");


        }

        if(aBeep==null)
              {
                 if(aTime==0&&aTime2==0){aTime=5;aTime2=30;Log.i("defaults","5 minutes and 30 sec intervals");}
                  Log.i("starting","AamBusy");
                  inService=true;
                  aBeep=new BeeperControl();
                  aBeep.beepForAnHour(aTime,aTime2);} else{ Log.i("already instance","recording");}



                //Open settings to grant permission for "Draw other apps".
            HiddenCameraUtils.openDrawOverPermissionSetting(this);

        return START_STICKY;}


    public void recordingNow(){
        Log.i("running","recording");   isBusy=true;   mAudioRecorder = AudioRecorder.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_mm_dd_hh_mm_ss", Locale.getDefault());
        File mAudioFile = openFileFor();
        mAudioRecorder.prepareRecord(MediaRecorder.AudioSource.MIC,
                MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.AudioEncoder.AAC,
                mAudioFile);

        mAudioRecorder.startRecord();
    }

    void Stopping(){mAudioRecorder.stopRecord();isBusy=false;   Log.i("running","stopped");if(inService){ recordingNow();}else{ Log.i("final stop called","finalStopp");sendBroadcast(sendThis);}}

    public void recordIt(){
        if(!isBusy){
            recordingNow();}
        if(isBusy){Stopping();}

        else{recordingNow();}}



    private File openFileFor() {
        File imageDirectory = null;
        String storageState = Environment.getExternalStorageState();

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
    if(preventKill){
        Intent anew=new Intent();
anew.setAction("start.thecia.Specific");
        anew.putExtra("RunTime",aTime);
        anew.putExtra("interval",aTime2);

        sendBroadcast(anew);
    }

    }
    @Override
    public void run() {
        Thread aThread=new Thread(A.this);
        aThread.start();
    }

    public class BeeperControl {
        private final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        public void beepForAnHour(int a,int B) {
                   inService=true;
            final Runnable beeper = new Runnable() {
                public void run() {  Log.i("running","beep for a hr");recordIt(); }
            };
            final ScheduledFuture<?> beeperHandle =
                    scheduler.scheduleAtFixedRate(beeper,0,B,SECONDS);
            scheduler.schedule(new Runnable() {
                public void run() { beeperHandle.cancel(true); inService=false;Stopping();stopSelf();}
            }, a, MINUTES);
        }
    }


    }


