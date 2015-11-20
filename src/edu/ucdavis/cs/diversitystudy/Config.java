package edu.ucdavis.cs.diversitystudy;

import java.util.Calendar;

public final class Config
{
    public static final boolean DEBUG = false;

    public static final String UPLOAD_URL = "http://cancer.cs.ucdavis.edu/wsgins/upload.py";
    public static final String POSTPARAM_DATA = "data";
    public static final String POSTPARAM_UDID = "udid";

    public static final String APPDATA_FN = "appdata.csv";
    public static final String SETTINGSDATA_FN = "settingsdata.csv";
    public static final String SDCARDDATA_FN = "sdcarddata.csv";
    public static final String DEVICEINFODATA_FN = "deviceinfo.csv";
    public static final String UPLOADTAR_FN = "collecteddata.tar";

    public static final int NOTIFICATION_ID = 0;
    public static final String NOTIFICATION_TICKERTXT = "PrefStudy Service Started";
    public static final String NOTIFICATION_TITLETXT = "PrefStudy";
    public static final String NOTIFICATION_MSGTXT = "PrefStudy is running.";

    public static final String JAVA_STRINGCLASS = "class java.lang.String";

    public static final String PREFERENCES_NAME = "DiversityPreferences";
    public static final String PREFSKEY_PARTICIPATING = "IsParticipating";
    public static final String PREFSKEY_LASTUPLOADTIME = "LastUploadTime";

    public static final boolean DEFAULT_PARTICIPATING = false;
    public static final long DEFAULT_LASTUPLOADTIME = 1356937027123L; // 12/31/2012

    public static final Calendar DAILY_SCHEDULE = Calendar.getInstance();
    static {
        DAILY_SCHEDULE.set(Calendar.HOUR_OF_DAY, 12);
        DAILY_SCHEDULE.set(Calendar.MINUTE, 0);
        DAILY_SCHEDULE.set(Calendar.SECOND, 0);
    }

}
