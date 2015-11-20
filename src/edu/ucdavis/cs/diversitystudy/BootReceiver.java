package edu.ucdavis.cs.diversitystudy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver
{
    @SuppressWarnings("unused")
    private static final String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        AndroidUtil.getInstance().initialize(context);
        AndroidUtil.getInstance().schedule_service();
    }

}
