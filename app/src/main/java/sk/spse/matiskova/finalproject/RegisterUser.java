package sk.spse.matiskova.finalproject;

import static sk.spse.matiskova.finalproject.Login.currentlyLoggedIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private TextView registerUser;
    private EditText editTextNickname, editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    private static final String FILE_NAME = "myFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        registerUser = findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextNickname = findViewById(R.id.nickname);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);

        progressBar = findViewById(R.id.progressBar);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registerUser:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String nickname = editTextNickname.getText().toString().trim();

        if (nickname.isEmpty()) {
            editTextNickname.setError("Vyžaduje si prezývku");
            editTextEmail.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Vyžaduje sa email");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Zadajte platný e-mail");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Vyžaduje sa heslo");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimálna dĺžka hesla je 6 znakov!");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @SuppressLint("SuspiciousIndentation")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            User user = new User(nickname, email);

                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterUser.this, "Používateľ bol úspešne zaregistrovaný!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(RegisterUser.this, ProfileActivity.class));
                                        StoredDataUsingSHaredPref(true);
                                        //dr.child(userId).child("rewriteTest").setValue("test1");
                                        FirebaseDatabase.getInstance().getReference("users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rewriteTest").setValue("test1");
                                        currentlyLoggedIn = true;
                                    } else {
                                        Toast.makeText(RegisterUser.this, "Registrácia zlyhala. Skúste to znova! Skontrolujte svoje internetové pripojenie.", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        RegisterUser.super.onBackPressed();
                                    }
                                }
                            });
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(RegisterUser.this, "Registrácia zlyhala.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void StoredDataUsingSHaredPref(boolean ischecked) {
        SharedPreferences.Editor editor = getSharedPreferences(FILE_NAME, MODE_PRIVATE).edit();
        editor.putBoolean("ischecked", ischecked);
        editor.apply();
    }
}