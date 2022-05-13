package sk.spse.matiskova.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainActivity extends AppCompatActivity {

    public static String userTopic;
    public static int numberOfQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button chemistryButton = findViewById(R.id.chemistryButton);
        Button biologyButton = findViewById(R.id.biologyButton);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        }

        String databaseName = "question_bank.sqlite";
        Path directoryPath = Paths.get(
                Environment.getExternalStorageDirectory().getPath()
                , "Millionaire"//getPackageName()
                , databaseName).toAbsolutePath();
        String directoryPathStr = directoryPath.toString();
        File externalStorageDb = new File(directoryPathStr);
        if (!externalStorageDb.exists())
        {
            AssetManager am = getApplicationContext().getAssets();
            InputStream assetDatabaseStream = null;
            try {
                assetDatabaseStream = am.open(databaseName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Files.createDirectories(directoryPath.getParent());
                FileOutputStream dst = new FileOutputStream(externalStorageDb);

                byte[] buffer = new byte[1024];
                int read;
                if (assetDatabaseStream != null) {
                    do {
                        read = assetDatabaseStream.read(buffer);
                        if (read > 0) {
                            dst.write(buffer, 0, read);
                        }
                    } while(read != -1);
                    assetDatabaseStream.close();
                }
                dst.flush();
                dst.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        QuestionLoader loader = new QuestionLoader(directoryPathStr);
        boolean result = loader.OpenReadOnlyDatabase();
        loader.DoSomeSketchySelect();
        loader.CloseDatabase();
        Log.i("Super duper log", "Result is " + (result ? "SUCCESS" : "FAILURE"));

        chemistryButton.setOnClickListener(view -> {
            userTopic = "chemistry";

            alertDialog();
        });

        biologyButton.setOnClickListener(view -> {
            userTopic = "biology";
            alertDialog();
        });
    }

    public void alertDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Koľko otázok z " + userTopic + " chceš mať v kvíze? ");
        final EditText count = new EditText(MainActivity.this);
        count.setHint("<5,20>");
        count.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialog.setView(count);


        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                numberOfQuestion = Integer.parseInt(count.getText().toString());
                if (numberOfQuestion > 4 && numberOfQuestion < 21) {
                    Intent intent = new Intent(MainActivity.this, QuestionMain.class);
                    startActivity(intent);
                }
                else {
                    count.setText("");
                }
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        dialog.show();

    }
}