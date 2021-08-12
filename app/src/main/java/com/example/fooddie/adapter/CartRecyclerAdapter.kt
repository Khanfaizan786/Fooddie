package com.example.fooddie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddie.R
import com.example.fooddie.database.FoodEntity
import kotlinx.android.synthetic.main.cart_food_item_list.view.*

class CartRecyclerAdapter(val context: Context, val foodItemList:List<FoodEntity>): RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {
    class CartViewHolder(view:View):RecyclerView.ViewHolder(view) {
        val txtFoodItemName:TextView=view.findViewById(R.id.txtFoodItemName)
        val txtFoodItemPrice:TextView=view.findViewById(R.id.txtFoodItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.cart_food_item_list,parent,false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodItemList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val food=foodItemList[position]
        holder.txtFoodItemName.text=food.food_name
        holder.txtFoodItemPrice.text=food.food_cost
    }
}