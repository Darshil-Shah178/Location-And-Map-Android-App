package com.example.locationandmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {
    private lateinit var marker: Marker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(
            this,
            PreferenceManager.getDefaultSharedPreferences(this)
        )
        setContentView(R.layout.activity_map)
    }
}