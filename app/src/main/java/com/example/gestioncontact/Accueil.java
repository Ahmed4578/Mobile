package com.example.gestioncontact;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class Accueil extends AppCompatActivity {
   public static ArrayList<Contact> data = new ArrayList<>();
    private TextView tvusername;
    private Button btnajout;
    private Button btnaff;

    private  Button btnqte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accueil);
        tvusername=findViewById(R.id.tvuser_accueil);
        btnajout=findViewById(R.id.btnajout_acc);
        btnaff=findViewById(R.id.btnaff_acc);
        btnqte=findViewById(R.id.btnqte_acc);
        Intent x = this.getIntent();
        Bundle b = x.getExtras();
        String u = b.getString("USER");
        tvusername.setText("Accueil de M. "+u);
        btnajout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Accueil.this,Ajouter.class);
                startActivity(i);
            }
        });
        btnaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Accueil.this,Afficher.class);
                startActivity(i);

            }
        });
        btnqte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear(); // Clear all saved data
                editor.apply();

                Intent i = new Intent(Accueil.this, MainActivity.class);
                startActivity(i);
                finish(); // Close the current activity
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}