package com.example.fooddie.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.fooddie.R
import com.example.fooddie.adapter.FavouriteRecyclerAdapter
import com.example.fooddie.database.RestaurantDatabase
import com.example.fooddie.database.RestaurantEntity

/**
 * A simple [Fragment] subclass.
 */
class FavouritesFragment : Fragment() {

    lateinit var recyclerFavourites:RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var progressLayoutFav: RelativeLayout
    lateinit var progressBarFav: ProgressBar

    lateinit var recyclerAdapter:FavouriteRecyclerAdapter
    var dbRestaurantList= listOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_favourites, container, false)

        progressBarFav=view.findViewById(R.id.progressBarFav)
        progressLayoutFav=view.findViewById(R.id.progressLayoutFav)

        recyclerFavourites=view.findViewById(R.id.recyclerFavourites)
        layoutManager= LinearLayoutManager(activity as Context)

        progressLayoutFav.visibility=View.VISIBLE

        dbRestaurantList=RetrieveFavourites(activity as Context).execute().get()

        if (activity!=null) {
            progressLayoutFav.visibility=View.GONE
            recyclerAdapter= FavouriteRecyclerAdapter(activity as Context,dbRestaurantList)
            recyclerFavourites.adapter=recyclerAdapter
            recyclerFavourites.layoutManager=layoutManager
        }
        else
        {
            Toast.makeText(activity,"Activity is null",Toast.LENGTH_LONG).show()
        }

        return view
    }

    class RetrieveFavourites(val context:Context): AsyncTask<Void,Void,List<RestaurantEntity>>(){
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db=Room.databaseBuilder(context,RestaurantDatabase::class.java,"restaurants-db").build()
            return db.restaurantDao().getAllRestaurants()
        }

    }

    override fun onResume() {
        recyclerAdapter.notifyDataSetChanged()
        super.onResume()
    }

}
