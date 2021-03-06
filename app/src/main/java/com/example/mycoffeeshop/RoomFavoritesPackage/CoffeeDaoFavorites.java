package com.example.mycoffeeshop.RoomFavoritesPackage;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CoffeeDaoFavorites {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CoffeeFavorites coffeeFavorites);

    @Delete
    void deleteNew(CoffeeFavorites coffeeFavorites);

    @Query("DELETE FROM coffee_table_favorites")
    void deleteAll();

    @Query("SELECT * from coffee_table_favorites")
    LiveData<List<CoffeeFavorites>> getAllCoffee();

    @Update
    void update(CoffeeFavorites... coffeeFavorites);
}