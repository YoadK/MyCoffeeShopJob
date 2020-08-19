package com.example.mycoffeeshop.Pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycoffeeshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class PaymentDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtId, txtAmount, txtStatus;
    private Button btnOkPayment;
    private FirebaseFirestore fireStoreDB = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        initUI();
        initListeners();
    }

    private void initUI() {
        txtId = findViewById(R.id.txtId);
        txtAmount = findViewById(R.id.txtAmount);
        txtStatus = findViewById(R.id.txtStatus);
        btnOkPayment = findViewById(R.id.btnOkPayment);

        try {
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(getIntent().getStringExtra("PaymentDetails")));
            showDetails(jsonObject.getJSONObject("response"), getIntent().getIntExtra("Quantity", 0), getIntent().getIntExtra("Amount", 0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initListeners() {
        btnOkPayment.setOnClickListener(this);
    }

    private void showDetails(JSONObject response, int quantity, int paymentAmount) {
        try {
            txtId.setText(response.getString("id"));
            txtAmount.setText("₪" + paymentAmount);
            txtStatus.setText(response.getString("state"));

            if (response.getString("state").equals("approved")) {
                postPaymentFirebase(quantity, paymentAmount);

                Toast.makeText(this, "The payment success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "The payment failed", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void postPaymentFirebase(int quantity, int price) {
        Map<String, Object> userShoppingCartProduct = new HashMap<>();
        userShoppingCartProduct.put("quantity", quantity);
        userShoppingCartProduct.put("totalPrice", price);
        userShoppingCartProduct.put("date", getCurrentDateTime());

        fireStoreDB
                .collection("users")
                .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .collection("shoppingCartProductOrdersList " + currentFirebaseUser.getEmail())
                .document()
                .set(userShoppingCartProduct)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "ההזמנה בוצעה", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(error -> Toast.makeText(this, "שגיאה בהוספת הפריט לסל הקניות", Toast.LENGTH_SHORT).show());
    }

    private Date getCurrentDateTime() {
        final String DATE_FORMAT = "dd-MM-yyyy\nHH:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        return Calendar.getInstance().getTime();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOkPayment:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }//switch END
    }//onClick END

}
