package com.skynet.hunter.core10200116;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by hunter on 24/1/16.
 */
public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle simple_variable) {
        super.onCreate(simple_variable);
        setContentView(R.layout.splash);
        Thread timer = new Thread() {
            public void run(){
                try{
                    sleep(5000);

                } catch (InterruptedException e){
                    e.printStackTrace();

                }finally {
                    Intent openStartingPoint = new Intent("com.skynet.hunter.core10200116.MAINACTIVITY");
                    startActivity(openStartingPoint);

                }


            }
        };
        timer.start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


}
