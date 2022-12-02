package sk.spse.matiskova.finalproject;

import static sk.spse.matiskova.finalproject.Login.wasLoggingIn;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static String userTopic;
    public static int numberOfQuestion;
    public static QuestionLoader loader;
    private FirebaseUser fus;
    private boolean ischecked;
    private static final String FILE_NAME = "myFile";

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth fau = FirebaseAuth.getInstance();
        fus = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        ischecked = sharedPreferences.getBoolean("ischecked", false);

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

        boolean isNetworkAvailable = isNetworkAvailable(Objects.requireNonNull(peekAvailableContext()));

        if (isNetworkAvailable) {
            login.setImageResource(R.drawable.profile);
        }
        else {
            login.setImageResource(R.drawable.crossed_person);
            login.setEnabled(false);
        }

        FilesystemManager filesystemManager = new FilesystemManager();
        if (!filesystemManager.IsDbInExternalStorage())
        {
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
            if (fus != null && ischecked) {
                //if (ischecked) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } //else if (wasLoggingIn) {
                else if (fus != null && wasLoggingIn) {
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

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        if (userTopic.equals("chemistry")) {
            dialog.setTitle("Koľko otázok z chemie chceš mať v kvíze? ");
        }
        else {
            dialog.setTitle("Koľko otázok z biologie chceš mať v kvíze? ");
        }
        final EditText count = new EditText(MainActivity.this);
        count.setHint("<5,20>");
        count.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialog.setView(count);

        dialog.setPositiveButton("OK", (dialogInterface, i) -> {
            numberOfQuestion = Integer.parseInt(count.getText().toString());
            if (numberOfQuestion > 4 && numberOfQuestion < 21) {
                Intent intent = new Intent(MainActivity.this, QuestionMain.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(), "Wrong number of questions", Toast.LENGTH_LONG).show();
                count.setText("");
            }
        });

        dialog.setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.cancel());
        dialog.show();
    }

    private void StoredDataUsingSHaredPref(boolean ischecked) {
        SharedPreferences.Editor editor = getSharedPreferences(FILE_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("ischecked", ischecked);
        editor.apply();
    }

    @Override
    public void onBackPressed()
    { }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}