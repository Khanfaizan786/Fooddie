package com.example.fooddie.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey val food_id:String,
    @ColumnInfo(name="food_name") val food_name:String,
    @ColumnInfo(name="food_cost") val food_cost:String,
    @ColumnInfo(name="restaurant_id") val restaurant_id: String
)