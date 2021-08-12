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
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fooddie.R
import com.example.fooddie.activity.HomeActivity
import com.example.fooddie.activity.RestaurantDetailsActivity
import com.example.fooddie.database.RestaurantEntity
import com.example.fooddie.fragment.FavouritesFragment
import com.example.fooddie.fragment.HomeFragment
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(val context:Context,val restaurantList:List<RestaurantEntity>):
    RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>(){

    class FavouriteViewHolder(view:View): RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView =view.findViewById(R.id.txtRestaurantName2)
        val txtRestaurantRating: TextView =view.findViewById(R.id.txtRestaurantRating2)
        val txtCostForOne: TextView =view.findViewById(R.id.txtPriceForOne2)
        val imgRestaurantImage: ImageView =view.findViewById(R.id.imgRestaurantImage2)
        val imgFavouritesIcon:ImageView=view.findViewById(R.id.imgFavouritesIcon2)
        val allRetaurantsLayout:LinearLayout=view.findViewById(R.id.allRestaurantsLayout2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_favourite_single_row,parent,false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val restaurant=restaurantList[position]

        holder.txtRestaurantName.text=restaurant.restaurantName
        holder.txtCostForOne.text="Rs.${restaurant.restaurantCostForOne}/person"
        holder.txtRestaurantRating.text="⭐${restaurant.restaurantRating}"

        try {
            Picasso.with(context).load(restaurant.restaurantImage).placeholder(R.mipmap.ic_launcher_round)
                .into(holder.imgRestaurantImage)
        } catch (e: Exception) {
        }

        val restaurantEntity=RestaurantEntity(
            restaurant.restaurant_id,
            restaurant.restaurantName,
            restaurant.restaurantRating,
            restaurant.restaurantCostForOne,
            restaurant.restaurantImage
        )
        val checkFav=HomeFragment.DBAsyncTask(context,restaurantEntity,1).execute()
        val isFav=checkFav.get()

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

        holder.allRetaurantsLayout.setOnClickListener {
            var favIcon="dislike"
            if (isFav) {
                favIcon="like"
            }
            val intent= Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra("restaurant_id",restaurant.restaurant_id)
            intent.putExtra("restaurant_name",restaurant.restaurantName)
            intent.putExtra("favIcon",favIcon)
            context.startActivity(intent)
        }
    }
}