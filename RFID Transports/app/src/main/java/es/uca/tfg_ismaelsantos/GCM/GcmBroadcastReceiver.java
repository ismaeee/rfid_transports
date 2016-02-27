package es.uca.tfg_ismaelsantos.GCM;

/**
 * Created by Ismael Santos Cabaña on 5/12/15.
 */

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    private final String TAG = "TFG_GcmBoradcast";
    // Receives the broadcast directly from GCM service
    @Override
    public void onReceive(Context context, Intent intent) {

        // Explicitly specify that GcmMessageHandler will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmMessageHandler.class.getName());


        Log.d(TAG,"Get packageName: "+context.getPackageName());
        Log.d(TAG,"Get name: "+GcmMessageHandler.class.getName());
        // Start the service, keeping the device awake while it is executing.
        startWakefulService(context, (intent.setComponent(comp)));
        // Return successful
        setResultCode(Activity.RESULT_OK);
    }


}