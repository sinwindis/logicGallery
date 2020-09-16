package com.example.nemologic.data;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.example.nemologic.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

//대집도..
//raw xml 파일에서 읽은 level의 데이터를 db에 차곡차곡 쌓는 역할로 바뀔 것
//raw xml 파일은 level을 저장하는 파일 하나, category의 이름들을 저장하는 파일 하나를 사용할 것이다.

public class DataManager {

    public DataManager()
    {
    }

    public static ArrayList<CategoryData> loadCategory(Context ctx)
    {
        InputStream categoryInputStream;

        ArrayList<CategoryData> categories = new ArrayList<>();

        try {
            categoryInputStream = ctx.getResources().openRawResource(R.raw.levels);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(categoryInputStream, "UTF-8"));
            int eventType = parser.getEventType();
            String nameOfCategory = "";
            int numOfLevels = 0;
            int numOfClears = 0;

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();
                        if(startTag.equals("cname")) {
                            nameOfCategory = parser.nextText();
                        }
                        else if(startTag.equals("level"))
                        {
                            numOfLevels++;
                        }
                        else if(startTag.equals("progress"))
                        {
                            if(parser.nextText().equals("2"))
                            {
                                numOfClears++;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if(endTag.equals("category")) {
                            categories.add(new CategoryData(nameOfCategory, numOfLevels, numOfClears));
                            numOfLevels = 0;
                            numOfClears = 0;
                        }
                        break;
                }
                eventType = parser.next();
            }


        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public static ArrayList<LevelData> loadLevel(Context ctx, int categoryIdx)
    {
        InputStream levelInputStream;

        ArrayList<LevelData> levels = new ArrayList<>();

        int categoryNum = 0;
        boolean foundCategory = false;

        String levelName = "";
        int levelWidth = 0;
        int levelHeight = 0;
        String levelData = "";
        int levelProgress = 0;
        String levelSave = "";

        try {
            levelInputStream = ctx.getResources().openRawResource(R.raw.levels);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(levelInputStream, "UTF-8"));
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String startTag = parser.getName();

                        switch (startTag) {
                            case "category":
                                if (categoryNum == categoryIdx) {
                                    //찾으려는 카테고리에 도착했을 때
                                    foundCategory = true;
                                }
                                break;
                            case "lname":
                                if (foundCategory) {
                                    levelName = parser.nextText();
                                }
                                break;
                            case "width":
                                if (foundCategory) {
                                    levelWidth = Integer.parseInt(parser.nextText());
                                }
                                break;
                            case "height":
                                if (foundCategory) {
                                    levelHeight = Integer.parseInt(parser.nextText());
                                }
                                break;
                            case "data":
                                if (foundCategory) {
                                    levelData = parser.nextText();
                                }
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if(endTag.equals("category") && categoryNum == categoryIdx) {
                            //찾으려는 카테고리를 모두 탐색하면 함수를 종료한다.
                            return levels;
                        }
                        else if(endTag.equals("level") && foundCategory)
                        {
                            //해당 레벨의 데이터를 db에 추가한다.
                        }
                        break;
                }
                eventType = parser.next();
            }


        } catch(XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return levels;
    }
}
