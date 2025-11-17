package com.example.alpha;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
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
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_pay);

        paymentsClient = GooglePayUtil.createPaymentsClient(this);
        googlePayButtonContainer = findViewById(R.id.googlePayButtonContainer);

        // --- FORCE TEST --- 
        Toast.makeText(this, "Forcing button visibility for test.", Toast.LENGTH_LONG).show();
        googlePayButtonContainer.setVisibility(View.VISIBLE);
        addGooglePayButton();
    }

    private void addGooglePayButton() {
        ButtonOptions buttonOptions = ButtonOptions.newBuilder()
                .setButtonTheme(1) // THEME_DARK
                .setButtonType(1)  // BUTTON_TYPE_BUY
                .build();

        PayButton googlePayButton = new PayButton(this);
        googlePayButton.initialize(buttonOptions);
        googlePayButton.setOnClickListener(v -> requestPayment());

        // FIX: Add LayoutParams to ensure the button has a size.
        // This tells the button to fill the container it's being added to.
        googlePayButtonContainer.addView(googlePayButton, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void requestPayment() {
        Optional<JSONObject> paymentDataRequestJson = GooglePayUtil.getPaymentDataRequest("1.00");
        if (!paymentDataRequestJson.isPresent()) {
            Toast.makeText(this, "Error creating payment request.", Toast.LENGTH_SHORT).show();
            return;
        }

        PaymentDataRequest request = PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());
        com.google.android.gms.tasks.Task<PaymentData> task = paymentsClient.loadPaymentData(request);
        AutoResolveHelper.resolveTask(task, this, LOAD_PAYMENT_DATA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    PaymentData paymentData = PaymentData.getFromIntent(data);
                    handlePaymentSuccess(paymentData);
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(this, "Payment cancelled.", Toast.LENGTH_SHORT).show();
                    break;
                case AutoResolveHelper.RESULT_ERROR:
                    Toast.makeText(this, "An error occurred.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void handlePaymentSuccess(PaymentData paymentData) {
        String paymentInformation = paymentData.toJson();
        Log.d("GooglePayResult", paymentInformation);
        Toast.makeText(this, "Payment successful! Check logs for details.", Toast.LENGTH_LONG).show();
    }
}

class GooglePayUtil {
    public static PaymentsClient createPaymentsClient(BaseActivity activity) {
        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                .build();
        return Wallet.getPaymentsClient(activity, walletOptions);
    }

    public static Optional<JSONObject> getPaymentDataRequest(String price) {
        try {
            JSONObject paymentDataRequest = new JSONObject();
            paymentDataRequest.put("apiVersion", 2);
            paymentDataRequest.put("apiVersionMinor", 0);

            JSONArray allowedPaymentMethods = new JSONArray();
            JSONObject cardPaymentMethod = getBaseCardPaymentMethod();
            cardPaymentMethod.put("tokenizationSpecification", getGatewayTokenizationSpecification());
            allowedPaymentMethods.put(cardPaymentMethod);
            paymentDataRequest.put("allowedPaymentMethods", allowedPaymentMethods);
            paymentDataRequest.put("merchantInfo", getMerchantInfo());
            paymentDataRequest.put("transactionInfo", getTransactionInfo(price));

            return Optional.of(paymentDataRequest);
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

    private static JSONObject getMerchantInfo() throws JSONException {
        return new JSONObject().put("merchantName", "Example Merchant");
    }

    private static JSONObject getTransactionInfo(String price) throws JSONException {
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("totalPrice", price);
        transactionInfo.put("currencyCode", "ILS");
        transactionInfo.put("countryCode", "IL");

        return transactionInfo;
    }

    private static JSONObject getGatewayTokenizationSpecification() throws JSONException {
        JSONObject tokenizationSpecification = new JSONObject();
        tokenizationSpecification.put("type", "PAYMENT_GATEWAY");
        JSONObject parameters = new JSONObject();
        parameters.put("gateway", "example");
        parameters.put("gatewayMerchantId", "exampleGatewayMerchantId");
        tokenizationSpecification.put("parameters", parameters);

        return tokenizationSpecification;
    }
}
