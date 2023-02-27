package sk.spse.matiskova.finalproject;

import static sk.spse.matiskova.finalproject.Login.currentlyLoggedIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private final ArrayList<Button> buttons = new ArrayList<>();
    private String[] tests = new String[] {"test1", "test2", "test3", "test4", "test5"};
    private static final String FILE_NAME = "myFile";
    private ArrayList<Integer> questionId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");
        userID = user.getUid();

        for (final int test : Arrays.asList(R.id.test1, R.id.test2, R.id.test3, R.id.test4, R.id.test5)) {
            buttons.add(findViewById(test));
        }

        final TextView fullNameTextView = findViewById(R.id.fullName);
        final TextView emailAddressTextView = findViewById(R.id.emailAddress);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    String fullName = userProfile.fullName;
                    String email = userProfile.email;

                    fullNameTextView.setText(fullName);
                    emailAddressTextView.setText(email);

                    for (int i = 0; i < buttons.size(); i++) {
                        String test = (String) snapshot.child(tests[i]).getValue();

                        if (test != null) {
                            test = test.replace(" ", "");
                            String[] text = test.split("/");
                            buttons.get(i).setText(text[0] + "          " + text[1] + "%");
                            buttons.get(i).setClickable(true);
                            buttons.get(i).setAlpha(1f);
                        }
                        else {
                            //buttons.get(i).setVisibility(View.INVISIBLE);
                            buttons.get(i).setAlpha(0.35f);
                            buttons.get(i).setClickable(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < buttons.size(); i++) {
                    int finalI = i;
                    buttons.get(i).setOnClickListener(view -> {
                        if (buttons.get(finalI).getText().charAt(0) == 'T') {
                            Toast.makeText(ProfileActivity.this, "Tu nemáš uložený test :D", Toast.LENGTH_LONG).show();
                        }
                        else {
                            String test = (String) snapshot.child(tests[finalI]).getValue();
                            assert test != null;
                            test = test.replace(" ", "");
                            String[] text = test.split("/");
                            text[2] = text[2].substring(1, text[2].length() - 1);
                            text[2] = text[2].replace(",", " ");
                            String[] ids = text[2].split(" ");

                            for (String id : ids) {
                                questionId.add(Integer.valueOf(id));
                            }

                            Intent intent = new Intent(ProfileActivity.this, QuestionMain.class);
                            intent.putExtra("questionId", questionId);
                            intent.putExtra("userTopic", text[0].trim());
                            startActivity(intent);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something wrong happened!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void StoredDataUsingSHaredPref(boolean ischecked) {
        SharedPreferences.Editor editor = getSharedPreferences(FILE_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("ischecked", ischecked);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.signOut) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            StoredDataUsingSHaredPref(false);
            currentlyLoggedIn = false;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}