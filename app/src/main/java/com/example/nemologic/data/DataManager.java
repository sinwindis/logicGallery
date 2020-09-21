package com.example.nemologic.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.nemologic.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

//대집도..
//raw xml 파일에서 읽은 level의 데이터를 db에 차곡차곡 쌓는 역할로 바뀔 것
//db는 카테고리의 이름을 저장하는 테이블 하나, 각 카테고리별 레벨을 저장하는 테이블 카테고리 개수만큼, 빅 퍼즐의 이름을 저장하는 테이블 하나, 각 빅 퍼즐의 레벨을 저장하는 테이블 빅퍼즐 개수만큼
//raw xml 파일은 level을 저장하는 파일 하나, category의 이름들을 저장하는 파일 하나를 사용할 것이다.

public class DataManager {


    public DataManager()
    {
    }

    public static ArrayList<String> getCategoriesFromXml(Context ctx)
    {
        InputStream categoryInputStream;
        ArrayList<String> categories = new ArrayList<>();

        try {
            categoryInputStream = ctx.getResources().openRawResource(R.raw.categories);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(categoryInputStream, "UTF-8"));
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String startTag = parser.getName();
                    if (startTag.equals("name")) {
                        categories.add(parser.nextText());
                    }
                }
                eventType = parser.next();
            }


        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return categories;
    }


    public static void loadCategory(Context ctx)
    {
        //xml파일에 저장된 데이터를 category db에 저장합니다.
        //게임의 버전을 인식하여 추가해야 할 카테고리만 자동으로 추가합니다.

        InputStream categoryInputStream;
        ArrayList<String> categories = new ArrayList<>();

        //get version
        SharedPreferences versionSavePref = ctx.getSharedPreferences("VERSION", MODE_PRIVATE);

        //마지막 플레이한 레벨의 이름과 카테고리를 받아온다.
        final String categoryVersion = versionSavePref.getString("ver_category", "0");
        String currentVersion = categoryVersion;

        String version = "0";

        //get version

        try {
            categoryInputStream = ctx.getResources().openRawResource(R.raw.categories);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(categoryInputStream, "UTF-8"));
            int eventType = parser.getEventType();


            while(eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String startTag = parser.getName();
                    if (startTag.equals("version")) {
                        version = parser.nextText();
                        if(version.equals(currentVersion))
                        {
                            //해당 태그 아래부터 카테고리 명을 insert하기 시작한다.
                            eventType = parser.next();
                            break;
                        }

                    }
                }
                eventType = parser.next();
            }

            while(eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String startTag = parser.getName();
                    if (startTag.equals("name")) {
                        categories.add(parser.nextText());
                    }
                    else if(startTag.equals("version"))
                    {
                        version = parser.nextText();
                    }
                }
                eventType = parser.next();
            }


        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < categories.size(); i++)
        {
            mDbOpenHelper.insertCategory(categories.get(i));
        }

        mDbOpenHelper.close();

        //카테고리 버전을 sharedPreferences에 업데이트 해준다.
        SharedPreferences.Editor editor = versionSavePref.edit();
        editor.putString("ver_category", version);

        editor.apply();
    }

    public static void loadLevel(Context ctx)
    {
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        InputStream levelInputStream;

        //get version
        SharedPreferences versionSavePref = ctx.getSharedPreferences("VERSION", MODE_PRIVATE);
        final String levelVersion = versionSavePref.getString("ver_level", "0");
        //get version
        Log.d("loadLevel", "sp ver_level: " + levelVersion);

        String levelName = "";
        String categoryName = "";
        int levelWidth = 0;
        int levelHeight = 0;
        String levelData = "";

        String version = "0";

        try {
            levelInputStream = ctx.getResources().openRawResource(R.raw.levels);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(levelInputStream, "UTF-8"));
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String startTag = parser.getName();
                    if (startTag.equals("version")) {
                        version = parser.nextText();
                        if(version.equals(levelVersion))
                        {
                            //해당 태그 아래부터 카테고리 명을 insert하기 시작한다.
                            eventType = parser.next();
                            break;
                        }
                    }
                }
                eventType = parser.next();
            }

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();

                        switch (startTag) {

                            case "lname":
                                levelName = parser.nextText();
                                break;
                            case "cname":
                                categoryName = parser.nextText();
                                break;
                            case "width":
                                levelWidth = Integer.parseInt(parser.nextText());
                                break;
                            case "height":
                                levelHeight = Integer.parseInt(parser.nextText());
                                break;
                            case "data":
                                levelData = parser.nextText();
                                break;
                            case "version":
                                version = parser.nextText();
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if(endTag.equals("level"))
                        {
                            //해당 레벨의 데이터를 db에 추가한다.
                            mDbOpenHelper.insertLevel(levelName, categoryName, levelWidth, levelHeight, levelData);
                        }
                        break;
                }
                eventType = parser.next();
            }


        } catch(XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        mDbOpenHelper.close();

        //버전을 갱신해 준다.
        SharedPreferences.Editor editor = versionSavePref.edit();
        editor.putString("ver_level", version);

        editor.apply();

        Log.d("DataManager", "saved Level version: " + version);

    }
}
