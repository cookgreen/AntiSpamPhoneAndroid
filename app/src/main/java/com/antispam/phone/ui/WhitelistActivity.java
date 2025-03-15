package com.antispam.phone.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.antispam.phone.R;
import com.antispam.phone.adapter.WhitelistAdapter;
import com.antispam.phone.data.WhitelistEntry;
import com.antispam.phone.viewmodel.WhitelistViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class WhitelistActivity extends AppCompatActivity {

    private WhitelistViewModel whitelistViewModel;
    private WhitelistAdapter adapter;
    private TextView textViewEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whitelist);
        
        // Set up RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        
        adapter = new WhitelistAdapter();
        recyclerView.setAdapter(adapter);
        
        // Set up ViewModel
        whitelistViewModel = new ViewModelProvider(this).get(WhitelistViewModel.class);
        whitelistViewModel.getAllEntries().observe(this, new Observer<List<WhitelistEntry>>() {
            @Override
            public void onChanged(List<WhitelistEntry> whitelistEntries) {
                adapter.setEntries(whitelistEntries);
                updateEmptyView(whitelistEntries.isEmpty());
            }
        });
        
        // Set up empty view
        textViewEmpty = findViewById(R.id.text_view_empty);
        
        // Set up FAB for adding new entries
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEntryDialog();
            }
        });
        
        // Set up item click listener
        adapter.setOnItemClickListener(new WhitelistAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(WhitelistEntry entry) {
                whitelistViewModel.delete(entry);
                Toast.makeText(WhitelistActivity.this, R.string.entry_deleted, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Set title
        setTitle(R.string.whitelist_management);
    }
    
    private void updateEmptyView(boolean isEmpty) {
        if (isEmpty) {
            textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            textViewEmpty.setVisibility(View.GONE);
        }
    }
    
    private void showAddEntryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_to_whitelist);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);
        
        final EditText nameInput = new EditText(this);
        nameInput.setHint(R.string.name_hint);
        layout.addView(nameInput);
        
        final EditText phoneInput = new EditText(this);
        phoneInput.setHint(R.string.phone_number_hint);
        layout.addView(phoneInput);
        
        builder.setView(layout);
        
        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameInput.getText().toString().trim();
                String phoneNumber = phoneInput.getText().toString().trim();
                
                if (phoneNumber.isEmpty()) {
                    Toast.makeText(WhitelistActivity.this, R.string.phone_number_required, Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (name.isEmpty()) {
                    name = phoneNumber; // Use phone number as name if not provided
                }
                
                WhitelistEntry entry = new WhitelistEntry(phoneNumber, name);
                whitelistViewModel.insert(entry);
                Toast.makeText(WhitelistActivity.this, R.string.entry_added, Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        
        builder.show();
    }
}