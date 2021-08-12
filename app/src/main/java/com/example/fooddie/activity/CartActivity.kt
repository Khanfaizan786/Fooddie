package com.example.fooddie.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fooddie.R
import com.example.fooddie.adapter.CartRecyclerAdapter
import com.example.fooddie.database.FoodEntity
import com.example.fooddie.database.RestaurantDatabase
import com.example.fooddie.database.RestaurantEntity
import com.example.fooddie.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var toolbarCart: Toolbar
    lateinit var txtCartInfo:TextView

    lateinit var recyclerCart: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var btnPlaceOrder:Button
    lateinit var cartRecyclerAdapter:CartRecyclerAdapter

    var priceSum=0

    var dbFoodList= listOf<FoodEntity>()

    lateinit var sharedPreferences: SharedPreferences
    lateinit var resName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerCart=findViewById(R.id.recyclerCart)
        layoutManager= LinearLayoutManager(this@CartActivity)
        toolbarCart=findViewById(R.id.toolbarCart)
        btnPlaceOrder=findViewById(R.id.btnPlaceOrder)
        txtCartInfo=findViewById(R.id.txtCartInfo)

        if (intent!=null) {
            resName=intent.getStringExtra("resName") as String
        }

        setSupportActionBar(toolbarCart)
        supportActionBar?.title="My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        txtCartInfo.text ="Ordering from: $resName"

        dbFoodList=RetrieveFoodItemList(this@CartActivity).execute().get()

        cartRecyclerAdapter= CartRecyclerAdapter(this@CartActivity,dbFoodList)
        recyclerCart.adapter=cartRecyclerAdapter
        recyclerCart.layoutManager=layoutManager

        for (element in dbFoodList) {
            val price= element.food_cost.toInt()
            priceSum += price
        }

        val btnText=priceSum.toString()
        btnPlaceOrder.text="Place Order(Total: Rs.$btnText)"

        sharedPreferences=getSharedPreferences("preference file", Context.MODE_PRIVATE)
        val user_id=sharedPreferences.getString("Id","100")

        btnPlaceOrder.setOnClickListener {
            val queue= Volley.newRequestQueue(this@CartActivity)
            val url="http://13.235.250.119/v2/place_order/fetch_result/"

            val foodJson=JSONArray()
            for (element in dbFoodList) {
                val jsonObject=JSONObject()
                jsonObject.put("food_item_id", element.food_id)
                foodJson.put(jsonObject)
            }
            val jsonParams=JSONObject()
            jsonParams.put("user_id",user_id)
            jsonParams.put("restaurant_id",dbFoodList[0].restaurant_id)
            jsonParams.put("total_cost",priceSum)
            jsonParams.put("food",foodJson)

            if(ConnectionManager().checkConnectivity(this@CartActivity)) {

                val jsonRequest=object: JsonObjectRequest(
                    Request.Method.POST,url,jsonParams,
                    Response.Listener {

                        try {
                            val success = it.getJSONObject("data").getBoolean("success")

                            if (success) {
                                Toast.makeText(this@CartActivity, "Your order has been successfully placed..!!", Toast.LENGTH_LONG).show()

                                val intent= Intent(this@CartActivity,OrderPlacedSplashScreenActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()

                            } else {
                                Toast.makeText(this@CartActivity, "Some error occured!!!", Toast.LENGTH_LONG).show()
                            }
                        }
                        catch (e: JSONException) {
                            Toast.makeText(this@CartActivity, "Error occured:$e", Toast.LENGTH_LONG).show()
                        }

                    },
                    Response.ErrorListener {

                        Toast.makeText(this@CartActivity, "Volley error occured: $it", Toast.LENGTH_LONG).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers=HashMap<String,String>()
                        headers["Content-type"]="application/json"
                        headers["token"]="9bf534118365f1"

                        return headers
                    }
                }
                queue.add(jsonRequest)
            } else {
                val dialog= AlertDialog.Builder(this@CartActivity)
                dialog.setTitle("Connectivity Error !!")
                dialog.setMessage("Internet connection is not found")
                dialog.setPositiveButton("Connect to Internet") {text, listener->
                    val settingsIntent= Intent(Settings.ACTION_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") {text,listener ->
                    ActivityCompat.finishAffinity(Activity())
                }
                dialog.create()
                dialog.show()
            }
        }
    }

    class RetrieveFoodItemList(val context:Context): AsyncTask<Void,Void,List<FoodEntity>>() {

        override fun doInBackground(vararg params: Void?): List<FoodEntity> {
            val db= Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurants-db").build()
            return db.restaurantDao().getAllFoods()
        }
    }
}

