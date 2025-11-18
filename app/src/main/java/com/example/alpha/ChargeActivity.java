package com.example.alpha;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ChargeActivity extends BaseActivity {

    // The instance of the receiver.
    private ChargeReceiver chargeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);
        setTitle("Charge Demo");

        // Create an instance of the receiver.
        chargeReceiver = new ChargeReceiver();
    }

    /**
     * Dynamically registers the ChargeReceiver to listen for power events.
     */
    public void o_n_Click(View view) {
        IntentFilter batteryFilter = new IntentFilter();
        batteryFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        batteryFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);


        registerReceiver(chargeReceiver, batteryFilter);

        Toast.makeText(this, "Charging receiver is ON", Toast.LENGTH_SHORT).show();
    }

    /**
     * Dynamically unregisters the ChargeReceiver.
     */
    public void Off_Click(View view) {
        try {
            // Unregister the receiver to stop it from listening to events.
            unregisterReceiver(chargeReceiver);
            Toast.makeText(this, "Charging receiver is OFF", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            // This exception is thrown if the receiver was not registered, which is fine.
            Toast.makeText(this, "Receiver was not registered.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(chargeReceiver);
        } catch (IllegalArgumentException e) {
            // This exception means the receiver wasn't registered, so we can ignore it.
        }
    }
}
