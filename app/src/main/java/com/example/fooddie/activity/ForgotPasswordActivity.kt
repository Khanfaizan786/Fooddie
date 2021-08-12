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

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var etForgotPhoneNumber: EditText
    lateinit var etForgotEmail: EditText
    lateinit var btnNext: Button

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        sharedPreferences = getSharedPreferences("preference file", Context.MODE_PRIVATE)

        etForgotPhoneNumber = findViewById(R.id.etForgotPhoneNumber)
        etForgotEmail = findViewById(R.id.etForgotEmail)
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {
            val phoneNumber = etForgotPhoneNumber.text.toString()
            val email = etForgotEmail.text.toString()

            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Please enter phone number",
                    Toast.LENGTH_LONG
                ).show()
            } else if (phoneNumber.length != 10) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Please enter a valid phone number of 10 digits",
                    Toast.LENGTH_LONG
                ).show()
            } else if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@ForgotPasswordActivity, "Please enter Email", Toast.LENGTH_LONG)
                    .show()
            } else {

                val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", phoneNumber)
                jsonParams.put("email", email)

                if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Method.POST, url, jsonParams,
                        Response.Listener {

                            try {
                                val success = it.getJSONObject("data").getBoolean("success")

                                if (success) {

                                    Toast.makeText(
                                        this@ForgotPasswordActivity,
                                        "Check your Email, we had sent an OTP",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val intent = Intent(
                                        this@ForgotPasswordActivity,
                                        ResetPasswordActivity::class.java
                                    )
                                    intent.putExtra("number",phoneNumber)
                                    startActivity(intent)
                                    finish()

                                } else {
                                    Toast.makeText(
                                        this@ForgotPasswordActivity,
                                        "Some error occured!!!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Error occured: $e",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        },
                        Response.ErrorListener {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Volley error occured: $it",
                                Toast.LENGTH_LONG
                            ).show()
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "9bf534118365f1"

                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)
                } else {
                    val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                    dialog.setTitle("Connectivity Error !!")
                    dialog.setMessage("Internet connection is not found")
                    dialog.setPositiveButton("Connect to Internet") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(Activity())
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }
    }
}