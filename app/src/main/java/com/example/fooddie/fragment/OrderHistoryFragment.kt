package com.example.fooddie.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.example.fooddie.R
import com.example.fooddie.adapter.OrderHistoryAdapter
import com.example.fooddie.model.OrderDetails
import com.example.fooddie.util.ConnectionManager

/**
 * A simple [Fragment] subclass.
 */
class OrderHistoryFragment : Fragment() {

    lateinit var llHasOrders:LinearLayout
    lateinit var recyclerOrderHistory:RecyclerView
    lateinit var rlLoading:RelativeLayout
    lateinit var orderHistoryAdapter: OrderHistoryAdapter
    var orderHistoryList= arrayListOf<OrderDetails>()
    lateinit var sharedPreferences:SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_order_history, container, false)

        llHasOrders=view.findViewById(R.id.llHasOrders)
        recyclerOrderHistory=view.findViewById(R.id.recyclerOrderHistory)
        rlLoading=view.findViewById(R.id.rlLoading)
        sharedPreferences=activity?.getSharedPreferences("preference file",Context.MODE_PRIVATE)!!

        val user_id=sharedPreferences.getString("Id","100") as String

        if(ConnectionManager().checkConnectivity(activity as Context)) {
            sendServerRequest(user_id)
        }
        else {
            val dialog= AlertDialog.Builder(activity as Context)
            dialog.setTitle("Connectivity Error !!")
            dialog.setMessage("Internet connection is not found")
            dialog.setPositiveButton("Connect to Internet") {text, listener->
                val settingsIntent= Intent(Settings.ACTION_SETTINGS)
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

    private fun sendServerRequest(userId: String) {
        val queue = Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v2/orders/fetch_result/$userId"

        val jsonObjectRequest = object :
            JsonObjectRequest(Method.GET, url, null, Response.Listener {
                rlLoading.visibility = View.GONE
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val resArray = data.getJSONArray("data")
                        if (resArray.length() == 0) {
                            llHasOrders.visibility = View.GONE
                        } else {
                            for (i in 0 until resArray.length()) {
                                val orderObject = resArray.getJSONObject(i)
                                val foodItems = orderObject.getJSONArray("food_items")
                                val orderDetails = OrderDetails(
                                    orderObject.getInt("order_id"),
                                    orderObject.getString("restaurant_name"),
                                    orderObject.getString("order_placed_at"),
                                    foodItems
                                )
                                orderHistoryList.add(orderDetails)
                                if (orderHistoryList.isEmpty()) {
                                    llHasOrders.visibility = View.GONE

                                } else {
                                    llHasOrders.visibility = View.VISIBLE

                                    if (activity != null) {
                                        orderHistoryAdapter = OrderHistoryAdapter(
                                            activity as Context,
                                            orderHistoryList
                                        )
                                        val mLayoutManager =
                                            LinearLayoutManager(activity as Context)
                                        recyclerOrderHistory.layoutManager = mLayoutManager
                                        recyclerOrderHistory.itemAnimator = DefaultItemAnimator()
                                        recyclerOrderHistory.adapter = orderHistoryAdapter
                                    } else {
                                        queue.cancelAll(this::class.java.simpleName)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener {
                Toast.makeText(activity as Context, it.message, Toast.LENGTH_SHORT).show()
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
}
