package com.example.fooddie.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.nio.channels.SelectableChannel

@Dao
interface RestaurantDao {

    @Insert
    fun insertRestaurant(restaurantEntity: RestaurantEntity)

    @Delete
    fun deleteRestaurant(restaurantEntity: RestaurantEntity)

    @Query("SELECT * FROM restaurants")
    fun getAllRestaurants(): List<RestaurantEntity>

    @Query("SELECT * FROM restaurants WHERE restaurant_id=:restaurantId")
    fun getRestaurantById(restaurantId:String):RestaurantEntity

    @Insert
    fun insertFood(foodEntity: FoodEntity)

    @Delete
    fun deleteFood(foodEntity: FoodEntity)

    @Query("SELECT * FROM foods")
    fun getAllFoods():List<FoodEntity>

    @Query("SELECT * FROM foods WHERE food_id=:foodId")
    fun getFoodById(foodId:String):FoodEntity

    @Query("DELETE FROM foods")
    fun deleteAllFoodItems()
}