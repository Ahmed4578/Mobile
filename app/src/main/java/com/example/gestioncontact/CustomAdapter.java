package com.example.gestioncontact;

import static com.example.gestioncontact.Call.REQUEST_CALL_PERMISSION;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<String> contact_id, contact_pseudo, contact_nom, contact_numero;
    private ArrayList<String> contact_id_full, contact_pseudo_full, contact_nom_full, contact_numero_full;
    private ActivityResultLauncher<Intent> callActivityResultLauncher;

    // Constructor
    public CustomAdapter(Activity activity, Context context, ArrayList<String> contact_id,
                         ArrayList<String> contact_pseudo, ArrayList<String> contact_nom,
                         ArrayList<String> contact_numero,
                         ActivityResultLauncher<Intent> callActivityResultLauncher) {
        this.activity = activity;
        this.context = context;
        this.contact_id = (contact_id != null) ? contact_id : new ArrayList<>();
        this.contact_nom = (contact_nom != null) ? contact_nom : new ArrayList<>();
        this.contact_pseudo = (contact_pseudo != null) ? contact_pseudo : new ArrayList<>();
        this.contact_numero = (contact_numero != null) ? contact_numero : new ArrayList<>();
        this.callActivityResultLauncher = callActivityResultLauncher;

        // Create a copy of the original data for filtering
        this.contact_id_full = new ArrayList<>(contact_id);
        this.contact_nom_full = new ArrayList<>(contact_nom);
        this.contact_pseudo_full = new ArrayList<>(contact_pseudo);
        this.contact_numero_full = new ArrayList<>(contact_numero);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.Tcontact_nom.setText(contact_nom.get(position));
        holder.Tcontact_pseudo.setText(contact_pseudo.get(position));
        holder.Tcontact_numero.setText(contact_numero.get(position));

        // Delete Button OnClickListener
        holder.btnDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete " + contact_nom.get(position) + " ?");
            builder.setMessage("Are you sure you want to delete " + contact_nom.get(position) + " ?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                DbHelper DB = new DbHelper(context);
                DB.deleteOneRow(contact_id.get(position));

                // Remove contact from the list and notify the adapter of item removed
                contact_id.remove(position);
                contact_nom.remove(position);
                contact_pseudo.remove(position);
                contact_numero.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, contact_id.size());
            });
            builder.setNegativeButton("No", (dialogInterface, i) -> {
                // No action
            });
            builder.create().show();
        });

        // Call Button OnClickListener
        holder.btnCall.setOnClickListener(view -> makePhoneCall(contact_numero.get(position)));

        // Update Button OnClickListener
        holder.btnUpdate.setOnClickListener(view -> showUpdatePopup(view, position));
    }

    private void makePhoneCall(String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Request CALL_PHONE permission if not granted
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            // Permission granted, make the call
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            context.startActivity(intent);
        }
    }



    private void showUpdatePopup(View view, int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_update, null);

      EditText updateNom = popupView.findViewById(R.id.Ucontact_nom);
        EditText updatePseudo = popupView.findViewById(R.id.Ucontact_pseudo);
        EditText updateNumero = popupView.findViewById(R.id.Ucontact_number);

       updateNom.setText(contact_nom.get(position));
        updatePseudo.setText(contact_pseudo.get(position));
        updateNumero.setText(contact_numero.get(position));

        // Create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(popupView, width , height, focusable);

        popupWindow.showAsDropDown(view);
        View rootView = ((Activity) context).getWindow().getDecorView().getRootView();
        popupWindow.showAtLocation(rootView, Gravity.LEFT, 0, 0);
        Button btnSave = popupView.findViewById(R.id.update_button);
        btnSave.setOnClickListener(v -> {

            String newNom = updateNom.getText().toString().trim();
            String newPseudo = updatePseudo.getText().toString().trim();
            String newNumero = updateNumero.getText().toString().trim();


            if (newNom.isEmpty() || newPseudo.isEmpty() || newNumero.isEmpty()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                long numero = Long.parseLong(newNumero); // Changed to long for wider range
                // Update the contact in the database
                DbHelper dbHelper = new DbHelper(context);
                boolean isUpdated = dbHelper.updateData(contact_id.get(position), newNom, newPseudo, (int) numero);

                if (isUpdated) {
                    // Update the local lists with the new values
                    contact_nom.set(position, newNom);
                    contact_pseudo.set(position, newPseudo);
                    contact_numero.set(position, newNumero);

                    // Notify the adapter about the change
                    notifyItemChanged(position);
                    Toast.makeText(context, "Contact updated", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle error
                    Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid number format", Toast.LENGTH_SHORT).show();
            }

            popupWindow.dismiss();
        });
    }


    @Override
    public int getItemCount() {
        return contact_id.size();  // Return the number of contacts
    }

    // ViewHolder class to hold view references
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Tcontact_nom, Tcontact_pseudo, Tcontact_numero;
        ImageButton btnCall, btnDelete, btnUpdate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Tcontact_nom = itemView.findViewById(R.id.contact_nom);
            Tcontact_pseudo = itemView.findViewById(R.id.contact_pseudo);
            Tcontact_numero = itemView.findViewById(R.id.contact_number);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
        }
    }

    public Filter getFilter() {
        return contactFilter;
    }

    private final Filter contactFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new ArrayList<>();
            List<String> filteredListPseudo = new ArrayList<>();
            List<String> filteredListNom = new ArrayList<>();
            List<String> filteredListNumero = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                // No filter applied, return full list
                filteredList.addAll(contact_id_full);
                filteredListPseudo.addAll(contact_pseudo_full);
                filteredListNom.addAll(contact_nom_full);
                filteredListNumero.addAll(contact_numero_full);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (int i = 0; i < contact_nom_full.size(); i++) {
                    if (contact_nom_full.get(i).toLowerCase().contains(filterPattern) ||
                            contact_pseudo_full.get(i).toLowerCase().contains(filterPattern) ||
                            contact_numero_full.get(i).contains(filterPattern)) {

                        filteredList.add(contact_id_full.get(i));
                        filteredListPseudo.add(contact_pseudo_full.get(i));
                        filteredListNom.add(contact_nom_full.get(i));
                        filteredListNumero.add(contact_numero_full.get(i));
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = new ArrayList[] {(ArrayList) filteredList, (ArrayList) filteredListPseudo, (ArrayList) filteredListNom,(ArrayList) filteredListNumero };
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList[] filteredResults = (ArrayList[]) results.values;

            contact_id.clear();
            contact_pseudo.clear();
            contact_nom.clear();
            contact_numero.clear();

            contact_id.addAll(filteredResults[0]);
            contact_pseudo.addAll(filteredResults[1]);
            contact_nom.addAll(filteredResults[2]);
            contact_numero.addAll(filteredResults[3]);

            notifyDataSetChanged();
        }
    };


          };

