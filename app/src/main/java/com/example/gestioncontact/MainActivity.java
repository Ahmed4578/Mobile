package com.example.gestioncontact;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity{
    // decalaration des composantes
    EditText ednom,edmp;
    private Button btnval;
    private Button btnqte;

    private Button btnsgn;

    LoginHelper LHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Recuperation des composantes
        setContentView(R.layout.activity_main);
        ednom=findViewById(R.id.ednom_auth);
        edmp=findViewById(R.id.edmp_auth);
        btnval=findViewById(R.id.btnval_auth);
        btnqte=findViewById(R.id.btnqte_val);
        btnsgn=findViewById(R.id.btnsgn);
        LHelper = new LoginHelper(this);
        SharedPreferences preferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            Intent i = new Intent(MainActivity.this, Accueil.class);
            i.putExtra("USER", preferences.getString("username", ""));
            startActivity(i);
            finish();
            return;
        }
        // ecouteur d'action
        btnqte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.finish();
            }
        });
        btnval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nom=ednom.getText().toString();
                String mp=edmp.getText().toString();
                Boolean checkCredentials = LHelper.checkNomPassword(nom,mp);
                if(checkCredentials == true){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("username", nom);
                    editor.apply();
                    Intent i = new Intent(MainActivity.this,Accueil.class);
                    i.putExtra("USER",nom);
                    startActivity(i);

                }else {
                    Toast.makeText(MainActivity.this,"valeur non valide",Toast.LENGTH_LONG).show();
                }
            }
        });
        btnsgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,SignUp.class);
                startActivity(i);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}