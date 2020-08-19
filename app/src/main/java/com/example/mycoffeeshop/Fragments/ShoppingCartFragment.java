package com.example.mycoffeeshop.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycoffeeshop.Adapters.AdapterShoppingRV;
import com.example.mycoffeeshop.Models.ProductModel;
import com.example.mycoffeeshop.OthersPackage.Config_paypal;
import com.example.mycoffeeshop.Pages.PaymentDetailsActivity;
import com.example.mycoffeeshop.R;
import com.example.mycoffeeshop.ViewModels.ShoppingCartViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;

public class ShoppingCartFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private AdapterShoppingRV mAdapterShoppingCart;
    private ArrayList<ProductModel> arrayList;
    private RecyclerView recyclerView;
    private ListenerRegistration fireStoreListener;
    private ShoppingCartViewModel shoppingCartViewModel;
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Button btnPayment;
    private static final int PAYPAL_REQUEST_CODE = 999;

    //showing the user the current total price:
    private TextView totalPriceText;
    //the calculation variables
    private int totalPriceNum, totalQuantity;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config_paypal.PAYPAL_CLIENT_ID);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_shoppingcart, container, false);

        initUI();
        initListeners();
        initRecyclerView();
        startServices();
        getReadFirebase();

        return mView;
    }

    private void initUI() {
        recyclerView = mView.findViewById(R.id.recyclerViewShoppingCart);
        btnPayment = mView.findViewById(R.id.btnPayment);
        totalPriceText = mView.findViewById(R.id.totalPrice);
        arrayList = new ArrayList<>();
        shoppingCartViewModel = ViewModelProviders.of(this).get(ShoppingCartViewModel.class);
    }

    private void initListeners() {
        btnPayment.setOnClickListener(this);
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));
    }

    private void startServices() {
        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        requireContext().startService(intent);
    }

    //reading shopping cart items
    private void getReadFirebase() {
        shoppingCartViewModel.readFirebase()
                .collection("users")
                .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .collection("shoppingCartProductList " + currentFirebaseUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayList.clear();

                        for (DocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                            ProductModel productItem = doc.toObject(ProductModel.class);
                            assert productItem != null;
                            productItem.setId(doc.getId());
                            arrayList.add(productItem);
                        }

                        mAdapterShoppingCart = new AdapterShoppingRV(arrayList, mView.getContext());
                        recyclerView.setAdapter(mAdapterShoppingCart);

                        if (mAdapterShoppingCart.getItemCount() > 0) {
                            btnPayment.setVisibility(View.VISIBLE);
                        } else {
                            btnPayment.setVisibility(View.GONE);
                        }

                        totalPriceNum = 0;
                        totalQuantity = 0;
                        for (int i = 0; i < arrayList.size(); i++) {

                            totalPriceNum += arrayList.get(i).getPrice() * arrayList.get(i).getQuantity();
                            totalQuantity += arrayList.get(i).getQuantity();
                        }

                        totalPriceText.setText(String.valueOf(totalPriceNum));
                    } else {
                        Toast.makeText(mView.getContext(), "שגיאה בקבלת המידע", Toast.LENGTH_SHORT).show();
                    }
                });

        // Listener FireStore
        fireStoreListener = shoppingCartViewModel.readFirebase()
                .collection("users")
                .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .collection("shoppingCartProductList " + currentFirebaseUser.getEmail())
                .addSnapshotListener((documentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(mView.getContext(), " רשימת הלייב נכשלה " + e, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    arrayList.clear();

                    assert documentSnapshots != null;
                    for (DocumentSnapshot doc : documentSnapshots) {
                        ProductModel productModel = doc.toObject(ProductModel.class);
                        assert productModel != null;
                        productModel.setId(doc.getId());
                        arrayList.add(productModel);
                    }

                    mAdapterShoppingCart = new AdapterShoppingRV(arrayList, mView.getContext());

                    if (mAdapterShoppingCart.getItemCount() > 0) {
                        btnPayment.setVisibility(View.VISIBLE);
                    } else {
                        btnPayment.setVisibility(View.GONE);
                    }

                    totalPriceNum = 0;
                    totalQuantity = 0;
                    for (int i = 0; i < arrayList.size(); i++) {
                        totalPriceNum += arrayList.get(i).getPrice() * arrayList.get(i).getQuantity();
                        totalQuantity += arrayList.get(i).getQuantity();
                    }

                    totalPriceText.setText("Current total Price: " + totalPriceNum);
                });
    }//getReadFirebase END

    private void processPayment() {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(totalPriceNum), "ILS",
                "Purchase Goods", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPayment:
                new AlertDialog.Builder(requireActivity())
                        .setTitle("רכישת פריטים")
                        .setMessage("כמות: " + totalQuantity + "\n" + "סכום: " + totalPriceNum)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> processPayment())
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                break;
        }//switch END
    }//onClick END

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(getActivity(), PaymentDetailsActivity.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("Quantity", totalQuantity)
                                .putExtra("Amount", totalPriceNum));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_SHORT).show();
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(getActivity(), "Invalid", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        fireStoreListener.remove();

        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        requireContext().stopService(intent);
    }

}
