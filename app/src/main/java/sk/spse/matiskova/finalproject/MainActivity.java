package sk.spse.matiskova.finalproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static String userTopic;
    public static int numberOfQuestion;
    public static QuestionLoader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Zub v hrsti");

        Button chemistryButton = findViewById(R.id.chemistryButton);
        Button biologyButton = findViewById(R.id.biologyButton);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
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

    @Override
    public void onBackPressed()
    { }
}