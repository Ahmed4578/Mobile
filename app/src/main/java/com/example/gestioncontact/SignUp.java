package com.example.gestioncontact;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUp extends AppCompatActivity {
    LoginHelper LHelper;
    private TextView sgn_nom;
    private TextView sgn_mp;
    private Button btnval_sgn;
    private Button btnqte_sgn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        LHelper = new LoginHelper(this);
        sgn_nom = findViewById(R.id.ednom_sgn);
        sgn_mp = findViewById(R.id.edmp_sgn);
        btnval_sgn = findViewById(R.id.btnval_sgn);
        btnqte_sgn = findViewById(R.id.btnqte_sgn);
        btnval_sgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nom = sgn_nom.getText().toString();
                String password = sgn_mp.getText().toString();
                if(nom.equals("")||password.equals(""))
                    Toast.makeText(SignUp.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                else{

                        Boolean checkUserNom = LHelper.checkNom(nom);
                        if(checkUserNom == false){
                            Boolean insert = LHelper.insertData(nom, password);
                            if(insert == true){
                                Toast.makeText(SignUp.this, "Signup Successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(SignUp.this, "Signup Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(SignUp.this, "User already exists! Please login", Toast.LENGTH_SHORT).show();
                        }

                }
            }

        });
        btnqte_sgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp.this.finish();
            }

    });
}
}