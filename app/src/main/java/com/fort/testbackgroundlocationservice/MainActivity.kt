package com.fort.testbackgroundlocationservice

import android.Manifest
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fort.testbackgroundlocationservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private val INTERVAL_MS: Long = 2000 // 1 second interval
    lateinit var pref : SharedPreferences

    private val REQUEST_IGNORE_BATTERY_OPTIMIZATIONS: Int = 566
    private val REQUEST_ENABLE_BACKGROUND: Int  = 568

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



//        val serviceIntent = Intent(this@MainActivity, BackgroundService::class.java)
//        stopService(serviceIntent)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if ( Build.VERSION.SDK_INT >= 23){
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED  ){
                    requestPermissions( arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101);
//                    return ;
                }
            }
        }

        pref = applicationContext.getSharedPreferences("location_background", MODE_PRIVATE)



        //is background service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    data = Uri.parse("package:${packageName}")
                }
                startActivityForResult(intent, REQUEST_ENABLE_BACKGROUND)
            }
        }

        binding.apply {
            appLaunch.setOnClickListener {
                actionOnReadNotification()
            }
            tvBattery.setOnClickListener {
                actionOnBatteryOptimizations()


                val serviceIntent = Intent(this@MainActivity, BackgroundService::class.java)
                startService(serviceIntent)
            }
        }

        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                // code block will repeat for everytime
//                Log.d("status_loc_status", "repeating")

                if (pref.getString("lat","") != ""){
                    Log.d("status_loc_status", "lat long set on textview")
                    findViewById<TextView>(R.id.tvLat).text = pref.getString("lat","")
                    findViewById<TextView>(R.id.tvLongi).text = pref.getString("long","")
                }
                handler!!.postDelayed(this, INTERVAL_MS)
            }
        }


        handler!!.post(runnable!!)

    }

    fun actionOnReadNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!notificationManager.isNotificationPolicyAccessGranted) {
                // Prompt the user to grant the Read notifications permission
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                startActivityForResult(intent,104)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
            if (mode != AppOpsManager.MODE_ALLOWED) {
                // Prompt the user to grant the Read notifications permission
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                startActivityForResult(intent,104)
//                startActivity(intent)
            }
        }
    }

    fun actionOnBatteryOptimizations(){
        // Request ignore battery optimizations Manifest.permission
        // Request allow app to stay connected in the background permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    data = Uri.parse("package:${packageName}")
                }
                startActivityForResult(intent, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            }
        }
    }
}