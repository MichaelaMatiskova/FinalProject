package sk.spse.matiskova.finalproject;

import static sk.spse.matiskova.finalproject.Login.currentlyLoggedIn;
import static sk.spse.matiskova.finalproject.MainActivity.alwaysLoggedIn;
import static sk.spse.matiskova.finalproject.MainActivity.numberOfQuestion;
import static sk.spse.matiskova.finalproject.MainActivity.userTopic;
import static sk.spse.matiskova.finalproject.QuestionMain.correctAnswer;
import static sk.spse.matiskova.finalproject.QuestionMain.questionsId;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class FinishActivity extends AppCompatActivity {

    private Button homeButton;
    private TextView correctA;
    private Button saveTest;

    private FirebaseUser user;

    private Dialog dialog;
    private TextView dialog_question;
    private Button yes_btn;
    private Button no_btn;

    private DatabaseReference dr;
    private String userId;

    private static final String FILE_NAME = "myFile";

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        getSupportActionBar().setTitle("Zub v hrsti");

        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        homeButton = findViewById(R.id.homeButton);
        correctA = findViewById(R.id.correctAnswers);
        saveTest = findViewById(R.id.saveTest);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();


        double average = (double) Math.round(( (double) correctAnswer / numberOfQuestion * 100) * 10) / 10;

        correctA.setText("Spravne odpovede " + correctAnswer + " / " + numberOfQuestion + "\n V percentach: " + average + " %");
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FinishActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        boolean isNetworkAvailable = isNetworkAvailable(Objects.requireNonNull(peekAvailableContext()));

        if (isNetworkAvailable) {

            if (currentlyLoggedIn || alwaysLoggedIn) {
            saveTest.setOnClickListener(view -> {
                //if (wasLoggingIn) {
                dr = FirebaseDatabase.getInstance().getReference("users");
                dr.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long count = snapshot.getChildrenCount();
                        if (count < 8) {
                            dr.child(userId).child("test" + (count - 2)).setValue(userTopic + "/" + average + "/" + questionsId.toString());
                            Intent intent = new Intent(FinishActivity.this, ProfileActivity.class);
                            startActivity(intent);
                        }

                        if (count == 8) {
                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void run() {
                                    alertDialog();
                                    dialog_question.setText("Naozaj chceš uložiť tento test? Ak áno zmaže sa ti jeden z predošlých uloženych testov");
                                    dialog_question.setTextSize(18);
                                    yes_btn.setOnClickListener(view -> {
                                        dialog.dismiss();
                                        String x = (String) snapshot.child("rewriteTest").getValue();
                                        assert x != null;
                                        dr.child(userId).child(x).
                                                setValue(userTopic + "/" + average + "/" + questionsId.toString());
                                        rewriteTest();
                                        Intent intent = new Intent(FinishActivity.this, ProfileActivity.class);
                                        startActivity(intent);
                                    });

                                    no_btn.setOnClickListener(view -> {
                                        Intent intent = new Intent(FinishActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    });
                                }
                            }, 500);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            });
            }
            else {
                //Intent intent = new Intent(FinishActivity.this, Login.class);
                //startActivity(intent);
                saveTest.setVisibility(View.INVISIBLE);
            }
        }
        else {
            saveTest.setVisibility(View.INVISIBLE);
        }
    }

    public void rewriteTest() {
        dr.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //String x =  String.valueOf(dr.child(userId).child("rewriteTest"));
                String x = (String) snapshot.child("rewriteTest").getValue();

                x = x.substring(x.length()-1);
                if (Integer.parseInt(x) < 5) {
                    int p = Integer.parseInt(x);
                    p++;
                    dr.child(userId).child("rewriteTest").setValue("test" + p);
                }
                else {
                    dr.child(userId).child("rewriteTest").setValue("test1");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onBackPressed()
    { }
}