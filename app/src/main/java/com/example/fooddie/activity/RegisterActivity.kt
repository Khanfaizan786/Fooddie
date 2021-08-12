package com.example.fooddie.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fooddie.R
import com.example.fooddie.adapter.HomeRecyclerAdapter
import com.example.fooddie.model.Restaurant
import com.example.fooddie.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    lateinit var mToolbar:Toolbar

    lateinit var etName:EditText
    lateinit var etEmail:EditText
    lateinit var etMobileNunber:EditText
    lateinit var etDeliveryAddress:EditText
    lateinit var etRegisterPassword:EditText
    lateinit var etConfirmPassword:EditText

    lateinit var btnRegister:Button

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences=getSharedPreferences("preference file", Context.MODE_PRIVATE)

        mToolbar = findViewById(R.id.appBar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "Register Yourself"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        etName = findViewById(R.id.etName)
        etEmail= findViewById(R.id.etEmail)
        etMobileNunber=findViewById(R.id.etMobileNumber)
        etDeliveryAddress=findViewById(R.id.etDeliveryAddress)
        etRegisterPassword=findViewById(R.id.etRegisterPassword)
        etConfirmPassword=findViewById(R.id.etConfirmPassword)

        btnRegister=findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name=etName.text.toString()
            val email=etEmail.text.toString()
            val mobileNumber=etMobileNunber.text.toString()
            val deliveryAddress=etDeliveryAddress.text.toString()
            val registerPassword=etRegisterPassword.text.toString()
            val confirmPassword=etConfirmPassword.text.toString()

            if (TextUtils.isEmpty(name))
            {
                Toast.makeText(this@RegisterActivity,"Please fill complete credentials",Toast.LENGTH_LONG).show()
            }
            else if (TextUtils.isEmpty(email))
            {
                Toast.makeText(this@RegisterActivity,"Please fill complete credentials",Toast.LENGTH_LONG).show()
            }
            else if (TextUtils.isEmpty(mobileNumber))
            {
                Toast.makeText(this@RegisterActivity,"Please fill complete credentials",Toast.LENGTH_LONG).show()
            }
            else if (TextUtils.isEmpty(deliveryAddress))
            {
                Toast.makeText(this@RegisterActivity,"Please fill complete credentials",Toast.LENGTH_LONG).show()
            }
            else if (TextUtils.isEmpty(registerPassword))
            {
                Toast.makeText(this@RegisterActivity,"Please create a new password",Toast.LENGTH_LONG).show()
            }
            else if (TextUtils.isEmpty(confirmPassword))
            {
                Toast.makeText(this@RegisterActivity,"Please confirm new password",Toast.LENGTH_LONG).show()
            }
            else if (registerPassword != confirmPassword)
            {
                Toast.makeText(this@RegisterActivity,"Password mismatch, please confirm again",Toast.LENGTH_LONG).show()
            }
            else if (registerPassword.length<6){
                Toast.makeText(this@RegisterActivity,"Length of password should be at least 6 characters",Toast.LENGTH_LONG).show()
            }
            else if (mobileNumber.length!=10)
            {
                Toast.makeText(this@RegisterActivity,"Please enter a valid phone number of 10 digits",Toast.LENGTH_LONG).show()
            }
            else {
                val queue=Volley.newRequestQueue(this@RegisterActivity)
                val url="http://13.235.250.119/v2/register/fetch_result"

                val jsonParams=JSONObject()
                jsonParams.put("name",name)
                jsonParams.put("mobile_number",mobileNumber)
                jsonParams.put("password",registerPassword)
                jsonParams.put("address",deliveryAddress)
                jsonParams.put("email",email)

                if(ConnectionManager().checkConnectivity(this@RegisterActivity)) {

                    val jsonRequest=object: JsonObjectRequest(Request.Method.POST,url,jsonParams,Response.Listener {

                        try {
                            val success = it.getJSONObject("data").getBoolean("success")

                            if (success) {
                                val data = it.getJSONObject("data").getJSONObject("data")

                                val userId=data.getString("user_id")
                                val userName=data.getString("name")
                                val userEmail=data.getString("email")
                                val userPhone=data.getString("mobile_number")
                                val userAddress=data.getString("address")

                                sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
                                sharedPreferences.edit().putString("Id",userId).apply()
                                sharedPreferences.edit().putString("Name",userName).apply()
                                sharedPreferences.edit().putString("Email",userEmail).apply()
                                sharedPreferences.edit().putString("Phone",userPhone).apply()
                                sharedPreferences.edit().putString("Address",userAddress).apply()

                                Toast.makeText(this@RegisterActivity, "You are registered successfully!!", Toast.LENGTH_LONG).show()

                                val intent=Intent(this@RegisterActivity,HomeActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()

                            } else {
                                Toast.makeText(this@RegisterActivity, "Some error occured!!!", Toast.LENGTH_LONG).show()
                            }
                        }
                        catch (e: JSONException) {
                            Toast.makeText(this@RegisterActivity, "Error occured:$e", Toast.LENGTH_LONG).show()
                        }

                    },Response.ErrorListener {

                        Toast.makeText(this@RegisterActivity, "Volley error occured: $it", Toast.LENGTH_LONG).show()
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
                    val dialog= AlertDialog.Builder(this@RegisterActivity)
                    dialog.setTitle("Connectivity Error !!")
                    dialog.setMessage("Internet connection is not found")
                    dialog.setPositiveButton("Connect to Internet") {text, listener->
                        val settingsIntent=Intent(Settings.ACTION_SETTINGS)
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
    }
}
