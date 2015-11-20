package edu.ucdavis.cs.diversitystudy.data;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

public class SettingsData extends CollectedData
{
    @SuppressWarnings("unused")
    private static final String TAG = SettingsData.class.getSimpleName();

    private boolean is_string_settingname(Field f)
    {
        int mods = f.getModifiers();
        return Modifier.isPublic(mods)
               && Modifier.isStatic(mods)
               && Modifier.isFinal(mods)
               && f.getType().toString().equals("class java.lang.String");
    }

    public void record_settings(Class<?> sc, PrintWriter pw)
    {
        Field[] fields = sc.getDeclaredFields();
        for(Field f : fields) {
            if(is_string_settingname(f)) {
                try {
                    Method gs = sc.getMethod("getString", ContentResolver.class, String.class);
                    String val = (String) gs.invoke(
                        sc,
                        _context.getContentResolver(),
                        (String) f.get(sc));

                    pw.printf("%s, %s", f.getName(), val);
                    pw.println();
                    pw.flush();
                }
                catch(Exception e) {
                    pw.printf("ERROR: %s,%s", f.getName(), e.toString());
                    pw.println();
                    pw.flush();
                    e.printStackTrace();
                }
            }
        }
    }

    public SettingsData(Context c, URI floc)
    {
        super(c, floc);
    }

    @Override
    public void record() throws IOException
    {
        final Class<?>[] settings_classes = { Settings.System.class, Settings.Secure.class };

        PrintWriter pw = new PrintWriter(new File(_fileloc));

        for(Class<?> sc : settings_classes) {
            record_settings(sc, pw);
        }

        pw.flush();
        pw.close();
    }
}
