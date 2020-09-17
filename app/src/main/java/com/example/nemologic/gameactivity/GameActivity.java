package com.example.nemologic.gameactivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nemologic.data.LevelPlayManager;
import com.example.nemologic.R;

import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    LevelPlayManager lpm;

    GameBoard gameBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        String name = Objects.requireNonNull(getIntent().getExtras()).getString("name");
        int width = Objects.requireNonNull(getIntent().getExtras()).getInt("width");
        int height = Objects.requireNonNull(getIntent().getExtras()).getInt("height");
        String dataSet = Objects.requireNonNull(getIntent().getExtras()).getString("dataset");
        String saveData = Objects.requireNonNull(getIntent().getExtras()).getString("savedata");

        lpm = new LevelPlayManager(name, width, height, dataSet, saveData);

        gameBoard = new GameBoard(this, lpm);

        gameBoard.makeGameBoard();
    }
}