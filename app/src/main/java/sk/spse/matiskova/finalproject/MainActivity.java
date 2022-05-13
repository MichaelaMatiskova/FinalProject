package sk.spse.matiskova.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static String userTopic;
    public static int numberOfQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button chemistryButton = findViewById(R.id.chemistryButton);
        Button biologyButton = findViewById(R.id.biologyButton);

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
        count.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialog.setView(count);

        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                numberOfQuestion = Integer.parseInt(count.getText().toString());
                Intent intent = new Intent(MainActivity.this, QuestionMain.class);
                startActivity(intent);
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