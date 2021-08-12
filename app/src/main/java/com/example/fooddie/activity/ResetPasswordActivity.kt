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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fooddie.R
import com.example.fooddie.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var etResetOtp:EditText
    lateinit var etResetPassword:EditText
    lateinit var etConfirmResetPassword:EditText
    lateinit var btnResetPassword:Button

    lateinit var phoneNumber:String

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        sharedPreferences = getSharedPreferences("preference file", Context.MODE_PRIVATE)

        etResetOtp=findViewById(R.id.etResetOtp)
        etResetPassword=findViewById(R.id.etResetPassword)
        etConfirmResetPassword=findViewById(R.id.etConfirmResetPassword)
        btnResetPassword=findViewById(R.id.btnResetPassword)

        if (intent!=null) {
            phoneNumber=intent.getStringExtra("number") as String
        }

        btnResetPassword.setOnClickListener {

            val otp=etResetOtp.text.toString()
            val resetPassword = etResetPassword.text.toString()
            val confirmResetPassword = etConfirmResetPassword.text.toString()

            if (TextUtils.isEmpty(otp)) {
                Toast.makeText(this@ResetPasswordActivity,"Please enter OTP", Toast.LENGTH_LONG).show()
            } else if (otp.length!=4) {
                Toast.makeText(this@ResetPasswordActivity,"Please enter a valid OTP of 4-digits", Toast.LENGTH_LONG).show()
            } else if (TextUtils.isEmpty(resetPassword)) {
                Toast.makeText(this@ResetPasswordActivity,"Please create a new password",Toast.LENGTH_LONG).show()
            } else if (TextUtils.isEmpty(confirmResetPassword)) {
                Toast.makeText(this@ResetPasswordActivity,"Please confirm new password",Toast.LENGTH_LONG).show()
            } else if (resetPassword != confirmResetPassword) {
                Toast.makeText(this@ResetPasswordActivity,"Password mismatch, please confirm again",Toast.LENGTH_LONG).show()
            } else if (resetPassword.length<6){
                Toast.makeText(this@ResetPasswordActivity,"Length of password should be at least 6 characters",Toast.LENGTH_LONG).show()
            } else {

                val queue= Volley.newRequestQueue(this@ResetPasswordActivity)
                val url="http://13.235.250.119/v2/reset_password/fetch_result"

                val jsonParams= JSONObject()
                jsonParams.put("mobile_number",phoneNumber)
                jsonParams.put("password",resetPassword)
                jsonParams.put("otp",otp)

                if(ConnectionManager().checkConnectivity(this@ResetPasswordActivity)) {

                    val jsonObjectRequest=object: JsonObjectRequest(
                        Method.POST,url,jsonParams,
                        Response.Listener {

                            println("Response is $it")

                            try {
                                val success = it.getJSONObject("data").getBoolean("success")

                                if (success) {
                                    val msg=it.getJSONObject("data").getString("successMessage")

                                    sharedPreferences.all.clear()
                                    Toast.makeText(this@ResetPasswordActivity, msg, Toast.LENGTH_LONG).show()

                                    val intent= Intent(this@ResetPasswordActivity,MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(intent)
                                    finish()

                                } else {
                                    Toast.makeText(this@ResetPasswordActivity, "Some error occured!!!", Toast.LENGTH_LONG).show()
                                }
                            }
                            catch (e: JSONException) {
                                Toast.makeText(this@ResetPasswordActivity, "Error occured: $e", Toast.LENGTH_LONG).show()
                            }

                        },
                        Response.ErrorListener {
                            Toast.makeText(this@ResetPasswordActivity, "Volley error occured: $it", Toast.LENGTH_LONG).show()
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
                    val dialog= AlertDialog.Builder(this@ResetPasswordActivity)
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
    }
}
