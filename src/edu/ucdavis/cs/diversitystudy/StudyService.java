//package edu.ucdavis.cs.diversitystudy;
//
//import java.net.URI;
//import java.util.*;
//import java.io.*;
//
//import org.xeustechnologies.jtar.TarEntry;
//import org.xeustechnologies.jtar.TarOutputStream;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//import android.util.Log;
//
///**
// * @deprecated We use StudyIntentService which kills itself automatically
// * 
// */
//public class StudyService extends Service
//{
//    private static final String TAG = StudyService.class.getSimpleName();
//
//    private ArrayList<CollectedData> setup_collection()
//    {
//        URI uad = AndroidUtil.get_internaluri(StudyService.this, Config.APPDATA_FN);
//        URI usd = AndroidUtil.get_internaluri(StudyService.this, Config.SETTINGSDATA_FN);
//        URI ucd = AndroidUtil.get_internaluri(StudyService.this, Config.SDCARDDATA_FN);
//        URI udd = AndroidUtil.get_internaluri(StudyService.this, Config.DEVICEINFODATA_FN);
//
//        ArrayList<CollectedData> collectme = new ArrayList<CollectedData>();
//
//        InstalledAppData iad = new InstalledAppData(StudyService.this, uad);
//        collectme.add(iad);
//
//        SettingsData std = new SettingsData(StudyService.this, usd);
//        collectme.add(std);
//
//        SDCardData scd = new SDCardData(StudyService.this, ucd);
//        collectme.add(scd);
//
//        DeviceInfoData did = new DeviceInfoData(StudyService.this, udd);
//        collectme.add(did);
//
//        return collectme;
//    }
//
//    private File[] files_to_tar()
//    {
//        File fad = AndroidUtil.get_internalfile(StudyService.this, Config.APPDATA_FN);
//        File fsd = AndroidUtil.get_internalfile(StudyService.this, Config.SETTINGSDATA_FN);
//        File fcd = AndroidUtil.get_internalfile(StudyService.this, Config.SDCARDDATA_FN);
//        File fdd = AndroidUtil.get_internalfile(StudyService.this, Config.DEVICEINFODATA_FN);
//
//        File[] rval = { fad, fsd, fcd, fdd };
//        return rval;
//    }
//
//    private Thread task = null;
//    private Runnable task_runnable = new Runnable()
//    {
//        @Override
//        public void run()
//        {
//            try {
//
//                // Collection
//                ArrayList<CollectedData> tocollect = setup_collection();
//                for(CollectedData cd : tocollect) {
//                    Log.i(TAG, "Processing " + cd.getClass().getSimpleName());
//                    cd.record();
//                }
//
//                // Tarball
//                FileOutputStream fos = new FileOutputStream(AndroidUtil.get_internalfile(
//                    StudyService.this,
//                    Config.UPLOADTAR_FN));
//
//                TarOutputStream tos = new TarOutputStream(new BufferedOutputStream(fos, 8192));
//                for(File f : files_to_tar()) {
//                    tos.putNextEntry(new TarEntry(f, f.getName()));
//
//                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
//
//                    int count = 0;
//                    byte[] buffer = new byte[8192];
//                    while((count = bis.read(buffer)) != -1) {
//                        tos.write(buffer, 0, count);
//                    }
//                    tos.flush();
//                    bis.close();
//                }
//
//                tos.close();
//
//                // Upload
//                File tarball = AndroidUtil.get_internalfile(StudyService.this, Config.UPLOADTAR_FN);
//                String udid = AndroidUtil.get_UDID(StudyService.this);
//                HttpUtil.UploadToStudyServer(tarball, udid);
//
//                StudyService.this.stopSelf();
//
//            }
//            catch(Exception e) {
//                e.printStackTrace();
//                StudyService.this.stopSelf();
//            }
//        }
//
//    };
//
//    @Override
//    public int onStartCommand(Intent i, int flags, int startId)
//    {
//        if(!task.isAlive()) {
//            task.run();
//        }
//
//        AndroidUtil.service_set_notification(StudyService.this);
//
//        return START_NOT_STICKY;
//    }
//
//    @Override
//    public void onCreate()
//    {
//        super.onCreate();
//
//        task = new Thread(task_runnable);
//        task.setDaemon(true);
//    }
//
//    @Override
//    public void onDestroy()
//    {
//        super.onDestroy();
//
//        task = null;
//
//        AndroidUtil.service_kill_notification(StudyService.this);
//    }
//
//    @Override
//    public IBinder onBind(Intent arg0)
//    {
//        return null;
//    }
//
// }
