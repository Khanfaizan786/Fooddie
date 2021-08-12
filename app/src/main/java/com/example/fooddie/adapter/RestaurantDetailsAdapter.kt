package com.example.fooddie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddie.R
import com.example.fooddie.activity.RestaurantDetailsActivity
import com.example.fooddie.database.FoodEntity
import com.example.fooddie.model.Food

class RestaurantDetailsAdapter(
    val context: Context,
    val foodList: List<Food>,
    btnProceedToCart: Button
):
    RecyclerView.Adapter<RestaurantDetailsAdapter.FoodViewHolder>() {

    val btnProceed=btnProceedToCart

    class FoodViewHolder(view:View):RecyclerView.ViewHolder(view) {
        val txtSerialNo:TextView=view.findViewById(R.id.txtSerialNo)
        val txtFoodName:TextView=view.findViewById(R.id.txtFoodName)
        val txtFoodPrice:TextView=view.findViewById(R.id.txtFoodPrice)
        val btnAddToCart:Button=view.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.all_food_item_list,parent,false)
        return FoodViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food=foodList[position]
        holder.txtFoodName.text=food.foodName
        holder.txtFoodPrice.text="Rs.${food.foodCost}"
        holder.txtSerialNo.text="${(position+1)}"

        val foodEntity=FoodEntity(
            food.foodId,
            food.foodName,
            food.foodCost,
            food.restaurantId
        )

        val checkAdd= RestaurantDetailsActivity.DBAsyncTaskFood(context,foodEntity,1).execute()
        val isAdded=checkAdd.get()

        if (isAdded) {
            holder.btnAddToCart.setText("Remove").toString()
            holder.btnAddToCart.setBackgroundResource(R.color.colorButton)
        }
        else {
            holder.btnAddToCart.text = "Add"
            holder.btnAddToCart.setBackgroundResource(R.color.colorPrimary)
        }

        holder.btnAddToCart.setOnClickListener {

            if (!RestaurantDetailsActivity.DBAsyncTaskFood(context,foodEntity,1).execute().get()) {
                val async=RestaurantDetailsActivity.DBAsyncTaskFood(context,foodEntity,2).execute()
                val result=async.get()

                if (result) {
                    Toast.makeText(context,"Food Item is added to Cart", Toast.LENGTH_SHORT).show()
                    holder.btnAddToCart.text = "Remove"
                    holder.btnAddToCart.setBackgroundResource(R.color.colorButton)
                    notifyDataSetChanged()
                } else {
                    Toast.makeText(context,"Some error occured",Toast.LENGTH_SHORT).show()
                }
            } else {
                val async=RestaurantDetailsActivity.DBAsyncTaskFood(context,foodEntity,3).execute()
                val result=async.get()

                if (result) {
                    Toast.makeText(context,"Food Item is removed from Cart", Toast.LENGTH_SHORT).show()
                    holder.btnAddToCart.text = "Add"
                    holder.btnAddToCart.setBackgroundResource(R.color.colorPrimary)
                    notifyDataSetChanged()
                } else {
                    Toast.makeText(context,"Some error occured",Toast.LENGTH_SHORT).show()
                }
            }

            val checkLength= RestaurantDetailsActivity.DBAsyncTaskFood(
                context,
                foodEntity,
                4
            ).execute()
            val isEmpty=checkLength.get()

            if (isEmpty) {
                btnProceed.visibility= View.GONE
            }
            else {
                btnProceed.visibility=View.VISIBLE
            }
        }
    }
}