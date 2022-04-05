package com.thinkingdobby.tukusedbook

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.thinkingdobby.tukusedbook.data.User
import kotlinx.android.synthetic.main.activity_create_profile.*


class CreateProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        createProfile_et_intro.addTextChangedListener(object : TextWatcher {
            var prev = ""

            override fun afterTextChanged(p0: Editable?) {
                if (createProfile_et_intro.lineCount >= 7) {
                    createProfile_et_intro.setText(prev)
                    createProfile_et_intro.setSelection(createProfile_et_intro.length())
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                prev = p0.toString()
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        val ref = FirebaseDatabase.getInstance().getReference("User").push()
        createProfile_btn_register.setOnClickListener {
            val user = User(
                ref.key!!,
                createProfile_et_name.text.toString(),
                createProfile_et_tell.text.toString(),
                createProfile_et_department.text.toString(),
                createProfile_et_grade.text.toString().toInt(),
                createProfile_et_intro.text.toString()
            )

            ref.setValue(user)
        }
    }
}
