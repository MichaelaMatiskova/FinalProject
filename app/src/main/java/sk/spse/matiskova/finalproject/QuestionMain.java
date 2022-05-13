package sk.spse.matiskova.finalproject;

import static sk.spse.matiskova.finalproject.MainActivity.numberOfQuestion;
import static sk.spse.matiskova.finalproject.MainActivity.userTopic;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QuestionMain extends AppCompatActivity {
    private TextView question;
    private Button submitButton, nextButton;

    private List<QuestionList> questionLists;
    private int currentQuestionPosition = 0;

    private boolean checkSubmit;
    private ScrollView scrollView;

    private ArrayList<Button> buttons = new ArrayList<>();
    private boolean[] checks = new boolean[8];

    public static int correctAnswer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_main);

        question = findViewById(R.id.question);

        buttons.add(findViewById(R.id.optionA));
        buttons.add(findViewById(R.id.optionB));
        buttons.add(findViewById(R.id.optionC));
        buttons.add(findViewById(R.id.optionD));
        buttons.add(findViewById(R.id.optionE));
        buttons.add(findViewById(R.id.optionF));
        buttons.add(findViewById(R.id.optionG));
        buttons.add(findViewById(R.id.optionH));

        submitButton = findViewById(R.id.submitButton);
        nextButton = findViewById(R.id.nextButton);

        questionLists = QuestionsBank.getQuestions(userTopic);

        scrollView = findViewById(R.id.scrolling);

        newQuestion(0);

        for (int i = 0; i < buttons.size(); i++) {
            int finalI = i;
            buttons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!checks[finalI]) {
                        buttons.get(finalI).setBackgroundColor(Color.WHITE);
                        buttons.get(finalI).setTextColor(Color.BLUE);
                        checks[finalI] = true;
                    } else {
                        buttons.get(finalI).setBackgroundColor(Color.parseColor("#1D74BA"));
                        buttons.get(finalI).setTextColor(Color.WHITE);
                        checks[finalI]  = false;
                    }
                }
            });
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checks.getClass().equals(false)) {
                    Toast.makeText(getApplicationContext(), "Enter the answer", Toast.LENGTH_SHORT).show();
                }
                else {
                    revealAnswer();
                    clicableOFF();
                    checkSubmit = true;
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkSubmit) {
                    Toast.makeText(getApplicationContext(), "Please submit this question", Toast.LENGTH_SHORT).show();
                }
                else {
                    changeNextquestion();
                    clicableON();
                }

                if (nextButton.getText().toString().equals("Finish") && checkSubmit) {
                    Intent intent = new Intent(QuestionMain.this, FinishActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void revealAnswer() {
        final Set<Integer> getAnswers = questionLists.get(currentQuestionPosition).getAnswer();
        boolean checkRed = false;

        for (int i = 0; i < buttons.size(); i++) {
            if (checks[i] && !getAnswers.contains(i)) {
                buttons.get(i).setBackgroundColor(Color.RED);
                buttons.get(i).setTextColor(Color.WHITE);
                checkRed = true;
            }

            else if (getAnswers.contains(i)) {
                buttons.get(i).setBackgroundColor(Color.GREEN);
                buttons.get(i).setTextColor(Color.WHITE);
            }
        }

        if (!checkRed) {
            correctAnswer++;
        }
    }

    @SuppressLint("SetTextI18n")
    private void changeNextquestion() {
        currentQuestionPosition++;

        if (currentQuestionPosition + 1 == numberOfQuestion) {
            nextButton.setText("Finish");
        }

        if (currentQuestionPosition < numberOfQuestion) {

            newQuestion(currentQuestionPosition);

            for (int i = 0; i < buttons.size(); i++) {
                buttons.get(i).setBackgroundColor(Color.parseColor("#1D74BA"));
                buttons.get(i).setTextColor(Color.WHITE);
            }
        }
    }

    private void newQuestion(int index) {
        question.setText(Html.fromHtml(String.valueOf((questionLists.get(index).getQuestion()))));
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setText(Html.fromHtml(String.valueOf((questionLists.get(index).getOption(i)))));
        }

        falseChecks();
        scrollView.scrollTo(0,0);
    }

    private void falseChecks() {
        checkSubmit = false;

        checks = new boolean[]{false, false, false, false, false, false, false, false};
    }

    private void clicableOFF() {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setClickable(false);
        }
    }

    private void clicableON() {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setClickable(true);
        }
    }
}