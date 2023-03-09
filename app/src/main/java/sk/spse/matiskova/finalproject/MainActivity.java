package sk.spse.matiskova.finalproject;

import static sk.spse.matiskova.finalproject.Login.currentlyLoggedIn;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static String userTopic;
    public static int numberOfQuestion;
    public static QuestionLoader loader;
    private FirebaseUser fus;
    public static boolean alwaysLoggedIn;
    private static final String FILE_NAME = "myFile";

    private Dialog dialog;
    private EditText count;
    public static Mode mode;
    StorageReference storageReference;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth fau = FirebaseAuth.getInstance();
        fus = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        alwaysLoggedIn = ReadDataUsingSHaredPref();

        Objects.requireNonNull(getSupportActionBar()).setTitle("Zub v hrsti");

        Button chemistryButton = findViewById(R.id.chemistryButton);
        Button biologyButton = findViewById(R.id.biologyButton);
        ImageView login = findViewById(R.id.login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        }

        storageReference = FirebaseStorage.getInstance().getReference();
        boolean isNetworkAvailable = isNetworkAvailable(Objects.requireNonNull(peekAvailableContext()));

        if (isNetworkAvailable) {

            if (fus != null && alwaysLoggedIn || fus != null && currentlyLoggedIn) {
                StorageReference profileRef = storageReference.child("users/"+fus.getUid()+"/profile.jpg");
                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(login);
                    }
                });
            }
            else {
                login.setImageResource(R.drawable.profile);
            }
        }
        else {
            login.setImageResource(R.drawable.crossed_person);
            login.setEnabled(false);
        }

        FilesystemManager filesystemManager = new FilesystemManager();
        if (!filesystemManager.IsDbInExternalStorage()) {
            AssetManager am = getApplicationContext().getAssets();
            try {
                filesystemManager.CopyDbToExternalStorage(am);
            }
            catch (IOException e) {
                Log.w("", "Cannot copy database to external storage, reason: " + e);
            }
        }

        String dbPath = filesystemManager.GetExternalDatabasePath();
        loader = new QuestionLoader(dbPath);
        if (!loader.OpenReadOnlyDatabase()) {
            Log.w("", "Cannot open database in path: " + dbPath);
        }

        login.setOnClickListener(view -> {
            if (fus != null && alwaysLoggedIn) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
            else if (fus != null && currentlyLoggedIn) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });


        chemistryButton.setOnClickListener(view -> {
            userTopic = "chemistry";
            alertDialog();
        });

        biologyButton.setOnClickListener(view -> {
            userTopic = "biology";
            alertDialog();
        });
    }

    @Override
    public boolean isDestroyed() {
        return super.isDestroyed();
    }

    @Override
    protected void onDestroy() {
        loader.CloseDatabase();
        super.onDestroy();
    }

    public void alertDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_count_of_questions);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        Button ok = dialog.findViewById(R.id.yes_btn);
        Button zrusit = dialog.findViewById(R.id.no_btn);
        RadioButton testing_radioButton = dialog.findViewById(R.id.testing_radioButton);
        RadioButton learning_radioButton = dialog.findViewById(R.id.learning_radioButton);
        count = dialog.findViewById(R.id.count);

        dialog.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!count.getText().toString().equals("") && (testing_radioButton.isSelected() || learning_radioButton.isSelected())) {
                    numberOfQuestion = Integer.parseInt(count.getText().toString());
                    if (numberOfQuestion > 4 && numberOfQuestion < 21) {
                        Intent intent = new Intent(MainActivity.this, QuestionMain.class);
                        startActivity(intent);
                        if (testing_radioButton.isSelected()) {
                            mode = Mode.Testing;
                        }
                        else {
                            mode = Mode.Learning;
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Nesprávny počet otázok, skus to ešte raz :)", Toast.LENGTH_LONG).show();
                        count.setText("");
                    }
                }
                else {
                    dialog.dismiss();
                }
            }
        });

        zrusit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        testing_radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!learning_radioButton.isSelected()) {
                    testing_radioButton.setChecked(true);
                    testing_radioButton.setSelected(true);
                } else {
                    testing_radioButton.setChecked(true);
                    testing_radioButton.setSelected(true);
                    learning_radioButton.setChecked(false);
                    learning_radioButton.setSelected(false);
                }
            }
        });

        learning_radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!testing_radioButton.isSelected()) {
                    learning_radioButton.setChecked(true);
                    learning_radioButton.setSelected(true);
                } else {
                    learning_radioButton.setChecked(true);
                    learning_radioButton.setSelected(true);
                    testing_radioButton.setChecked(false);
                    testing_radioButton.setSelected(false);
                }
            }
        });
    }

    private void StoredDataUsingSHaredPref(boolean ischecked) {
        SharedPreferences.Editor editor = getSharedPreferences(FILE_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("ischecked", ischecked);
        editor.apply();
    }

    private boolean ReadDataUsingSHaredPref() {
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean("ischecked", false);
    }

    @Override
    public void onBackPressed()
    { }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public enum Mode {
        Learning,
        Testing
    }
}