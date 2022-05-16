package sk.spse.matiskova.finalproject;

import static sk.spse.matiskova.finalproject.MainActivity.numberOfQuestion;
import static sk.spse.matiskova.finalproject.QuestionMain.correctAnswer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FinishActivity extends AppCompatActivity {

    Button homeButton;
    TextView correctA;
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        getSupportActionBar().setTitle("Zub v hrsti");
        
        homeButton = findViewById(R.id.homeButton);
        correctA = findViewById(R.id.correctAnswers);

        double average = (double) Math.round(( (double) correctAnswer / numberOfQuestion * 100) * 10) / 10;

        correctA.setText("Spravne odpovede " + correctAnswer + " / " + numberOfQuestion + "\n V percentach: " + average + " %");
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FinishActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed()
    { }
}