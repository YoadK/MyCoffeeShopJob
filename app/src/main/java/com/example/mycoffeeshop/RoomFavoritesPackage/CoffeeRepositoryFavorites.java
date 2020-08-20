package com.example.mycoffeeshop.RoomFavoritesPackage;

import android.app.Application;

import androidx.lifecycle.LiveData;

import android.os.AsyncTask;

import java.util.List;

public class CoffeeRepositoryFavorites {

    private CoffeeDaoFavorites mCoffeeDaoFavorites;
    private LiveData<List<CoffeeFavorites>> mAllCoffeeFavorites;

    public CoffeeRepositoryFavorites(Application application) {
        CoffeeRoomDatabaseFavorites db = CoffeeRoomDatabaseFavorites.getDatabase(application);
        mCoffeeDaoFavorites = db.coffeeDao();
        mAllCoffeeFavorites = mCoffeeDaoFavorites.getAllCoffee();
    }

    public LiveData<List<CoffeeFavorites>> getAllCoffee() {
        return mAllCoffeeFavorites;
    }

    private static class DeleteLastSearchAsyncTask extends AsyncTask<Void, Void, Void> {

        private CoffeeDaoFavorites coffeeDaoFavorites;

        private DeleteLastSearchAsyncTask(CoffeeDaoFavorites dao) {
            coffeeDaoFavorites = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            coffeeDaoFavorites.deleteAll();
            return null;
        }
    }

    void deleteLastSearch() {
        DeleteLastSearchAsyncTask deleteLastSearchAsyncTask = new DeleteLastSearchAsyncTask(mCoffeeDaoFavorites);
        deleteLastSearchAsyncTask.execute();
    }

    private static class updateCoffeeAsyncTask extends AsyncTask<CoffeeFavorites, Void, Void> {

        private CoffeeDaoFavorites coffeeDaoFavorites;

        private updateCoffeeAsyncTask(CoffeeDaoFavorites dao) {
            coffeeDaoFavorites = dao;
        }

        @Override
        protected Void doInBackground(final CoffeeFavorites... params) {
            coffeeDaoFavorites.update(params[0]);
            return null;
        }
    }

    void updateCoffee(CoffeeFavorites coffeeFavorites) {
        new updateCoffeeAsyncTask(mCoffeeDaoFavorites).execute(coffeeFavorites);
    }

    private static class deletePlaceAsyncTask extends AsyncTask<CoffeeFavorites, Void, Void> {

        private CoffeeDaoFavorites coffeeDaoFavorites;

        private deletePlaceAsyncTask(CoffeeDaoFavorites dao) {
            coffeeDaoFavorites = dao;
        }

        @Override
        protected Void doInBackground(final CoffeeFavorites... params) {
            coffeeDaoFavorites.deleteNew(params[0]);
            return null;
        }
    }

    void deleteCoffee(CoffeeFavorites coffeeFavorites) {
        new deletePlaceAsyncTask(mCoffeeDaoFavorites).execute(coffeeFavorites);
    }

    private static class insertAsyncTask extends AsyncTask<CoffeeFavorites, Void, Void> {

        private CoffeeDaoFavorites coffeeDaoFavorites;

        private insertAsyncTask(CoffeeDaoFavorites dao) {
            coffeeDaoFavorites = dao;
        }

        @Override
        protected Void doInBackground(final CoffeeFavorites... params) {
            coffeeDaoFavorites.insert(params[0]);
            return null;
        }
    }

    void insertCoffee(CoffeeFavorites coffeeFavorites) {
        new insertAsyncTask(mCoffeeDaoFavorites).execute(coffeeFavorites);
    }

}
