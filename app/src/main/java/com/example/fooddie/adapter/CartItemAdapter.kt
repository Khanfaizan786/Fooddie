package com.example.fooddie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddie.R
import com.example.fooddie.model.FoodItem
import kotlinx.android.synthetic.main.order_history_single_row.view.*

class CartItemAdapter(val foodItemsList: ArrayList<FoodItem>,val context: Context):
    RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    class CartItemViewHolder(view:View): RecyclerView.ViewHolder(view) {
        val txtResHistoryFoodName:TextView=view.findViewById(R.id.txtResHistoryFoodName)
        val txtResHistoryPrice:TextView=view.findViewById(R.id.txtResHistoryPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_history_single_row, parent, false)
        return CartItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodItemsList.size
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val item=foodItemsList[position]
        holder.txtResHistoryFoodName.text=item.foodName
        holder.txtResHistoryPrice.text="Rs.${item.foodCost.toString()}"
    }
}