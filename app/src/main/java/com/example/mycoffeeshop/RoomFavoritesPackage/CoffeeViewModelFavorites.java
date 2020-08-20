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

    public LiveData<List<CoffeeFavorites>> getAllCoffee() {
        return coffeeRepositoryFavorites.getAllCoffee();
    }

    public void insertCoffee(CoffeeFavorites coffeeFavorites) {
        coffeeRepositoryFavorites.insertCoffee(coffeeFavorites);
    }

    public void deleteAll() {
        coffeeRepositoryFavorites.deleteLastSearch();
    }

    public void deleteCoffee(CoffeeFavorites coffeeFavorites) {
        coffeeRepositoryFavorites.deleteCoffee(coffeeFavorites);
    }

    public void updateCoffee(CoffeeFavorites coffeeFavorites) {
        coffeeRepositoryFavorites.updateCoffee(coffeeFavorites);
    }

}
