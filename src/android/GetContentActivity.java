package com.cesidiodibenedetto.filechooser;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

public class GetContentActivity extends Activity
{
    private static String TAG = "GetContentActivity";
    
    /*
     * this activity will be started if the user touches a notification that we own.
     * We send it's data off to the push plugin for processing.
     * If needed, we boot up the main activity to kickstart the application.
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        
        boolean isGetContentPluginActive = FileChooser.isActive();
        
        Log.d(TAG, "isGetContentPluginActive:" + isGetContentPluginActive);
        processGetContentBundle(isGetContentPluginActive);
        finish();
        
        if (!isGetContentPluginActive) {
            forceMainActivityReload();
        }
    }
    
    /**
     * Takes the pushBundle extras from the intent,
     * and sends it through to the PushPlugin for processing.
     */
    private void processGetContentBundle(boolean isPushPluginActive)
    {
        Bundle extras = getIntent().getExtras();
        
        if (extras != null)	{
            /*Bundle originalExtras = extras.getBundle("pushBundle");
            
            originalExtras.putBoolean("foreground", false);
            originalExtras.putBoolean("coldstart", !isPushPluginActive);
            */
            FileChooser.sendExtras(extras);
            Log.v(TAG, "processGetContentBundle");
        }
    }
    
    /**
     * Forces the main activity to re-launch if it's unloaded.
     */
    private void forceMainActivityReload()
    {
        PackageManager pm = getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());
        startActivity(launchIntent);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        //final NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        //notificationManager.cancelAll();
    }
    
}
