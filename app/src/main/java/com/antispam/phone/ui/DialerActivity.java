package com.antispam.phone.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.antispam.phone.R;
import com.antispam.phone.viewmodel.WhitelistViewModel;

public class DialerActivity extends AppCompatActivity {

    private EditText phoneNumberInput;
    private WhitelistViewModel whitelistViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);
        
        // Initialize ViewModel
        whitelistViewModel = new WhitelistViewModel(getApplication());
        
        // Initialize UI components
        phoneNumberInput = findViewById(R.id.edit_text_phone_number);
        
        // Set up number buttons
        setupDialPad();
        
        // Set up call button
        ImageButton callButton = findViewById(R.id.button_call);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });
        
        // Set up delete button
        ImageButton deleteButton = findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = phoneNumberInput.getText().toString();
                if (!currentText.isEmpty()) {
                    phoneNumberInput.setText(currentText.substring(0, currentText.length() - 1));
                }
            }
        });
        
        // Long press to clear all
        deleteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                phoneNumberInput.setText("");
                return true;
            }
        });
    }
    
    private void setupDialPad() {
        // Set up number buttons
        int[] buttonIds = {
            R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
            R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
            R.id.button_8, R.id.button_9, R.id.button_star, R.id.button_hash
        };
        
        String[] buttonValues = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "#"
        };
        
        for (int i = 0; i < buttonIds.length; i++) {
            final String value = buttonValues[i];
            Button button = findViewById(buttonIds[i]);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phoneNumberInput.append(value);
                }
            });
        }
    }
    
    private void makePhoneCall() {
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Check if we have permission to make calls
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return;
        }
        
        // Make the call
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission denied to make calls", Toast.LENGTH_SHORT).show();
            }
        }
    }
}