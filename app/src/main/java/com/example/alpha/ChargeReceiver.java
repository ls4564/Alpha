package com.example.alpha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ChargeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the action from the broadcasted intent.
        String action = intent.getAction();

        if (action == null) {
            return;
        }

        // Check which action was received and show the appropriate toast.
        if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
            Toast.makeText(context, "Connected to power", Toast.LENGTH_SHORT).show();
        } else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            Toast.makeText(context, "Disconnected from power", Toast.LENGTH_SHORT).show();
        }
    }
}
