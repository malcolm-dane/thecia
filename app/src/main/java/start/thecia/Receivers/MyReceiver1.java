package start.thecia.Receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import start.thecia.Services.TimeService;


public class MyReceiver1 extends BroadcastReceiver {
    Intent intent;
    PendingIntent alarmIntent;

    @Override
    public void onReceive(final Context context, Intent intent) {

          //  Thread B=new Thread(
                  //  new Runnable(){

                     //   public void run(){
                          //  context.startService(serviceIntent1);};;

                  //  } );B.start();


        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {

            final Intent serviceIntent = new Intent(context, TimeService.class);
            Thread a=new Thread(
                    new Runnable(){

                        public void run(){
                            context.startService(serviceIntent);};;

                    } );a.start();
        }
      }}



