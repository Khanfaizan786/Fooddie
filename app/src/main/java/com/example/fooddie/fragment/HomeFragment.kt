package com.example.fooddie.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.webkit.DateSorter
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Database
import androidx.room.Room
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fooddie.R
import com.example.fooddie.adapter.HomeRecyclerAdapter
import com.example.fooddie.database.RestaurantDatabase
import com.example.fooddie.database.RestaurantEntity
import com.example.fooddie.model.Restaurant
import com.example.fooddie.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var homeRecyclerAdapter: HomeRecyclerAdapter

    var restaurantListInfo= arrayListOf<Restaurant>()
    
    var ratingComparator=Comparator<Restaurant>{restaurant1,restaurant2 ->
        restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating,true)
    }

    var costComparator=Comparator<Restaurant>{restaurant1,restaurant2 ->
        restaurant1.restaurantCostForOne.compareTo(restaurant2.restaurantCostForOne,true)
    }

    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        recyclerHome=view.findViewById(R.id.recyclerHome)
        layoutManager= LinearLayoutManager(activity)

        progressBar=view.findViewById(R.id.progressBar)
        progressLayout=view.findViewById(R.id.progressLayout)

        progressLayout.visibility=View.VISIBLE

        val queue:RequestQueue = Volley.newRequestQueue(activity as Context)
        val url:String = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if(ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {

                    try {
                        progressLayout.visibility=View.GONE
                        val success = it.getJSONObject("data").getBoolean("success")

                        if (success) {
                            val data = it.getJSONObject("data").getJSONArray("data")

                            for (i in 0 until data.length()) {
                                val restaurantJsonObject = data.getJSONObject(i)
                                val resId=restaurantJsonObject.getString("id")
                                val resName = restaurantJsonObject.getString("name")
                                val resRating=restaurantJsonObject.getString("rating")
                                val resCost=restaurantJsonObject.getString("cost_for_one")
                                val resImage=restaurantJsonObject.getString("image_url")
                                val restaurantObject = Restaurant(
                                    resId,
                                    resName,
                                    resRating,
                                    resCost,
                                    resImage
                                )
                                restaurantListInfo.add(restaurantObject)
                                homeRecyclerAdapter =
                                    HomeRecyclerAdapter(activity as Context, restaurantListInfo)
                                recyclerHome.adapter = homeRecyclerAdapter
                                recyclerHome.layoutManager = layoutManager

                            }
                        } else {
                            Toast.makeText(activity, "Some error occured!!!", Toast.LENGTH_LONG).show()
                        }
                    }
                    catch (e:JSONException) {
                        Toast.makeText(activity, "Error occured:$e", Toast.LENGTH_LONG).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(activity, "Volley error occured!!!", Toast.LENGTH_LONG).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "9bf534118365f1"
                        return headers
                    }
                }

            queue.add(jsonObjectRequest)
        }

        else {
            val dialog= AlertDialog.Builder(activity)
            dialog.setTitle("Connectivity Error !!")
            dialog.setMessage("Internet connection is not found")
            dialog.setPositiveButton("Connect to Internet") {text, listener->
                val settingsIntent=Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") {text,listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view
    }

    class DBAsyncTask(val context: Context,val restaurantEntity: RestaurantEntity,val mode:Int): AsyncTask<Void,Void,Boolean>() {

        val db=Room.databaseBuilder(context,RestaurantDatabase::class.java,"restaurants-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {

                1 -> {
                    val restaurant: RestaurantEntity?=db.restaurantDao().getRestaurantById(restaurantEntity.restaurant_id.toString())
                    db.close()
                    return restaurant!=null
                }

                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }

            return false
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var checkedItem:Int=-1
        val id= item.itemId
        if (id==(R.id.action_sort)) {
            val builder=AlertDialog.Builder(activity as Context)
            builder.setTitle("Sort by?")
            builder.setSingleChoiceItems(R.array.filters,checkedItem){ _, isChecked ->
                checkedItem=isChecked
            }
            builder.setPositiveButton("Ok"){_, _ ->
                when(checkedItem) {
                    0 -> {
                        Collections.sort(restaurantListInfo,costComparator)
                    }

                    1 -> {
                        Collections.sort(restaurantListInfo,costComparator)
                        restaurantListInfo.reverse()
                    }

                    2 -> {
                        Collections.sort(restaurantListInfo,ratingComparator)
                        restaurantListInfo.reverse()
                    }
                }
                homeRecyclerAdapter.notifyDataSetChanged()
            }
            builder.setNegativeButton("Cancel") { _, _ ->

            }
            builder.create()
            builder.show()
        }

        return super.onOptionsItemSelected(item)
    }
}
