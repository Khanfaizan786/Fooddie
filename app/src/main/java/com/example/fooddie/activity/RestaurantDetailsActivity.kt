package com.example.fooddie.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.View.GONE
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fooddie.R
import com.example.fooddie.adapter.RestaurantDetailsAdapter
import com.example.fooddie.database.FoodEntity
import com.example.fooddie.database.RestaurantDatabase
import com.example.fooddie.model.Food
import com.example.fooddie.util.ConnectionManager
import org.json.JSONException

class RestaurantDetailsActivity : AppCompatActivity() {

    lateinit var toolbarRestaurantDetails: Toolbar

    lateinit var recyclerRestaurantDetails: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    lateinit var btnProceedToCart:Button
    lateinit var imgFavouritesIcon:ImageView

    lateinit var restaurantDetailsAdapter: RestaurantDetailsAdapter

    var restaurant_id:String="100"
    var restaurant_name:String="Restaurant Details"
    var favIcon:String="dislike"

    var foodInfoList= arrayListOf<Food>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)

        if (intent!=null) {
            restaurant_id=intent.getStringExtra("restaurant_id") as String
            restaurant_name=intent.getStringExtra("restaurant_name") as String
            favIcon=intent.getStringExtra("favIcon") as String
        }

        recyclerRestaurantDetails=findViewById(R.id.recyclerRestaurantDetails)
        layoutManager=LinearLayoutManager(this@RestaurantDetailsActivity)
        toolbarRestaurantDetails=findViewById(R.id.toolbarRestaurantDetails)
        progressBar=findViewById(R.id.progressBarDetails)
        progressLayout=findViewById(R.id.progressLayoutDetails)
        btnProceedToCart=findViewById(R.id.btnProceedToCart)
        imgFavouritesIcon=findViewById(R.id.imgFavouritesIcon3)

        setSupportActionBar(toolbarRestaurantDetails)
        supportActionBar?.title=restaurant_name
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (favIcon=="like") {
            imgFavouritesIcon.setImageResource(R.drawable.like)
        }
        //btnProceedToCart.visibility=View.GONE
        progressLayout.visibility=View.VISIBLE

        val queue = Volley.newRequestQueue(this@RestaurantDetailsActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurant_id"

        if(ConnectionManager().checkConnectivity(this@RestaurantDetailsActivity)) {

            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {

                    try {
                        progressLayout.visibility= GONE
                        val success = it.getJSONObject("data").getBoolean("success")

                        if (success) {
                            val data = it.getJSONObject("data").getJSONArray("data")

                            for (i in 0 until data.length()) {
                                val restaurantJsonObject = data.getJSONObject(i)
                                val foodId=restaurantJsonObject.getString("id")
                                val foodName = restaurantJsonObject.getString("name")
                                val foodCost=restaurantJsonObject.getString("cost_for_one")
                                val restaurant_id=restaurantJsonObject.getString("restaurant_id")
                                val foodObject=Food(
                                    foodId,foodName,foodCost,restaurant_id
                                )
                                foodInfoList.add(foodObject)
                                restaurantDetailsAdapter=RestaurantDetailsAdapter(this@RestaurantDetailsActivity,foodInfoList,btnProceedToCart)
                                recyclerRestaurantDetails.adapter=restaurantDetailsAdapter
                                recyclerRestaurantDetails.layoutManager=layoutManager
                                restaurantDetailsAdapter.notifyDataSetChanged()

                                val foodEntity=FoodEntity(
                                    foodId,foodName,foodCost,restaurant_id
                                )

                                val checkLength= DBAsyncTaskFood(this@RestaurantDetailsActivity,foodEntity,4).execute()
                                val isEmpty=checkLength.get()

                                if (isEmpty) {
                                    btnProceedToCart.visibility= GONE
                                }
                                else {
                                    btnProceedToCart.visibility=View.VISIBLE
                                }
                            }
                        } else {
                            Toast.makeText(this@RestaurantDetailsActivity, "Some error occured!!!", Toast.LENGTH_LONG).show()
                        }
                    }
                    catch (e: JSONException) {
                        Toast.makeText(this@RestaurantDetailsActivity, "Error occured:$e", Toast.LENGTH_LONG).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(this@RestaurantDetailsActivity, "Volley error occured!!!", Toast.LENGTH_LONG).show()
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
            val dialog= AlertDialog.Builder(this@RestaurantDetailsActivity)
            dialog.setTitle("Connectivity Error !!")
            dialog.setMessage("Internet connection is not found")
            dialog.setPositiveButton("Connect to Internet") {text, listener->
                val settingsIntent= Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") {text,listener ->
                ActivityCompat.finishAffinity(this@RestaurantDetailsActivity)
            }
            dialog.create()
            dialog.show()
        }

        btnProceedToCart.setOnClickListener {
            val intent=Intent(this@RestaurantDetailsActivity,CartActivity::class.java)
            intent.putExtra("resName",restaurant_name)
            startActivity(intent)
            finish()
        }
    }


    class DBAsyncTaskFood(val context: Context,val foodEntity: FoodEntity,val mode:Int): AsyncTask<Void,Void,Boolean>() {
        val db= Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurants-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
                    val food: FoodEntity?=db.restaurantDao().getFoodById(foodEntity.food_id.toString())
                    db.close()
                    return food!=null
                }

                2 -> {
                    db.restaurantDao().insertFood(foodEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.restaurantDao().deleteFood(foodEntity)
                    db.close()
                    return true
                }

                4 -> {
                    val allFoods=db.restaurantDao().getAllFoods()
                    db.close()
                    return allFoods.isEmpty()
                }

                5 -> {
                    db.restaurantDao().deleteAllFoodItems()
                    db.close()
                    return true
                }
            }

            return false
        }

    }

    override fun onBackPressed() {
        DeleteFoodList(this@RestaurantDetailsActivity).execute().get()
        super.onBackPressed()
    }

    override fun onStop() {
        DeleteFoodList(this@RestaurantDetailsActivity).execute().get()
        super.onStop()
    }

    class DeleteFoodList(val context:Context): AsyncTask<Void,Void,Boolean>(){
        override fun doInBackground(vararg params: Void?):Boolean {
            val db=Room.databaseBuilder(context,RestaurantDatabase::class.java,"restaurants-db").build()
            db.restaurantDao().deleteAllFoodItems()
            return true
        }

    }
}
