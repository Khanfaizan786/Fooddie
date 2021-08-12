package com.example.fooddie.activity


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.fooddie.R
import com.example.fooddie.fragment.*
import com.example.fooddie.util.ConnectionManager
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView

class HomeActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    private lateinit var navigationView: NavigationView
    lateinit var txtUserNameHeader:TextView

    var previousMenuItem: MenuItem?=null

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferences=getSharedPreferences("preference file", Context.MODE_PRIVATE)
        val userProfileName=sharedPreferences.getString("Name","No name") as String

        drawerLayout=findViewById(R.id.drawerLayout)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.frame)
        navigationView=findViewById(R.id.navigation_view)

        val convertView=LayoutInflater.from(this@HomeActivity).inflate(R.layout.drawer_header,null)

        txtUserNameHeader =convertView.findViewById(R.id.txtUserNameHeader)

        txtUserNameHeader.text=userProfileName
        navigationView.addHeaderView(convertView)

        setUpToolbar()

        openHomePage()

        val actionBarDrawerToggle= ActionBarDrawerToggle(this@HomeActivity,drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem!=null) {
                previousMenuItem?.isChecked=false
            }

            it.isCheckable=true
            it.isChecked=true

            previousMenuItem=it

            when (it.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            HomeFragment()
                        ).commit()
                    supportActionBar?.title="Home"
                    drawerLayout.closeDrawers()
                }

                R.id.favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FavouritesFragment()
                        ).commit()
                    supportActionBar?.title="Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }

                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            ProfileFragment()
                        ).commit()
                    supportActionBar?.title=" My Profile"
                    drawerLayout.closeDrawers()
                }

                R.id.history -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            OrderHistoryFragment()
                        ).commit()
                    supportActionBar?.title=" My Order History"
                    drawerLayout.closeDrawers()
                }

                R.id.faqs -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FaqsFragment()
                        )
                        .commit()
                    supportActionBar?.title="FAQs"
                    drawerLayout.closeDrawers()
                }

                R.id.logout -> {
                    val dialog= AlertDialog.Builder(this@HomeActivity)
                    dialog.setTitle("Alert !!")
                    dialog.setMessage("Are you sure want to logout ?")
                    dialog.setPositiveButton("Yes") { _, _ ->
                        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                        sharedPreferences.all.clear()
                        val intent = Intent(this@HomeActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dialog.setNegativeButton("Cancel") { _, _ ->

                    }
                    dialog.create()
                    dialog.show()

                }
            }

            return@setNavigationItemSelectedListener true
        }
    }

    private fun checkInternetConnection() {
        if(ConnectionManager().checkConnectivity(this@HomeActivity)) {
            val dialog= AlertDialog.Builder(this@HomeActivity)
            dialog.setTitle("Hello..!")
            dialog.setMessage("Welcome to the tasty fooddie app")
            dialog.setPositiveButton("Ok") { _, _ ->

            }
            dialog.create()
            dialog.show()
        } else {
            val dialog= AlertDialog.Builder(this@HomeActivity)
            dialog.setTitle("Connectivity Error !!")
            dialog.setMessage("Please make sure your Phone is connected to Internet")
            dialog.setPositiveButton("Ok") { _, _ ->

            }
            dialog.create()
            dialog.show()
        }

    }


    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        //supportActionBar?.title="Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if (id==android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    fun openHomePage() {
        val fragment= HomeFragment()
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame,fragment)
        transaction.commit()

        supportActionBar?.title="Home"
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onBackPressed() {

        val fragment=supportFragmentManager.findFragmentById(R.id.frame)
        if (fragment is HomeFragment)
        {
            super.onBackPressed()
        }
        else {
            openHomePage()
        }
    }
}
