package com.example.mycoffeeshop.RoomFavoritesPackage;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "coffee_table_favorites", indices = @Index(value = {"title"}, unique = true))
public class CoffeeFavorites implements Serializable {

    public CoffeeFavorites(@NonNull String title, @NonNull String description, int price, int weight_unit, String img) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.weight_unit = weight_unit;
        this.img = img;
    }

    public CoffeeFavorites() {

    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private long ID;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "price")
    private int price;

    @ColumnInfo(name = "weight_unit")
    private int weight_unit;

    @ColumnInfo(name = "img")
    private String img;


    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getWeight_unit() {
        return weight_unit;
    }

    public void setWeight_unit(int weight_unit) {
        this.weight_unit = weight_unit;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

}
