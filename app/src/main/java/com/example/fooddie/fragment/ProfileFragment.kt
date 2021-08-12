package com.example.fooddie.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.fooddie.R
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    lateinit var imgProfileImage:CircleImageView
    lateinit var txtProfileName:TextView
    lateinit var txtProfilePhone:TextView
    lateinit var txtProfileEmail:TextView
    lateinit var txtProfileAddress:TextView

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPreferences= activity?.getSharedPreferences("preference file", Context.MODE_PRIVATE)!!

        imgProfileImage =view.findViewById(R.id.imgProfileImage)
        txtProfileName=view.findViewById(R.id.txtProfileName)
        txtProfilePhone=view.findViewById(R.id.txtProfilePhone)
        txtProfileEmail=view.findViewById(R.id.txtProfileEmail)
        txtProfileAddress=view.findViewById(R.id.txtProfileAddress)

        val profileName=sharedPreferences.getString("Name","No Name")
        val profilePhone=sharedPreferences.getString("Phone","No Phone Number")
        val profileEmail=sharedPreferences.getString("Email","No Email")
        val profileAddress=sharedPreferences.getString("Address","No Delivery Address")

        txtProfileName.text=profileName
        txtProfilePhone.text=profilePhone
        txtProfileEmail.text=profileEmail
        txtProfileAddress.text=profileAddress

        return view
    }

}
