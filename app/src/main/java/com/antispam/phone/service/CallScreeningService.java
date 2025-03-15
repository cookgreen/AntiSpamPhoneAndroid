package com.antispam.phone.service;

import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.telecom.Connection;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;

import com.antispam.phone.data.WhitelistRepository;

/**
 * Service that screens incoming calls and blocks those not in the whitelist
 */
public class CallScreeningService extends android.telecom.CallScreeningService {
    private static final String TAG = "CallScreeningService";
    private WhitelistRepository whitelistRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        whitelistRepository = new WhitelistRepository(getApplication());
        Log.d(TAG, "CallScreeningService created");
    }

    @Override
    public void onScreenCall(Call.Details callDetails) {
        String phoneNumber = callDetails.getHandle().getSchemeSpecificPart();
        Log.d(TAG, "Screening call from: " + phoneNumber);
        
        // Create response builder
        CallResponse.Builder responseBuilder = new CallResponse.Builder();
        
        // Check if the number is in the whitelist
        if (whitelistRepository.isNumberInWhitelist(phoneNumber)) {
            // Allow the call if it's in the whitelist
            Log.d(TAG, "Number is in whitelist, allowing call");
            responseBuilder.setDisallowCall(false)
                          .setRejectCall(false)
                          .setSilenceCall(false);
        } else {
            // Block the call if it's not in the whitelist
            Log.d(TAG, "Number is not in whitelist, blocking call");
            responseBuilder.setDisallowCall(true)
                          .setRejectCall(true)
                          .setSkipCallLog(false)
                          .setSkipNotification(false);
        }
        
        // Respond to the call
        respondToCall(callDetails, responseBuilder.build());
    }
}