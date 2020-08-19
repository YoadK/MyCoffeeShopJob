package com.example.mycoffeeshop.RoomFavoritesPackage;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class CoffeeViewModelFavorites extends AndroidViewModel {

    private CoffeeRepositoryFavorites coffeeRepositoryFavorites;

    public CoffeeViewModelFavorites(Application application) {
        super(application);

        coffeeRepositoryFavorites = new CoffeeRepositoryFavorites(application);
    }

    public LiveData<List<CoffeeFavorites>> getAllPlaces() {
        return coffeeRepositoryFavorites.getAllCoffee();
    }

    public void insertPlace(List<CoffeeFavorites> coffeeFavorites) {
        coffeeRepositoryFavorites.insertPlace(coffeeFavorites);
    }

    public void insertPlace(CoffeeFavorites coffeeFavorites) {
        coffeeRepositoryFavorites.insertPlace(coffeeFavorites);
    }

    public void deleteAll() {
        coffeeRepositoryFavorites.deleteLastSearch();
    }

    public void deletePlace(CoffeeFavorites coffeeFavorites) {
        coffeeRepositoryFavorites.deletePlace(coffeeFavorites);
    }

    public void updatePlace(CoffeeFavorites coffeeFavorites) {
        coffeeRepositoryFavorites.updatePlace(coffeeFavorites);
    }

    public CoffeeFavorites exist(String name, double lat, double lng) {
        return coffeeRepositoryFavorites.getExist(name, lat, lng);
    }

}
