package sk.spse.matiskova.finalproject;

import static sk.spse.matiskova.finalproject.MainActivity.loader;
import static sk.spse.matiskova.finalproject.MainActivity.mode;
import static sk.spse.matiskova.finalproject.MainActivity.numberOfQuestion;
import static sk.spse.matiskova.finalproject.MainActivity.userTopic;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class QuestionMain extends AppCompatActivity {
    private TextView question;
    private Button nextButtonTesting;
    private Button nextButtonLearning;
    private Button submitButton;

    private int currentQuestionPosition = 0;

    private boolean checkSubmit;
    private ScrollView scrollView;

    private final ArrayList<Button> buttons = new ArrayList<>();
    private boolean[] checks = new boolean[8];

    public static int correctAnswer = 0;
    public static ArrayList<Integer> questionsId = new ArrayList<>();

    private Question actualQuestion;
    Random rd = new Random();

    private int textSize = 20;
    private Dialog dialog;
    private TextView dialog_question;
    private Button yes_btn;
    private Button no_btn;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_main);

        //Objects.requireNonNull(getSupportActionBar()).setTitle(userTopic);

        Intent intent = getIntent();
        ArrayList<Integer> temp = intent.getIntegerArrayListExtra("questionId");

        if (temp == null) {
            generateId();
            if (userTopic.equals("biology")) {
                Objects.requireNonNull(getSupportActionBar()).setTitle("Biológia");
            }
            else {
                Objects.requireNonNull(getSupportActionBar()).setTitle("Chémia");
            }
        }
        else {
            questionsId.clear();
            questionsId = temp;
            numberOfQuestion = temp.size();
            userTopic = intent.getStringExtra("userTopic");
            //userTopic = intent.getStringExtra("userTopic");
            mode = MainActivity.Mode.Testing;
        }

        question = findViewById(R.id.question);

        for (final int option : Arrays.asList(R.id.optionA, R.id.optionB, R.id.optionC, R.id.optionD,
                R.id.optionE, R.id.optionF, R.id.optionG, R.id.optionH)) {
            buttons.add(findViewById(option));
        }

        submitButton = findViewById(R.id.submitButton);
        nextButtonTesting = findViewById(R.id.nextButton);
        nextButtonLearning = findViewById(R.id.nextButton2);
        scrollView = findViewById(R.id.scrolling);

        newQuestion(questionsId.get(0));

        if (mode == MainActivity.Mode.Testing) {
            submitButton.setVisibility(View.VISIBLE);
            nextButtonTesting.setVisibility(View.VISIBLE);
            nextButtonLearning.setVisibility(View.INVISIBLE);
            startTest();
        }

        else {
            submitButton.setVisibility(View.INVISIBLE);
            nextButtonTesting.setVisibility(View.INVISIBLE);
            nextButtonLearning.setVisibility(View.VISIBLE);
            clickableOff();
            revealAnswerLearning();

            nextButtonLearning.setOnClickListener(view -> {
                changeNextquestion();
                revealAnswerLearning();

                if (currentQuestionPosition + 1 == numberOfQuestion) {
                    nextButtonLearning.setText("Finish");
                }

                if (nextButtonLearning.getText().toString().equals("Finish")) {
                    nextButtonLearning.setClickable(false);
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            alertDialog();
                            dialog_question.setText("Chceš si svoje vedomosti overiť v testovacom mode? ");

                            yes_btn.setOnClickListener(view -> {
                                dialog.dismiss();
                                currentQuestionPosition = 0;
                                nextButtonTesting.setText("Next");
                                submitButton.setVisibility(View.VISIBLE);
                                nextButtonTesting.setVisibility(View.VISIBLE);
                                //nextButtonTesting.setText("Next");
                                nextButtonLearning.setVisibility(View.INVISIBLE);
                                startTest();
                            });

                            no_btn.setOnClickListener(view -> {
                                Intent intent = new Intent(QuestionMain.this, MainActivity.class);
                                startActivity(intent);
                            });
                        }
                    }, 10000);
                }
            });
        }
    }

    public void startTest() {
        clickableOn();

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setBackgroundColor(Color.parseColor("#A6C6DF"));
        }

        for (int i = 0; i < buttons.size(); i++) {
            int finalI = i;
            buttons.get(i).setOnClickListener(view -> {
                if (!checks[finalI]) {
                    buttons.get(finalI).setBackgroundColor(Color.WHITE);
                    buttons.get(finalI).setTextColor(Color.BLUE);
                    checks[finalI] = true;
                } else {
                    buttons.get(finalI).setBackgroundColor(Color.parseColor("#A6C6DF"));
                    buttons.get(finalI).setTextColor(Color.WHITE);
                    checks[finalI] = false;
                }
            });
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            boolean checkEnterButton = false;

            @Override
            public void onClick(View view) {
                for (boolean check : checks) {
                    if (check) {
                        checkEnterButton = true;
                        break;
                    }
                }
                if (!checkEnterButton) {
                    Toast.makeText(getApplicationContext(), "Enter the answer", Toast.LENGTH_SHORT).show();
                } else {
                    revealAnswerTesting();
                    clickableOff();
                    checkSubmit = true;
                    checkEnterButton = false;
                }
            }
        });

        nextButtonTesting.setOnClickListener(view -> {
            if (!checkSubmit) {
                Toast.makeText(getApplicationContext(), "Please submit this question", Toast.LENGTH_SHORT).show();
            } else {
                changeNextquestion();
                clickableOn();
            }

            if (nextButtonTesting.getText().toString().equals("Finish") && checkSubmit) {
                Intent intent = new Intent(QuestionMain.this, FinishActivity.class);
                startActivity(intent);
            }
        });
    }

    public void alertDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_yes_no_answer);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        yes_btn = dialog.findViewById(R.id.yes_btn);
        no_btn = dialog.findViewById(R.id.no_btn);
        dialog_question = dialog.findViewById(R.id.dialog_question);

        dialog.show();
    }

    private void revealAnswerTesting() {
        boolean checkRed = false;

        for (int i = 0; i < buttons.size(); i++) {
            if (checks[i] && !actualQuestion.IsAnswerCorrect(i)) {
                //buttons.get(i).setBackgroundColor(Color.RED);
                buttons.get(i).setTextColor(Color.RED);
                checkRed = true;
            } else if (actualQuestion.IsAnswerCorrect(i)) {
                if (!checks[i]) {
                    checkRed = true;
                }
                //buttons.get(i).setBackgroundColor(Color.GREEN); FF0F6813
                buttons.get(i).setTextColor(Color.parseColor("#FF0F6813"));
            }
        }

        if (!checkRed) {
            correctAnswer++;
        }
    }

    private void revealAnswerLearning() {

        for (int i = 0; i < buttons.size(); i++) {
           if (actualQuestion.IsAnswerCorrect(i)) {
               buttons.get(i).setBackgroundColor(Color.parseColor("#FF0F6813"));
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void changeNextquestion() {
        currentQuestionPosition++;

        if (currentQuestionPosition + 1 == numberOfQuestion) {
            nextButtonTesting.setText("Finish");
        }

        if (currentQuestionPosition < numberOfQuestion) {

            newQuestion(questionsId.get(currentQuestionPosition));

            for (int i = 0; i < buttons.size(); i++) {
                buttons.get(i).setBackgroundColor(Color.parseColor("#A6C6DF"));
                buttons.get(i).setTextColor(Color.WHITE);
            }
        }
    }

    private void newQuestion(int index) {
        set20SizeText();

        actualQuestion = loader.SelectQuestionFromTable(userTopic, index);
        setTextSize();

        if (actualQuestion.GetQuestion().length() > 100) {
            question.setTextSize(20);
        }
        question.setText(Html.fromHtml(actualQuestion.GetQuestion(), Html.FROM_HTML_MODE_COMPACT));
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setTextSize(textSize);
            buttons.get(i).setText(Html.fromHtml(actualQuestion.GetAnswer(i), Html.FROM_HTML_MODE_COMPACT));
        }
        falseChecks();
        scrollView.scrollTo(0, 0);
    }

    private void falseChecks() {
        checkSubmit = false;

        checks = new boolean[]{false, false, false, false, false, false, false, false};
    }

    private void clickableOff() {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setClickable(false);
        }
    }

    private void clickableOn() {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setClickable(true);
        }
        nextButtonTesting.setClickable(true);
    }

    private void generateId() {
        questionsId.clear();
        Set<Integer> temp = new TreeSet<>();
        int questionCount = loader.getRowCount(userTopic);
        while (temp.size() != numberOfQuestion) {
            temp.add(rd.nextInt(questionCount) + 1);
        }
        questionsId.addAll(temp);
    }

    private void set20SizeText() {
        question.setTextSize(25);
        textSize = 20;
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setTextSize(20);
        }
    }

    private void setTextSize() {
        for (int i = 0; i < buttons.size(); i++) {

            if (actualQuestion.GetAnswer(i).length() >= 35) {
                if (actualQuestion.GetAnswer(i).length() >= 60) {
                    textSize = 14;
                } else {
                    textSize = 16;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
}