package com.example.alpha;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.button.ButtonOptions;
import com.google.android.gms.wallet.button.PayButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class Google_Pay extends BaseActivity {

    private PaymentsClient paymentsClient;
    private RelativeLayout googlePayButtonContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_pay);

        // Re-adding the creation of the client, which uses the helper class
        paymentsClient = GooglePayUtil.createPaymentsClient(this);
        googlePayButtonContainer = findViewById(R.id.googlePayButtonContainer);

        checkGooglePayIsReady();
    }

    private void checkGooglePayIsReady() {
        final Optional<JSONObject> isReadyToPayJson = GooglePayUtil.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }

        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());

        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(this, completedTask -> {
            try {
                boolean result = completedTask.getResult(ApiException.class);
                if (result) {
                    addGooglePayButton();
                }
                googlePayButtonContainer.setVisibility(result ? View.VISIBLE : View.GONE);
            } catch (ApiException e) {
                Log.w("isReadyToPay failed", e);
                googlePayButtonContainer.setVisibility(View.GONE);
            }
        });
    }

    private void addGooglePayButton() {
        // WORKAROUND: Using literal integer values to bypass IDE symbol resolution issues.
        // THEME_DARK = 1, BUTTON_TYPE_PLAIN = 5
        ButtonOptions buttonOptions = ButtonOptions.newBuilder()
                .setButtonTheme(1)
                .setButtonType(5)
                .build();

        PayButton googlePayButton = new PayButton(this);
        googlePayButton.initialize(buttonOptions);
        googlePayButton.setOnClickListener(v ->
                Toast.makeText(this, "Google Pay integration is ready!", Toast.LENGTH_SHORT).show()
        );

        googlePayButtonContainer.addView(googlePayButton);
    }
}

// FIX: Re-adding the missing utility class.
class GooglePayUtil {

    public static PaymentsClient createPaymentsClient(BaseActivity activity) {
        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                .build();
        return Wallet.getPaymentsClient(activity, walletOptions);
    }

    public static Optional<JSONObject> getIsReadyToPayRequest() {
        try {
            JSONObject json = new JSONObject();
            json.put("apiVersion", 2);
            json.put("apiVersionMinor", 0);

            JSONArray allowedPaymentMethods = new JSONArray();
            allowedPaymentMethods.put(getBaseCardPaymentMethod());
            json.put("allowedPaymentMethods", allowedPaymentMethods);
            
            // Adding merchantInfo to make the request more robust
            JSONObject merchantInfo = new JSONObject();
            merchantInfo.put("merchantName", "Example Merchant");
            json.put("merchantInfo", merchantInfo);

            return Optional.of(json);
        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    private static JSONObject getBaseCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");

        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", new JSONArray() {{ put("PAN_ONLY"); put("CRYPTOGRAM_3DS"); }});
        parameters.put("allowedCardNetworks", new JSONArray() {{ put("AMEX"); put("DISCOVER"); put("JCB"); put("MASTERCARD"); put("VISA"); }});
        cardPaymentMethod.put("parameters", parameters);

        return cardPaymentMethod;
    }
}
