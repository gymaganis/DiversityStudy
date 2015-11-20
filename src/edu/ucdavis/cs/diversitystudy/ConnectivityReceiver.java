package edu.ucdavis.cs.diversitystudy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 
 * http://ofps.oreilly.com/titles/9781449390501/Android_Broadcast_Receivers.html
 * 
 */
public class ConnectivityReceiver extends BroadcastReceiver
{
    @SuppressWarnings("unused")
    private static final String TAG = ConnectivityReceiver.class.getSimpleName();

    /**
     * This code can potentially execute while the service is running but that's
     * okay as long as it gets set there when successful.
     * 
     * Since this code can potentially get called frequently, we make several
     * checks first before actually running the service
     * 
     */
    @Override
    public void onReceive(Context ctx, Intent i)
    {
        // No network connectivity. Doing this first assuming it's most
        // lightweight to read the Intent vs. stuff below
        if(i != null && i.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
            return;
        }

        AndroidUtil.getInstance().initialize(ctx);

        // Is participating?
        if(AndroidUtil.getInstance().get_participating() == false) {
            return;
        }

        // Have we uploaded today?
        if(AndroidUtil.getInstance().check_already_uploaded() == true) {
            return;
        }

        // Make sure status is CONNECTED
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null) {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if(ni != null && ni.isConnected()) {
                AndroidUtil.getInstance().service_start();
            }
        }

    }
}
