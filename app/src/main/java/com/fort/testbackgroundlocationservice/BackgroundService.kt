package com.fort.testbackgroundlocationservice

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.fort.testbackgroundlocationservice.local.AppDatabase
import com.fort.testbackgroundlocationservice.local.EntityLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar

class BackgroundService : Service() {
    private lateinit var locationManager: LocationManager
    lateinit var pref : SharedPreferences
    lateinit var appDB: AppDatabase

    companion object {
        private const val MIN_TIME_BW_UPDATES: Long = 1000 // 1 second
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES = 0.5f // 1 meter
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("location_service", "LocationBackground", NotificationManager.IMPORTANCE_NONE)
            channel.lightColor = Color.BLUE
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }

        var pendingIntent: PendingIntent
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(applicationContext, 0, Intent(applicationContext, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE  or PendingIntent.FLAG_UPDATE_CURRENT)
        }
        else {
            PendingIntent.getActivity(applicationContext, 0, Intent(applicationContext, MainActivity::class.java), PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notification: Notification = NotificationCompat.Builder(
            this, "location_service"
        )
            .setContentTitle("Location Background")
            .setContentText("You're Secure")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(546, notification)
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        appDB = AppDatabase.getDatabase(applicationContext)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        pref = applicationContext.getSharedPreferences("location_background", MODE_PRIVATE)

        // Request necessary permissions at runtime if not granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            Log.d("status_loc_status", "on stop")
            stopSelf() // Stop the service if location permission is not granted
            return
        }

        Log.d("status_loc_status", "on create")

        // Request location updates
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            MIN_TIME_BW_UPDATES,
            MIN_DISTANCE_CHANGE_FOR_UPDATES,
            locationListener
        )
    }


    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d("status_loc_status", "\n lat :- ${location.latitude} \n long :- ${location.longitude}")
            runBlocking {
                launch(Dispatchers.IO) {

                    // saved data in pref
                    val editor = pref.edit()
                    editor.putString("lat",location.latitude.toString())
                    editor.putString("long",location.longitude.toString())
                    editor.apply()

                    Log.d("status_loc_status", "location updated on pref ")


                    // saved data in local db (Room database)

                    val currentTime = Calendar.getInstance().time
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val time = dateFormat.format(currentTime)

                    appDB.locationDAO().insert(
                        EntityLocation(
                            0,
                            location.latitude.toString()?:"",
                            location.longitude.toString()?:"",
                            time
                        )
                    )


                    Log.d("status_loc_status", "location updated on room db ")
                }
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            Log.d("status_loc_status", "onStatusChanged: provider --> ${provider}, ${status}")
        }

        override fun onProviderEnabled(provider: String) {
            Log.d("status_loc_status", "onProviderEnabled: ${provider}")
        }

        override fun onProviderDisabled(provider: String) {
            Log.d("status_loc_status", "onProviderDisabled: ${provider}")
        }
    }
}