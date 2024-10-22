package com.example.gestioncontact;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Call extends AppCompatActivity {
    TextView Tnom, Tnumero;
    ImageButton btnCall, btnDelete;
    String id, nom, pseudo, numero;

    static final int REQUEST_CALL_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        // Apply insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        Tnom = findViewById(R.id.contact_nom);
        Tnumero = findViewById(R.id.contact_number);
        btnCall = findViewById(R.id.btnCall);
        btnDelete = findViewById(R.id.btnDelete);

        // Retrieve intent extras safely
        id = getIntent().getStringExtra("id");
        nom = getIntent().getStringExtra("nom");
        pseudo = getIntent().getStringExtra("pseudo");
        numero = getIntent().getStringExtra("numero");

        // Set text in the TextViews
        Tnom.setText(nom != null ? nom : "Unknown");
        Tnumero.setText(numero != null ? numero : "Unknown");

        // Handle call button click
        btnCall.setOnClickListener(view -> {
            if (numero != null && !numero.isEmpty()) {
                makePhoneCall(numero);
            } else {
                showErrorMessage("Invalid phone number.");
            }
        });

        // Handle delete button click
        btnDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Call.this);
            builder.setTitle("Delete " + nom + " ?");
            builder.setMessage("Are you sure you want to delete " + nom + " ?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                DbHelper DB = new DbHelper(Call.this);
                DB.deleteOneRow(id);
                finish();  // Close the activity after deletion
            });
            builder.setNegativeButton("No", (dialogInterface, i) -> {
                // Do nothing, just close the dialog
            });
            builder.create().show();
        });
    }

    // Method to handle phone calls
    private void makePhoneCall(String phoneNumber) {
        // Check for call permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the CALL_PHONE permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            // Permission already granted, make the call
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, make the call
                makePhoneCall(numero);
            } else {
                // Permission denied, show an error message
                showErrorMessage("Permission to make calls denied.");
            }
        }
    }

    // Method to show error message
    private void showErrorMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }
}
