package com.thinkingdobby.tukusedbook

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_loading.*

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        loading_v_start.setOnClickListener {
            val first = getSharedPreferences("basic", MODE_PRIVATE).getBoolean("isFirst", true)
//            val first = true

            val intent = if (first) Intent(this, CreateProfileActivity::class.java) else Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
