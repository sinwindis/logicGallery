package com.sinwindis.logicgallery.data;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.sinwindis.logicgallery.R;
import com.sinwindis.logicgallery.data.sharedpref.VersionPreference;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class DbManager {

    Context ctx;

    public DbManager(Context ctx) {
        this.ctx = ctx;
    }

    private String getLatestVersion(XmlPullParser parser) {
        int eventType;
        String latestVersion;
        try {
            eventType = parser.getEventType();

            //최신 버전 체크
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String startTag = parser.getName();
                    if (startTag.equals("lastv")) {
                        latestVersion = parser.nextText();
                        parser.next();
                        parser.next();
                        return latestVersion;
                    }
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private int loadArtist(XmlPullParser parser) throws IOException, XmlPullParserException {

        //artist
        parser.next();
        //artist text
        parser.next();
        //a_id startTag

        //a_id
        int a_id = Integer.parseInt(parser.nextText());

        Log.d("loadArtist", "a_id: " + a_id);

        //a_id text
        parser.next(); //a_id end tag
        parser.next(); //puzzle start tag

        return a_id;
    }

    private int loadPuzzle(XmlPullParser parser, int a_id) throws IOException, XmlPullParserException {
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //puzzle
        parser.nextTag();

        //p_id
        int p_id = Integer.parseInt(parser.nextText());
        Log.d("loadPuzzle", "p_id: " + p_id);
        parser.nextTag();
        //width
        int p_width = Integer.parseInt(parser.nextText());
        Log.d("loadPuzzle", "p_width: " + p_width);
        parser.nextTag();
        //height
        int p_height = Integer.parseInt(parser.nextText());
        Log.d("loadPuzzle", "p_height: " + p_height);
        parser.nextTag();
        //width
        int l_width = Integer.parseInt(parser.nextText());
        Log.d("loadPuzzle", "l_width: " + l_width);
        parser.nextTag();
        //height
        int l_height = Integer.parseInt(parser.nextText());
        Log.d("loadPuzzle", "l_height: " + l_height);
        parser.nextTag();
        //colorset
        String p_colorSet = parser.nextText();
        Log.d("loadPuzzle", "p_colorSet: " + p_colorSet);
        parser.nextTag();
        //level start tag

        byte[] p_colorSetByteArray = Base64.decode(p_colorSet, Base64.DEFAULT);

        //해당 퍼즐의 데이터를 db 에 추가한다.
        int insertId = (int) mDbOpenHelper.insertBigPuzzle(p_id, a_id, p_width, p_height, l_width, l_height, p_colorSetByteArray);

        Log.d("loadPuzzle", "insert ID: " + insertId);

        mDbOpenHelper.close();

        return insertId;
    }

    private int loadLevel(XmlPullParser parser, int puzzleId) throws IOException, XmlPullParserException {

        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //level
        parser.nextTag();

        Log.d("loadLevel", "getLineNumber(): " + parser.getLineNumber());
        Log.d("loadLevel", "getName(): " + parser.getName());
        Log.d("loadLevel", "getText(): " + parser.getText());


        //number
        int number = Integer.parseInt(parser.nextText());
        Log.d("loadLevel", "level number: " + number);
        parser.nextTag();
        //width
        int width = Integer.parseInt(parser.nextText());
        Log.d("loadLevel", "level width: " + width);
        parser.nextTag();
        //height
        int height = Integer.parseInt(parser.nextText());
        Log.d("loadLevel", "level height: " + height);
        parser.nextTag();
        //dataset
        String dataSet = parser.nextText();
        Log.d("loadLevel", "level dataSet: " + dataSet);
        parser.nextTag();
        //colorset
        String colorSet = parser.nextText();
        Log.d("loadLevel", "level colorSet: " + colorSet);

        //모든 데이터를 받아왔으면 해당 레벨을 db 에 insert 해준다.

        byte[] dataBlob = CustomParser.parseDataSetStringToByteArray(dataSet);
        byte[] colorBlob = Base64.decode(colorSet, Base64.DEFAULT);

        int insertId = (int) mDbOpenHelper.insertBigLevel(puzzleId, number, width, height, dataBlob, colorBlob);

        mDbOpenHelper.close();

        Log.d("loadLevel", "insert ID: " + insertId);

        //지금 파싱하고 있는 레벨 태그의 마지막까지 커서를 옮겨준다.
        //다음 레벨 스타트태그 혹은 퍼즐 엔드태그를 나타낼 것
//        parser.next(); //colorset end tag
//        parser.next(); //level end tag
//        parser.next(); //next tag after this level tag

        parser.nextTag();
        parser.nextTag();

        Log.d("loadLevel", "parser ended at : " + parser.getLineNumber());

        return insertId;
    }

    private int getParsingPosition(XmlPullParser parser, String currentVersion) {
        int eventType;

        try {
            eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String startTag = parser.getName();
                    if (startTag.equals("version")) {
                        if (parser.nextText().equals(currentVersion)) {
                            //현재 버전과 일치한다면
                            //해당 위치 이후부터 파싱을 시작한다.
                            Log.d("getParsingPosition", "found current version, parsing start");
                            parser.next();
                            return parser.getLineNumber();
                        }
                        //일치하지 않으면 parser.next() 를 계속한다.
                    }
                }
                eventType = parser.nextTag();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public String loadData() {
        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        InputStream levelInputStream;

        //get version
        VersionPreference versionPreference = new VersionPreference(ctx);
        String currentVersion = versionPreference.getVersion();

        Log.d("DbManager", "currentVersion: " + currentVersion);
        //get version

        int a_id;
        int p_id;

        String latestVersion;

        try {
            levelInputStream = ctx.getResources().openRawResource(R.raw.data);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new InputStreamReader(levelInputStream, StandardCharsets.UTF_8));
            int eventType = parser.getEventType();


            //최신 버전 체크
            latestVersion = getLatestVersion(parser);
            if (latestVersion == null) {
                //최신 버전의 확인이 안되는 상태
                Log.d("DbManager", "latest version check error...");
                return null;
            } else if (latestVersion.equals(currentVersion)) {
                //현재 버전과 최신 버전이 일치한다면
                Log.d("DbManager", "latest version. no need to update DB");
                return currentVersion;
            } else {
                //일치하지 않으면 파싱을 시작한다.
                Log.d("DbManager", "current version: " + currentVersion + " latest version: " + latestVersion);
            }

            //현재 버전 이후부터 파싱을 해야 하기 때문에 현재 버전을 나타내는 xml 위치까지 파서 이동
            if (getParsingPosition(parser, currentVersion) == 0) {
                //현재 버전의 데이터를 찾을 수 없는 상태
                Log.d("DbManager", "current version data check error...");
                return null;
            }

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String startTag = parser.getName();

                    Log.d("DbManager", "startTag: " + startTag);
                    switch (startTag) {
                        //버전 체크를 계속해서 해준다
                        case "version":
                            //로드가 완료된 버전으로 버전 넘버를 업데이트 해준다.
                            currentVersion = parser.nextText();
                            versionPreference.setVersion(currentVersion);
                            break;

                        case "artist":
                            //아티스트를 갱신해 줌
                            a_id = loadArtist(parser);

                            //이제 퍼즐이 반복해서 나옴
                            //아티스트 엔드 태그가 나올 때까지 파싱함
                            while (parser.getEventType() != XmlPullParser.END_TAG || !parser.getName().equals("artist")) {
                                //퍼즐을 인서트한 후 해당 인서트 id 를 반환한다.
                                p_id = loadPuzzle(parser, a_id);
                                //퍼즐 엔드 태그가 나올 때까지 해당 퍼즐 내의 레벨을 파싱함
                                while (parser.getEventType() != XmlPullParser.END_TAG || !parser.getName().equals("puzzle")) {
                                    Log.d("DbManager", "getEventType(): " + parser.getEventType());
                                    Log.d("DbManager", "getName(): " + parser.getName());
                                    Log.d("DbManager", "getLineNumber(): " + parser.getLineNumber());
                                    loadLevel(parser, p_id);
                                }
                                Log.d("DbManager", "퍼즐 엔드 태그 발생, p_id: " + p_id);
                                parser.nextTag();
                            }

                            Log.d("DbManager", "아티스트 엔드 태그 발생, a_id: " + a_id);
                    }
                }
                eventType = parser.next();
            }


        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        mDbOpenHelper.close();


        Log.d("DBManager", "loadData end");

        return currentVersion;
    }
}


//    public static void loadCategoriesFromXmlToDB(Context ctx)
//    {
//        //xml파일에 저장된 데이터를 category db에 저장합니다.
//        //게임의 버전을 인식하여 추가해야 할 카테고리만 자동으로 추가합니다.
//
//        InputStream categoryInputStream;
//        ArrayList<String> categories = new ArrayList<>();
//
//        //get version
//        SharedPreferences versionSavePref = ctx.getSharedPreferences("VERSION", MODE_PRIVATE);
//
//        //마지막 플레이한 레벨의 이름과 카테고리를 받아온다.
//        final String categoryVersion = versionSavePref.getString("categoryVersion", "0");
//        String currentVersion = categoryVersion;
//
//        String version = "0";
//
//        //get version
//
//        try {
//            categoryInputStream = ctx.getResources().openRawResource(R.raw.categories);
//
//            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//            XmlPullParser parser = factory.newPullParser();
//            parser.setInput(new InputStreamReader(categoryInputStream, "UTF-8"));
//            int eventType = parser.getEventType();
//
//
//            while(eventType != XmlPullParser.END_DOCUMENT) {
//                if (eventType == XmlPullParser.START_TAG) {
//                    String startTag = parser.getName();
//                    if (startTag.equals("version")) {
//                        version = parser.nextText();
//                        if(version.equals(currentVersion))
//                        {
//                            //해당 태그 아래부터 카테고리 명을 insert하기 시작한다.
//                            eventType = parser.next();
//                            break;
//                        }
//
//                    }
//                }
//                eventType = parser.next();
//            }
//
//            while(eventType != XmlPullParser.END_DOCUMENT) {
//                if (eventType == XmlPullParser.START_TAG) {
//                    String startTag = parser.getName();
//                    if (startTag.equals("name")) {
//                        categories.add(parser.nextText());
//                    }
//                    else if(startTag.equals("version"))
//                    {
//                        version = parser.nextText();
//                    }
//                }
//                eventType = parser.next();
//            }
//
//
//        } catch (XmlPullParserException | IOException e) {
//            e.printStackTrace();
//        }
//
//        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
//        try {
//            mDbOpenHelper.open();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        for(int i = 0; i < categories.size(); i++)
//        {
//            mDbOpenHelper.insertCategory(categories.get(i));
//        }
//
//        mDbOpenHelper.close();
//
//        //카테고리 버전을 sharedPreferences 에 업데이트 해준다.
//        SharedPreferences.Editor editor = versionSavePref.edit();
//        editor.putString("categoryVersion", version);
//
//        editor.apply();
//    }

//    public static void loadLevelsFromXmlToDB(Context ctx)
//    {
//        DbOpenHelper mDbOpenHelper = new DbOpenHelper(ctx);
//        try {
//            mDbOpenHelper.open();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        InputStream levelInputStream;
//
//        //get version
//        SharedPreferences versionSavePref = ctx.getSharedPreferences("VERSION", MODE_PRIVATE);
//        final String levelVersion = versionSavePref.getString("levelVersion", "0");
//        //get version
//
//        String levelName = "";
//        String categoryName = "";
//        int levelWidth = 0;
//        int levelHeight = 0;
//        String levelData = "";
//        String levelColor = "";
//
//        String version = "0";
//
//        try {
//            levelInputStream = ctx.getResources().openRawResource(R.raw.levels);
//
//            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//            XmlPullParser parser = factory.newPullParser();
//            parser.setInput(new InputStreamReader(levelInputStream, "UTF-8"));
//            int eventType = parser.getEventType();
//
//            while(eventType != XmlPullParser.END_DOCUMENT) {
//                if (eventType == XmlPullParser.START_TAG) {
//                    String startTag = parser.getName();
//                    if (startTag.equals("version")) {
//                        version = parser.nextText();
//                        if(version.equals(levelVersion))
//                        {
//                            //해당 태그 아래부터 카테고리 명을 insert하기 시작한다.
//                            eventType = parser.next();
//                            break;
//                        }
//                    }
//                }
//                eventType = parser.next();
//            }
//
//            while(eventType != XmlPullParser.END_DOCUMENT) {
//                switch (eventType) {
//                    case XmlPullParser.START_TAG:
//                        String startTag = parser.getName();
//
//                        switch (startTag) {
//
//                            case "lname":
//                                levelName = parser.nextText();
//                                break;
//                            case "cname":
//                                categoryName = parser.nextText();
//                                break;
//                            case "width":
//                                levelWidth = Integer.parseInt(parser.nextText());
//                                break;
//                            case "height":
//                                levelHeight = Integer.parseInt(parser.nextText());
//                                break;
//                            case "data":
//                                levelData = parser.nextText();
//                                break;
//                            case "version":
//                                version = parser.nextText();
//                                break;
//                            case "color":
//                                levelColor = parser.nextText();
//                                break;
//                        }
//                        break;
//                    case XmlPullParser.END_TAG:
//                        String endTag = parser.getName();
//                        if(endTag.equals("level"))
//                        {
//                            //해당 레벨의 데이터를 db에 추가한다.
//
//                            byte[] dataSetBytes = CustomParser.parseDataSetStringToByteArray(levelData);
//                            byte[] colorSetBytes = CustomParser.parseColorSetStringToByteArray(levelColor, levelWidth, levelHeight);
//                            mDbOpenHelper.insertLevel(levelName, categoryName, levelWidth, levelHeight, dataSetBytes, colorSetBytes, 0);
//                        }
//                        break;
//                }
//                eventType = parser.next();
//            }
//
//
//        } catch(XmlPullParserException | IOException e) {
//            e.printStackTrace();
//        }
//
//        mDbOpenHelper.close();
//
//        //버전을 갱신해 준다.
//        SharedPreferences.Editor editor = versionSavePref.edit();
//        editor.putString("levelVersion", version);
//
//        editor.apply();
//    }