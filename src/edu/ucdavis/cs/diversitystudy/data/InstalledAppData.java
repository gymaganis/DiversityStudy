package edu.ucdavis.cs.diversitystudy.data;

import java.io.*;
import java.util.*;
import java.net.URI;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.Context;

public final class InstalledAppData extends CollectedData
{
    @SuppressWarnings("unused")
    private static final String TAG = InstalledAppData.class.getSimpleName();

    public InstalledAppData(Context c, URI floc)
    {
        super(c, floc);
    }

    @Override
    public void record() throws IOException
    {
        PackageManager pm = _context.getPackageManager();
        List<PackageInfo> pkgs = pm.getInstalledPackages(0);

        PrintWriter pw = new PrintWriter(new File(_fileloc));

        for(PackageInfo pi : pkgs) {
            // We can opt to not to record System apps
            // i.e., pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM
            // But, we'll collect all for now
            String pkgname = pi.packageName;
            String versionname = pi.versionName;
            int versioncode = pi.versionCode;

            String appname = "";
            CharSequence anc = pi.applicationInfo.loadLabel(pm);
            if(anc != null) {
                appname = anc.toString();
            }

            String description = "";
            CharSequence dc = pi.applicationInfo.loadDescription(pm);
            if(dc != null) {
                description = dc.toString();
            }

            pw.printf("%s,%s,%d,%s,%s", pkgname, versionname, versioncode, appname, description);
            pw.println();
            pw.flush();
        }

        pw.flush();
        pw.close();
    }

}
