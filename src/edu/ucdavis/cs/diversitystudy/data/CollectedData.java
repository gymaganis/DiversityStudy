package edu.ucdavis.cs.diversitystudy.data;

import java.io.IOException;
import java.net.URI;

import android.content.Context;

public abstract class CollectedData
{
    protected Context _context;
    protected URI _fileloc;

    public CollectedData(Context c, URI floc)
    {
        _context = c;
        _fileloc = floc;
    }

    public void set_fileloc(URI floc)
    {
        _fileloc = floc;
    }

    public abstract void record() throws IOException;

}
