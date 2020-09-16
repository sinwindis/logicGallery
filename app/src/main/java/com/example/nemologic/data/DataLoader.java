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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class DataLoader {

    public DataLoader()
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
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if(endTag.equals("category")) {
                            categories.add(new CategoryData(nameOfCategory, numOfLevels));
                            numOfLevels = 0;
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

    private ArrayList<LevelData> loadLevel(Context ctx, int categoryIdx)
    {
        InputStream levelInputStream;

        ArrayList<LevelData> levels = new ArrayList<>();

        int categoryNum = 0;
        boolean foundCategory = false;

        String levelName = "";
        int levelWidth = 0;
        int levelHeight = 0;
        String levelData = "";

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
                            //찾으려는 카테고리고, 한 개의 레벨 데이터를 모두 로드하면 카테고리데이터에 해당 레벨을 추가한다.
                            levels.add(new LevelData(levelName, levelWidth, levelHeight, levelData));
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
