package com.reanstudio.imagefilter.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.reanstudio.imagefilter.R;

/**
 * Created by yahyamukhlis on 4/28/16.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private Button btnJayway;

    private Button btnReinier;

    private Button btnLessonOne;

    private Button btnLessonTwo;

    private Button btnLessonThree;

    private Button btnLessonFour;

    private Button btnLessonFive;

    private Button btnLessonSix;

    private Button btnLessonSeven;

    private Button btnLessonEight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnJayway = (Button) findViewById(R.id.btn_jayway);
        btnJayway.setOnClickListener(this);

        btnReinier = (Button) findViewById(R.id.btn_reinier);
        btnReinier.setOnClickListener(this);

        btnLessonOne = (Button) findViewById(R.id.btn_lesson_one);
        btnLessonOne.setOnClickListener(this);

        btnLessonTwo = (Button) findViewById(R.id.btn_lesson_two);
        btnLessonTwo.setOnClickListener(this);

        btnLessonThree = (Button) findViewById(R.id.btn_lesson_three);
        btnLessonThree.setOnClickListener(this);

        btnLessonFour = (Button) findViewById(R.id.btn_lesson_four);
        btnLessonFour.setOnClickListener(this);

        btnLessonFive = (Button) findViewById(R.id.btn_lesson_five);
        btnLessonFive.setOnClickListener(this);

        btnLessonSix = (Button) findViewById(R.id.btn_lesson_six);
        btnLessonSix.setOnClickListener(this);

        btnLessonSeven = (Button) findViewById(R.id.btn_lesson_seven);
        btnLessonSeven.setOnClickListener(this);

        btnLessonEight = (Button) findViewById(R.id.btn_lesson_eight);
        btnLessonEight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_jayway:
                showOpenGL(new Intent(this, JaywayMainActivity.class));
                break;
            case R.id.btn_reinier:
                showOpenGL(new Intent(this, ReinierMainActivity.class));
                break;
            case R.id.btn_lesson_one:
                showOpenGL(new Intent(this, LessonOneActivity.class));
                break;
            case R.id.btn_lesson_two:
                showOpenGL(new Intent(this, LessonTwoActivity.class));
                break;
            case R.id.btn_lesson_three:
                showOpenGL(new Intent(this, LessonThreeActivity.class));
                break;
            case R.id.btn_lesson_four:
                showOpenGL(new Intent(this, LessonFourActivity.class));
                break;
            case R.id.btn_lesson_five:
                showOpenGL(new Intent(this, LessonFiveActivity.class));
                break;
            case R.id.btn_lesson_six:
                showOpenGL(new Intent(this, LessonSixActivity.class));
                break;
            case R.id.btn_lesson_seven:
                showOpenGL(new Intent(this, LessonSevenActivity.class));
                break;
            case R.id.btn_lesson_eight:
                showOpenGL(new Intent(this, LessonEightActivity.class));
                break;
        }
    }

    private void showOpenGL(Intent intent) {
        startActivity(intent);
    }
}
