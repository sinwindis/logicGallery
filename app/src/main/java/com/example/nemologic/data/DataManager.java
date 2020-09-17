package com.example.nemologic.data;

import android.content.Context;
import com.example.nemologic.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;

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

        //get version
        String currentVersion = "0";
        //get version

        try {
            categoryInputStream = ctx.getResources().openRawResource(R.raw.categories);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(categoryInputStream, "UTF-8"));
            int eventType = parser.getEventType();
            String version;

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

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<String> categories = getCategoriesFromXml(ctx);

        for(int i = 0; i < categories.size(); i++)
        {
            mDbOpenHelper.insertCategory(categories.get(i));
        }

        mDbOpenHelper.close();

        //카테고리 버전을 업데이트 해준다.
        //~~
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
        String currentVersion = "0";
        //get version

        String levelName = "";
        String categoryName = "";
        int levelWidth = 0;
        int levelHeight = 0;
        String levelData = "";

        String version;

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
    }
}
