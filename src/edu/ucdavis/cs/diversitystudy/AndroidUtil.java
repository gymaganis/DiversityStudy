package edu.ucdavis.cs.diversitystudy;

import java.io.File;
import java.net.URI;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.provider.Settings.Secure;

public final class AndroidUtil
{
    @SuppressWarnings("unused")
    private static final String TAG = AndroidUtil.class.getSimpleName();

    private AndroidUtil()
    {
    }

    private static final AndroidUtil instance = new AndroidUtil();

    private Context ctx = null;

    public static AndroidUtil getInstance()
    {
        return instance;
    }

    public void initialize(Context c)
    {
        this.ctx = c;
    }

    public String get_UDID()
    {
        return Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
    }

    public URI get_internaluri(String fname)
    {
        String fdir = ctx.getFilesDir().getAbsolutePath();
        return URI.create("file:" + fdir + File.separator + fname);
    }

    public File get_internalfile(String fname)
    {
        String fdir = ctx.getFilesDir().getAbsolutePath();
        return new File(fdir + File.separator + fname);
    }

    public void schedule_service()
    {
        Intent i = new Intent(ctx, StudyIntentService.class);
        PendingIntent pi = PendingIntent.getService(ctx, 0, i, 0);

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi); // clear any existing intents

        if(Config.DEBUG) {
            // run in 60 seconds
            am.setRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                60 * 1000,
                pi);

        }
        else {
            am.setRepeating(
                AlarmManager.RTC_WAKEUP,
                Config.DAILY_SCHEDULE.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pi);

        }
    }

    public void cancel_schedule_service()
    {
        Intent i = new Intent(ctx, StudyIntentService.class);
        PendingIntent pi = PendingIntent.getService(ctx, 0, i, 0);

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi); // clear any existing intents
    }

    public void service_set_notification()
    {
        Intent i = new Intent(ctx, StudyIntentService.class);
        PendingIntent ci = PendingIntent.getActivity(ctx, 0, i, 0);

        Notification n = new Notification(
            R.drawable.icon,
            Config.NOTIFICATION_TICKERTXT,
            System.currentTimeMillis());

        n.flags |= Notification.FLAG_ONGOING_EVENT;
        n.setLatestEventInfo(ctx, Config.NOTIFICATION_TITLETXT, Config.NOTIFICATION_MSGTXT, ci);

        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(Config.NOTIFICATION_ID, n);
    }

    public void service_kill_notification()
    {
        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(Config.NOTIFICATION_ID);
    }

    public void service_start()
    {
        Intent i = new Intent(ctx, StudyIntentService.class);
        ctx.startService(i);
    }

    public boolean get_participating()
    {
        SharedPreferences prefs = ctx.getSharedPreferences(
            Config.PREFERENCES_NAME,
            Context.MODE_PRIVATE);

        return prefs.getBoolean(Config.PREFSKEY_PARTICIPATING, Config.DEFAULT_PARTICIPATING);
    }

    public void set_participating(boolean participating)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(
            Config.PREFERENCES_NAME,
            Context.MODE_PRIVATE);

        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(Config.PREFSKEY_PARTICIPATING, participating);
        edit.commit();
    }

    /**
     * 
     * @deprecated See @method check_already_uploaded()
     * 
     */
    public long get_lastuploadtime()
    {
        SharedPreferences prefs = ctx.getSharedPreferences(
            Config.PREFERENCES_NAME,
            Context.MODE_PRIVATE);

        return prefs.getLong(Config.PREFSKEY_LASTUPLOADTIME, Config.DEFAULT_LASTUPLOADTIME);
    }

    public void set_lastuploadtime(long time)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(
            Config.PREFERENCES_NAME,
            Context.MODE_PRIVATE);

        SharedPreferences.Editor edit = prefs.edit();
        edit.putLong(Config.PREFSKEY_LASTUPLOADTIME, time);
        edit.commit();
    }

    public boolean check_already_uploaded()
    {
        SharedPreferences prefs = ctx.getSharedPreferences(
            Config.PREFERENCES_NAME,
            Context.MODE_PRIVATE);

        long lastuploadtime = prefs.getLong(
            Config.PREFSKEY_LASTUPLOADTIME,
            Config.DEFAULT_LASTUPLOADTIME);

        if(Config.DEBUG) {
            long now = System.currentTimeMillis();

            return((now - lastuploadtime) < (1000 * 70));
        }
        else {

            Calendar last = Calendar.getInstance();
            last.setTimeInMillis(lastuploadtime);
            int lday = last.get(Calendar.DAY_OF_YEAR);

            Calendar now = Calendar.getInstance();
            int nday = now.get(Calendar.DAY_OF_YEAR);

            // Log.d(TAG, "LAST UPLOAD DELTA: " + nday + "-" + lday + " = " +
            // (nday - lday));

            // If yesterday was last year i.e., 365 and today is 1 then negative
            return((nday - lday) == 0);
        }
    }
}
