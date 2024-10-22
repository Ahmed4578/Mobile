package com.example.gestioncontact;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Ajouter extends AppCompatActivity {

    private Button btnQuitt;
    private Button btnSv;
    private TextView enom;
    private TextView epseudo;
    private TextView enumero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ajouter);
        btnQuitt=findViewById(R.id.bt_Quitter);
        btnSv=findViewById(R.id.bt_Save);
        enom = findViewById(R.id.edNom);
        epseudo = findViewById(R.id.ed_ps);
        enumero = findViewById(R.id.edNumero);
        btnQuitt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnSv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nom=enom.getText().toString();
                String pseudo=epseudo.getText().toString();
                String numero = enumero.getText().toString();
                Contact c = new Contact(pseudo,nom,numero);
                Accueil.data.add(c);
                DbHelper myDB = new DbHelper(Ajouter.this);
                myDB.addContact(enom.getText().toString().trim(),
                        epseudo.getText().toString().trim(),
                        Integer.valueOf(enumero.getText().toString().trim()));
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}