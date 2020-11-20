package com.example.nemologic.data;

import android.content.Context;
import android.util.Log;

import com.example.nemologic.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class StringGetter {

    public static ArrayList<String> a_name;
    public static ArrayList<String> p_name;

    public StringGetter()
    {
    }

    public static void loadData(Context ctx)
    {

        a_name = new ArrayList<>();
        p_name = new ArrayList<>();

        InputStream levelInputStream;

        try {
            levelInputStream = ctx.getResources().openRawResource(R.raw.a_name);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(levelInputStream, "UTF-8"));
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT)
            {
                if (eventType == XmlPullParser.START_TAG) {
                    String startTag = parser.getName();
                    if(startTag.equals("name"))
                    {
                        a_name.add(parser.nextText());
                    }
                }
                eventType = parser.next();
            }


        } catch(XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        try {
            levelInputStream = ctx.getResources().openRawResource(R.raw.p_name);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(levelInputStream, "UTF-8"));
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT)
            {
                if (eventType == XmlPullParser.START_TAG) {
                    String startTag = parser.getName();
                    if(startTag.equals("name"))
                    {
                        p_name.add(parser.nextText());
                    }
                }
                eventType = parser.next();
            }


        } catch(XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        Log.d("StringGetter", "load end");
    }
}
