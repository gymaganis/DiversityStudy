package edu.ucdavis.cs.diversitystudy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;

import org.xeustechnologies.jtar.TarEntry;
import org.xeustechnologies.jtar.TarOutputStream;

import edu.ucdavis.cs.diversitystudy.data.CollectedData;
import edu.ucdavis.cs.diversitystudy.data.DeviceInfoData;
import edu.ucdavis.cs.diversitystudy.data.InstalledAppData;
import edu.ucdavis.cs.diversitystudy.data.SDCardData;
import edu.ucdavis.cs.diversitystudy.data.SettingsData;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class StudyIntentService extends IntentService
{

    private static final String TAG = StudyIntentService.class.getSimpleName();

    public StudyIntentService()
    {
        super(TAG);
    }

    private ArrayList<CollectedData> setup_collection()
    {
        URI uad = AndroidUtil.getInstance().get_internaluri(Config.APPDATA_FN);
        URI usd = AndroidUtil.getInstance().get_internaluri(Config.SETTINGSDATA_FN);
        URI ucd = AndroidUtil.getInstance().get_internaluri(Config.SDCARDDATA_FN);
        URI udd = AndroidUtil.getInstance().get_internaluri(Config.DEVICEINFODATA_FN);

        ArrayList<CollectedData> collectme = new ArrayList<CollectedData>();

        InstalledAppData iad = new InstalledAppData(StudyIntentService.this, uad);
        collectme.add(iad);

        SettingsData std = new SettingsData(StudyIntentService.this, usd);
        collectme.add(std);

        SDCardData scd = new SDCardData(StudyIntentService.this, ucd);
        collectme.add(scd);

        DeviceInfoData did = new DeviceInfoData(StudyIntentService.this, udd);
        collectme.add(did);

        return collectme;
    }

    private File[] files_to_tar()
    {
        File fad = AndroidUtil.getInstance().get_internalfile(Config.APPDATA_FN);
        File fsd = AndroidUtil.getInstance().get_internalfile(Config.SETTINGSDATA_FN);
        File fcd = AndroidUtil.getInstance().get_internalfile(Config.SDCARDDATA_FN);
        File fdd = AndroidUtil.getInstance().get_internalfile(Config.DEVICEINFODATA_FN);

        File[] rval = { fad, fsd, fcd, fdd };
        return rval;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        AndroidUtil.getInstance().initialize(StudyIntentService.this);
    }

    @Override
    protected void onHandleIntent(Intent arg0)
    {
        // Is participating?
        if(AndroidUtil.getInstance().get_participating() == false) {
            return;
        }

        // We need this here since we could have already uploaded per
        // a CONNECTIVITY_CHANGE event.
        // if(AndroidUtil.getInstance().check_already_uploaded() == true) {
        // return;
        // }

        AndroidUtil.getInstance().service_set_notification();

        try {
            // Collection
            ArrayList<CollectedData> tocollect = setup_collection();
            for(CollectedData cd : tocollect) {
                Log.i(TAG, "Processing " + cd.getClass().getSimpleName());
                cd.record();
            }

            // Tarball
            FileOutputStream fos = new FileOutputStream(AndroidUtil.getInstance().get_internalfile(
                Config.UPLOADTAR_FN));

            TarOutputStream tos = new TarOutputStream(new BufferedOutputStream(fos, 8192));
            for(File f : files_to_tar()) {
                tos.putNextEntry(new TarEntry(f, f.getName()));

                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));

                int count = 0;
                byte[] buffer = new byte[8192];
                while((count = bis.read(buffer)) != -1) {
                    tos.write(buffer, 0, count);
                }
                tos.flush();
                bis.close();
            }

            tos.close();

            // Upload
            File tarball = AndroidUtil.getInstance().get_internalfile(Config.UPLOADTAR_FN);
            String udid = AndroidUtil.getInstance().get_UDID();
            HttpUtil.UploadToStudyServer(tarball, udid);

            // Set last upload time
            AndroidUtil.getInstance().set_lastuploadtime(System.currentTimeMillis());

            AndroidUtil.getInstance().service_kill_notification();
        }
        catch(Exception e) {
            AndroidUtil.getInstance().service_kill_notification();
            e.printStackTrace();
        }
    }
}
