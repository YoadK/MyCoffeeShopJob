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

import com.example.mycoffeeshop.Adapters.AdapterHomeRV;
import com.example.mycoffeeshop.Models.ProductModel;
import com.example.mycoffeeshop.R;
import com.example.mycoffeeshop.ViewModels.HomeViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment  {

    private View mView;
    private ArrayList<ProductModel> arrayList;
    private RecyclerView rv;
    private AdapterHomeRV adapter;
    private ListenerRegistration fireStoreListener;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);


        initUI();

        initRecyclerView();
        getReadFirebase();

        return mView;
    }

    private void initUI() {
        rv = mView.findViewById(R.id.recyclerViewHome);

        arrayList = new ArrayList<>();
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);


    }

    private void initRecyclerView() {
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(mView.getContext()));
    }

    //Reading product details from DB, adding them to local arrayList, letting the adapter to show that arrayList in the recyclerView
    private void getReadFirebase() {
        homeViewModel.readFirebase()
                .collection("products")
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

                        adapter = new AdapterHomeRV(arrayList, mView.getContext());
                        rv.setAdapter(adapter);
                    } else {
                        Toast.makeText(mView.getContext(), "שגיאה בקבלת המידע", Toast.LENGTH_SHORT).show();
                    }
                });

        // Listener FireStore
        fireStoreListener = homeViewModel.readFirebase()
                .collection("products")
                .addSnapshotListener((documentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(mView.getContext(), "רשימת הלייב נכשלה" + e, Toast.LENGTH_SHORT).show();
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

                    adapter = new AdapterHomeRV(arrayList, mView.getContext());
                    rv.setAdapter(adapter);
                });
    }




    @Override
    public void onDestroy() {
        super.onDestroy();

        fireStoreListener.remove();
    }



}
