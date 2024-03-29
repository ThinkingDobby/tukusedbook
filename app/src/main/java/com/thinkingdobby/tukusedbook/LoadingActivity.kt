package com.thinkingdobby.tukusedbook

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_loading.*

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        loading_v_start.setOnClickListener {
            val first = getSharedPreferences("basic", MODE_PRIVATE).getBoolean("isFirst", true)
//            val first = true

            val intent = if (first) Intent(this, CreateProfileActivity::class.java) else Intent(
                this,
                MainActivity::class.java
            )
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
    }
}
