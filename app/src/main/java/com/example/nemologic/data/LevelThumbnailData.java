package com.example.nemologic.data;

public class LevelThumbnailData {

    private int id;
    private String name;
    private String category;
    private int width;
    private int height;
    private int progress;
    private String dataSet;
    private String saveData = "";
    private String colorSet;


    public LevelThumbnailData(int id, String category, String name, int width, int height, int progress, String dataSet, String saveData, String colorSet)
    {
        this.id = id;
        this.category = category;
        this.name = name;
        this.width = width;
        this.height = height;
        this.dataSet = dataSet;
        this.progress = progress;
        this.colorSet = colorSet;
        if(progress == 1)
        {
            //예전에 플레이하던 레벨의 경우에만 세이브데이터를 파싱해서 저장한다.
            this.saveData = saveData;
        }

    }

    public int getId() {return this.id;}
    public String getCategory() {return this.category;}

    public String getName()
    {
        return this.name;
    }

    public int getProgress() {return this.progress;}
    public int getWidth() {return this.width;}
    public int getHeight() {return this.height;}
    public String getSaveData() {return this.saveData;}
    public String getColorSet() {return this.colorSet;}
    public String getDataSet() {return this.dataSet;}
}
