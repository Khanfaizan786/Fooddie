package com.example.fooddie.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fooddie.R
import com.example.fooddie.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var etPhoneNumber:EditText
    lateinit var etPassword:EditText
    lateinit var btnLogin:Button
    lateinit var txtForgotPassword:TextView
    lateinit var txtCreateAccount:TextView

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences=getSharedPreferences("preference file", Context.MODE_PRIVATE)

        val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)

        etPhoneNumber=findViewById(R.id.etPhoneNumber)
        etPassword=findViewById(R.id.etPassword)
        btnLogin=findViewById(R.id.btnLogin)
        txtForgotPassword=findViewById(R.id.txtForgotPassword)
        txtCreateAccount=findViewById(R.id.txtCreateAccount)

        if (isLoggedIn) {
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {

            /*val phoneNumber=etPhoneNumber.text.toString()
            val password=etPassword.text.toString()

            if (TextUtils.isEmpty(phoneNumber))
            {
                Toast.makeText(this@MainActivity,"Please enter Phone number",Toast.LENGTH_LONG).show()
            }
            else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this@MainActivity,"Please enter Password",Toast.LENGTH_LONG).show()
            }
            else if (phoneNumber.length!=10)
            {
                Toast.makeText(this@MainActivity,"Please enter a valid phone number of 10 digits",Toast.LENGTH_LONG).show()
            }

            else {

                val queue= Volley.newRequestQueue(this@MainActivity)
                val url="http://13.235.250.119/v2/login/fetch_result"

                val jsonParams= JSONObject()
                jsonParams.put("mobile_number",phoneNumber)
                jsonParams.put("password",password)

                if(ConnectionManager().checkConnectivity(this@MainActivity)) {

                    val jsonObjectRequest=object: JsonObjectRequest(Request.Method.POST,url,jsonParams,Response.Listener {

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

                                Toast.makeText(this@MainActivity, "You are logged In successfully!!", Toast.LENGTH_LONG).show()*/

                                val intent=Intent(this@MainActivity,HomeActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()

                            /*} else {
                                Toast.makeText(this@MainActivity, "Some error occured!!!", Toast.LENGTH_LONG).show()
                            }
                        }
                        catch (e: JSONException) {
                            Toast.makeText(this@MainActivity, "Error occured: $e", Toast.LENGTH_LONG).show()
                        }

                    },Response.ErrorListener {
                        Toast.makeText(this@MainActivity, "Volley error occured: $it", Toast.LENGTH_LONG).show()
                    }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers=HashMap<String,String>()
                            headers["Content-type"]="application/json"
                            headers["token"]="9bf534118365f1"

                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)
                } else {
                    val dialog= AlertDialog.Builder(this@MainActivity)
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
            }*/
        }

        txtForgotPassword.setOnClickListener {
            val intent = Intent(this@MainActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        txtCreateAccount.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
