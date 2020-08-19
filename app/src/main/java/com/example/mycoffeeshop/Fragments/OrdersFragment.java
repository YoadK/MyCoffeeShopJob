package com.example.mycoffeeshop.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycoffeeshop.Adapters.AdapterOrdersRV;
import com.example.mycoffeeshop.Models.OrdersModel;
import com.example.mycoffeeshop.R;
import com.example.mycoffeeshop.ViewModels.OrdersViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class OrdersFragment extends Fragment {

    private View mView;
    private ArrayList<OrdersModel> arrayList;
    private RecyclerView rv;
    private AdapterOrdersRV adapter;
    private ListenerRegistration fireStoreListener;
    private OrdersViewModel ordersViewModel;
    private FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_orders, container, false);

        initUI();
        initRecyclerView();
        getReadFirebase();

        return mView;
    }

    private void initUI() {
        rv = mView.findViewById(R.id.recyclerViewHome);
        arrayList = new ArrayList<>();
        ordersViewModel = ViewModelProviders.of(this).get(OrdersViewModel.class);
    }

    private void initRecyclerView() {
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(mView.getContext()));
    }

    private void getReadFirebase() {
        ordersViewModel.readFirebase()
                .collection("users")
                .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .collection("shoppingCartProductOrdersList " + currentFirebaseUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        arrayList.clear();

                        for (DocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                            OrdersModel ordersModel = doc.toObject(OrdersModel.class);
                            assert ordersModel != null;
                            ordersModel.setId(doc.getId());
                            arrayList.add(ordersModel);
                        }

                        sortList(arrayList);
                        adapter = new AdapterOrdersRV(arrayList, mView.getContext());
                        rv.setAdapter(adapter);
                    } else {
                        Toast.makeText(mView.getContext(), "שגיאה בקבלת המידע", Toast.LENGTH_SHORT).show();
                    }
                });

        // Listener FireStore
        fireStoreListener = ordersViewModel.readFirebase()
                .collection("users")
                .document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .collection("shoppingCartProductOrdersList " + currentFirebaseUser.getEmail())
                .addSnapshotListener((documentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(mView.getContext(), "רשימת הלייב נכשלה" + e, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    arrayList.clear();

                    assert documentSnapshots != null;
                    for (DocumentSnapshot doc : documentSnapshots) {
                        OrdersModel ordersModel = doc.toObject(OrdersModel.class);
                        assert ordersModel != null;
                        ordersModel.setId(doc.getId());
                        arrayList.add(ordersModel);
                    }

                    sortList(arrayList);
                    adapter = new AdapterOrdersRV(arrayList, mView.getContext());
                    rv.setAdapter(adapter);
                });
    }

    private void sortList(ArrayList<OrdersModel> ordersModelArrayList) {
        Collections.sort(ordersModelArrayList, (lhs, rhs) -> rhs.getDate().compareTo(lhs.getDate()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        fireStoreListener.remove();
    }

}
