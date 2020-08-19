package com.example.mycoffeeshop.ViewModels;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;

public class ShoppingCartViewModel extends ViewModel {

    private FirebaseFirestore fireStoreDB = FirebaseFirestore.getInstance();

    public FirebaseFirestore readFirebase() {
        return fireStoreDB;
    }

}
