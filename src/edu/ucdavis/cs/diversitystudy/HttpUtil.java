package edu.ucdavis.cs.diversitystudy;

import java.io.*;
import java.util.*;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

public final class HttpUtil
{

    private static void MultiPartPost(URI dst, Hashtable<String, AbstractContentBody> parts)
                    throws IOException
    {
        MultipartEntity me = new MultipartEntity();
        for(String name : parts.keySet()) {
            me.addPart(name, parts.get(name));
        }

        HttpPost post = new HttpPost(dst);
        post.setEntity(me);

        HttpClient client = new DefaultHttpClient();
        HttpResponse hr = client.execute(post);

        int status = hr.getStatusLine().getStatusCode();
        if(status != 200) {
            throw new IOException("Post failed with HTTP status: " + status);
        }

        client.getConnectionManager().shutdown();

    }

    public static void UploadFile(URI src, URI dst, String name) throws IOException
    {
        Hashtable<String, AbstractContentBody> parts = new Hashtable<String, AbstractContentBody>();
        parts.put(name, new FileBody(new File(src)));
        MultiPartPost(dst, parts);
    }

    public static void UploadToStudyServer(File tarball, String udid) throws IOException
    {
        Hashtable<String, AbstractContentBody> parts = new Hashtable<String, AbstractContentBody>();

        parts.put(Config.POSTPARAM_UDID, new StringBody(udid));
        parts.put(Config.POSTPARAM_DATA, new FileBody(tarball));

        URI dst = URI.create(Config.UPLOAD_URL);
        MultiPartPost(dst, parts);
    }
}
