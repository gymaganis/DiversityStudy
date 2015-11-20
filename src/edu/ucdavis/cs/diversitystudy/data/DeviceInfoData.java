package edu.ucdavis.cs.diversitystudy.data;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;

import edu.ucdavis.cs.diversitystudy.Config;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

public class DeviceInfoData extends CollectedData
{
    @SuppressWarnings("unused")
    private static final String TAG = DeviceInfoData.class.getSimpleName();

    // TODO: Put in ReflectUtil
    private boolean is_psfstring(Field f)
    {
        int mods = f.getModifiers();
        return Modifier.isPublic(mods)
               && Modifier.isStatic(mods)
               && Modifier.isFinal(mods)
               && f.getType().toString().equals(Config.JAVA_STRINGCLASS);

    }

    private boolean has_stringrval(Method m)
    {
        return m.getReturnType().toString().equals(Config.JAVA_STRINGCLASS);
    }

    private boolean has_noparams(Method m)
    {
        return m.getParameterTypes().length == 0;
    }

    public DeviceInfoData(Context c, URI floc)
    {
        super(c, floc);
    }

    @Override
    public void record() throws IOException
    {
        PrintWriter pw = new PrintWriter(new File(_fileloc));

        // TelephonyManager info
        TelephonyManager tm = (TelephonyManager) _context.getSystemService(Context.TELEPHONY_SERVICE);

        Class<?> tc = TelephonyManager.class;
        Method[] methods = tc.getDeclaredMethods();
        for(Method m : methods) {
            if(has_stringrval(m) && has_noparams(m)) {
                try {
                    String mname = m.getName();
                    String mrval = (String) m.invoke(tm);

                    pw.printf("%s,%s", mname, mrval);
                    pw.println();
                    pw.flush();
                }
                catch(Exception e) {
                    pw.printf("ERROR: %s,%s", m.getName(), e.toString());
                    pw.println();
                    pw.flush();
                    e.printStackTrace();
                }
            }
        }

        // android.os.Build info
        Class<?> bc = Build.class;
        Field[] fields = bc.getDeclaredFields();
        for(Field f : fields) {
            if(is_psfstring(f)) {
                try {
                    String fname = f.getName();
                    String fval = (String) f.get(bc);

                    pw.printf("%s,%s", fname, fval);
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

        pw.flush();
        pw.close();
    }
}
