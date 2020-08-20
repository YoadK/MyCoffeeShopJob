package com.example.mycoffeeshop.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycoffeeshop.Adapters.AdapterFavoritesRV;
import com.example.mycoffeeshop.R;
import com.example.mycoffeeshop.RoomFavoritesPackage.CoffeeFavorites;
import com.example.mycoffeeshop.RoomFavoritesPackage.CoffeeViewModelFavorites;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    private View mView;
    private RecyclerView rv;
    private AdapterFavoritesRV adapterFavoritesRV;
    private ArrayList<CoffeeFavorites> arrayList = new ArrayList<>();
    private CoffeeViewModelFavorites coffeeViewModelFavorites;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_coffee_favorites, container, false);

        initUI();
        buildRecyclerView();

        return mView;
    }

    private void initUI() {
        rv = mView.findViewById(R.id.recyclerViewFavorites);
    }

    private void buildRecyclerView() {
        adapterFavoritesRV = new AdapterFavoritesRV(arrayList, getContext());
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(mView.getContext()));
        rv.setAdapter(adapterFavoritesRV);

        coffeeViewModelFavorites = ViewModelProviders.of(this).get(CoffeeViewModelFavorites.class);
        coffeeViewModelFavorites.getAllCoffee().observe(getViewLifecycleOwner(), placesFavorites -> adapterFavoritesRV.setCoffee(placesFavorites));
    }

}
