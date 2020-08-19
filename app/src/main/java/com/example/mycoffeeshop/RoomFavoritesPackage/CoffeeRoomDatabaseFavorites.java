package com.example.mycoffeeshop.RoomFavoritesPackage;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.Context;

@Database(entities = {CoffeeFavorites.class}, version = 1, exportSchema = false)
public abstract class CoffeeRoomDatabaseFavorites extends RoomDatabase {

    public abstract CoffeeDaoFavorites coffeeDao();
    private static volatile CoffeeRoomDatabaseFavorites INSTANCE;

    public static CoffeeRoomDatabaseFavorites getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CoffeeRoomDatabaseFavorites.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CoffeeRoomDatabaseFavorites.class, "coffee_database_favorites").allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
