package com.example.gestioncontact;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Afficher extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private SearchView searchEditText;

    private ArrayList<String> contact_id, contact_nom, contact_pseudo, contact_numero;
    private DbHelper DB;

    // Declare the ActivityResultLauncher
    private ActivityResultLauncher<Intent> callActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afficher);

        // Initialize UI components
        recyclerView = findViewById(R.id.id_RV);
        emptyView = findViewById(R.id.empty_view);
        searchEditText = findViewById(R.id.ed_searchView);  // Initialize the search EditText

        // Initialize database helper and ArrayLists
        DB = new DbHelper(Afficher.this);
        contact_id = new ArrayList<>();
        contact_nom = new ArrayList<>();
        contact_pseudo = new ArrayList<>();
        contact_numero = new ArrayList<>();

        // Register the ActivityResultLauncher
        callActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        refreshData();
                    }
                });

        // Store contact data into arrays
        storeDataInArrays();

        // Initialize the adapter
        final CustomAdapter customAdapter = new CustomAdapter(Afficher.this, this, contact_id, contact_nom, contact_pseudo, contact_numero, callActivityResultLauncher);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Afficher.this));

        toggleRecyclerViewVisibility();

        // Add TextWatcher to the search EditText for real-time filtering
        searchEditText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                customAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                customAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    // Method to fetch data from the database and store it in the arrays
    private void storeDataInArrays() {
        try (Cursor cursor = DB.readAllData()) {
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    contact_id.add(cursor.getString(0));
                    contact_nom.add(cursor.getString(1));
                    contact_pseudo.add(cursor.getString(2));
                    contact_numero.add(cursor.getString(3));
                }
            }
        }
        toggleRecyclerViewVisibility();
    }

    // Method to toggle RecyclerView visibility based on whether data is present
    private void toggleRecyclerViewVisibility() {
        if (contact_id.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    // Method to refresh data after adding, updating, or deleting a contact
    private void refreshData() {
        // Clear existing lists
        contact_id.clear();
        contact_nom.clear();
        contact_pseudo.clear();
        contact_numero.clear();

        // Reload data from the database
        storeDataInArrays();

        // Notify the adapter of the changes
        if (recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }
}
