package com.nas2skupa.do12;

 
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
 
public class SplashScreen extends Activity {
    @SuppressLint("NewApi") @Override
    public void onCreate(Bundle savedInstanceState) {
		getActionBar().setIcon(android.R.color.transparent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        new Handler().postDelayed(new Runnable() {
 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, LoginScreen.class);
                startActivity(i);
 
                // close this activity
                finish();
            }
        }, 3000);
     }
    
    
    public void nextScreen(View view){
    	Intent i = new Intent(this, LoginScreen.class);
    	startActivity(i);
    	
    }
}