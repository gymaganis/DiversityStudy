package edu.ucdavis.cs.diversitystudy.data;

import java.io.*;
import java.net.URI;

import android.content.Context;
import android.os.Environment;

public class SDCardData extends CollectedData
{
    @SuppressWarnings("unused")
    private static final String TAG = SDCardData.class.getSimpleName();

    private static final File SDCARD_ROOT = Environment.getExternalStorageDirectory();

    private void traverse(File curf, PrintWriter pw)
    {
        if(curf.isDirectory()) {
            File[] files = curf.listFiles();
            if(files != null) {
                pw.printf("%s,0,0", curf.getAbsolutePath());
                pw.println();
                pw.flush();

                for(File f : files) {
                    traverse(f, pw);
                }
            }
            else {
                // temp directory returns null
                pw.printf("%s,-1,-1", curf.getAbsolutePath());
                pw.println();
                pw.flush();
            }
        }
        else {
            // is file
            // TODO: compute a sha1 value?
            String abspath = curf.getAbsolutePath();
            long lastmodified = curf.lastModified();
            long size = curf.length();

            pw.printf("%s,%d,%d", abspath, lastmodified, size);
            pw.println();
            pw.flush();
        }
    }

    public SDCardData(Context c, URI floc)
    {
        super(c, floc);
    }

    @Override
    public void record() throws IOException
    {
        PrintWriter pw = new PrintWriter(new File(_fileloc));
        try {
            traverse(SDCARD_ROOT, pw);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        pw.flush();
        pw.close();
    }
}

// protected String normalizeUnicode(String str)
// {
// Normalizer.Form form = Normalizer.Form.NFD;
// if(!Normalizer.isNormalized(str, form)) {
// return Normalizer.normalize(str, form);
// }
// return str;
// }

// private void traverse(File root, PrintWriter pw)
// {
// Stack<File> fstack = new Stack<File>();
//
// fstack.push(root);
// while(!fstack.isEmpty()) {
// File curf = fstack.pop();
//
// if(curf.isDirectory()) {
// // File[] files = curf.listFiles();
//
// String[] files = curf.list();
// if(files != null) {
// pw.printf("%s,0,0", curf.getAbsolutePath());
// pw.println();
//
// // for(File f : files) {
// // fstack.push(f);
// // }
//
// for(String f : files) {
// String fnorm = normalizeUnicode(f);
// String fullpath = curf.getAbsolutePath() + File.separator + fnorm;
// fstack.push(new File(fullpath));
// }
// }
// else {
// // temp directory returns null
// pw.printf("%s,-1,-1", curf.getAbsolutePath());
// pw.println();
// }
// }
// else {
// // is file
// // TODO: compute a sha1 value?
// String abspath = curf.getAbsolutePath();
// long lastmodified = curf.lastModified();
// long size = curf.length();
//
// pw.printf("%s,%d,%d", abspath, lastmodified, size);
// pw.println();
// }
// }
//
// }
