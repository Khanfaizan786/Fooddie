package com.example.fooddie.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddie.R
import com.example.fooddie.activity.RestaurantDetailsActivity
import com.example.fooddie.database.RestaurantEntity
import com.example.fooddie.fragment.HomeFragment
import com.example.fooddie.model.Restaurant
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class HomeRecyclerAdapter(val context:Context,val itemList:ArrayList<Restaurant>):
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val txtRestaurantName:TextView=view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantRating:TextView=view.findViewById(R.id.txtRestaurantRating)
        val txtCostForOne:TextView=view.findViewById(R.id.txtPriceForOne)
        val imgRestaurantImage:ImageView=view.findViewById(R.id.imgRestaurantImage)
        val allRestaurantsLayout:LinearLayout=view.findViewById(R.id.allRestaurantsLayout)
        val imgFavouritesIcon:ImageView=view.findViewById(R.id.imgFavouritesIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row,parent,false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant=itemList[position]

        holder.txtRestaurantName.text=restaurant.restaurantName
        holder.txtCostForOne.text="Rs.${restaurant.restaurantCostForOne}/person"
        holder.txtRestaurantRating.text="⭐${restaurant.restaurantRating}"

        try {
            Picasso.with(context).load(restaurant.restaurantImage).placeholder(R.mipmap.ic_launcher_round)
                .into(holder.imgRestaurantImage)
        } catch (e: Exception) {
        }

        val restaurantEntity=RestaurantEntity(
            restaurant.restaurantId,
            restaurant.restaurantName,
            restaurant.restaurantRating,
            restaurant.restaurantCostForOne,
            restaurant.restaurantImage
        )
        val checkFav=HomeFragment.DBAsyncTask(context,restaurantEntity,1).execute()
        val isFav=checkFav.get()

        if (isFav) {
            holder.imgFavouritesIcon.setImageResource(R.drawable.like)
        } else {
            holder.imgFavouritesIcon.setImageResource(R.drawable.dislike)
        }

        holder.allRestaurantsLayout.setOnClickListener {
            var favIcon="dislike"
            if (isFav) {
                favIcon="like"
            }
            val intent= Intent(context,RestaurantDetailsActivity::class.java)
            intent.putExtra("restaurant_id",restaurant.restaurantId)
            intent.putExtra("restaurant_name",restaurant.restaurantName)
            intent.putExtra("favIcon",favIcon)
            context.startActivity(intent)
        }

        holder.imgFavouritesIcon.setOnClickListener {

            if (!HomeFragment.DBAsyncTask(context,restaurantEntity,1).execute().get()) {
                val async=HomeFragment.DBAsyncTask(context,restaurantEntity,2).execute()
                val result=async.get()

                if (result) {
                    Toast.makeText(context,"Restaurant is added to favourites",Toast.LENGTH_SHORT).show()
                    holder.imgFavouritesIcon.setImageResource(R.drawable.like)
                } else {
                    Toast.makeText(context,"Some error occured",Toast.LENGTH_SHORT).show()
                }
            } else {
                val async=HomeFragment.DBAsyncTask(context,restaurantEntity,3).execute()
                val result=async.get()

                if (result) {
                    Toast.makeText(context,"Restaurant is removed from favourites",Toast.LENGTH_SHORT).show()
                    holder.imgFavouritesIcon.setImageResource(R.drawable.dislike)
                } else {
                    Toast.makeText(context,"Some error occured",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}