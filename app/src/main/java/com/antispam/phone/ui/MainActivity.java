package com.antispam.phone.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.antispam.phone.R;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SET_DEFAULT_DIALER = 1;
    private static final int REQUEST_CODE_PERMISSIONS = 2;
    
    private String[] requiredPermissions = {
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.WRITE_CALL_LOG,
        Manifest.permission.READ_CONTACTS
    };
    
    // Add Android 9+ (API 28+) permissions
    private String[] androidPPermissions = {
        Manifest.permission.ANSWER_PHONE_CALLS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Set up button to manage whitelist
        Button btnManageWhitelist = findViewById(R.id.btn_manage_whitelist);
        btnManageWhitelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WhitelistActivity.class));
            }
        });
        
        // Set up button to set as default phone app
        Button btnSetDefault = findViewById(R.id.btn_set_default);
        btnSetDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestDefaultDialerStatus();
            }
        });
        
        // Set up button to open dialer
        Button btnOpenDialer = findViewById(R.id.btn_open_dialer);
        btnOpenDialer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DialerActivity.class));
            }
        });
        
        // Check and request permissions
        checkAndRequestPermissions();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateDefaultDialerStatus();
    }
    
    private void updateDefaultDialerStatus() {
        TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
        boolean isDefault = telecomManager != null && 
                           getPackageName().equals(telecomManager.getDefaultDialerPackage());
        
        Button btnSetDefault = findViewById(R.id.btn_set_default);
        if (isDefault) {
            btnSetDefault.setText(R.string.default_dialer_set);
            btnSetDefault.setEnabled(false);
        } else {
            btnSetDefault.setText(R.string.set_as_default_dialer);
            btnSetDefault.setEnabled(true);
        }
    }
    
    private void requestDefaultDialerStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10+ (API 29+)
            RoleManager roleManager = (RoleManager) getSystemService(ROLE_SERVICE);
            if (roleManager.isRoleAvailable(RoleManager.ROLE_DIALER)) {
                Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
                startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER);
            }
        } else {
            // For Android 6.0 (API 23) to Android 9 (API 28)
            Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                    .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
            startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER);
        }
    }
    
    private void checkAndRequestPermissions() {
        // Combine required permissions based on API level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String[] combinedPermissions = new String[requiredPermissions.length + androidPPermissions.length];
            System.arraycopy(requiredPermissions, 0, combinedPermissions, 0, requiredPermissions.length);
            System.arraycopy(androidPPermissions, 0, combinedPermissions, requiredPermissions.length, androidPPermissions.length);
            requiredPermissions = combinedPermissions;
        }
        
        // Check which permissions we need to request
        boolean needToRequest = false;
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                needToRequest = true;
                break;
            }
        }
        
        if (needToRequest) {
            ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_CODE_PERMISSIONS);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (!allGranted) {
                showPermissionExplanationDialog();
            }
        }
    }
    
    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.permissions_required)
                .setMessage(R.string.permissions_explanation)
                .setPositiveButton(R.string.go_to_settings, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CODE_SET_DEFAULT_DIALER) {
            updateDefaultDialerStatus();
            TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
            if (telecomManager != null && 
                getPackageName().equals(telecomManager.getDefaultDialerPackage())) {
                Toast.makeText(this, R.string.default_dialer_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.default_dialer_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }
}