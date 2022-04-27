package com.thinkingdobby.tukusedbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.thinkingdobby.tukusedbook.data.User
import kotlinx.android.synthetic.main.activity_my_profile.*

class MyProfileActivity : AppCompatActivity() {

    // edit 후에도 반영될 수 있도록 onResume()에 프로필 로딩 등록
    override fun onResume() {
        super.onResume()
        val pref = getSharedPreferences("profile", MODE_PRIVATE)
        val id = pref.getString("user_id", "temp")!!

        Firebase.database.getReference("User").child(id).get().addOnSuccessListener {
            val user = it.getValue(User::class.java)!!

            myProfile_tv_nickname.text = user.name
            myProfile_tv_tel.text = user.tel
            myProfile_tv_department.text = user.department
            myProfile_tv_grade.text = user.grade.toString()
            myProfile_tv_intro.text = user.intro
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        myProfile_btn_back.setOnClickListener {
            finish()
        }

        myProfile_btn_edit.setOnClickListener {
            val intent = Intent(this, CreateProfileActivity::class.java)
            intent.putExtra("isEdit", true)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        myProfile_cv_toMyBook.setOnClickListener {
            val intent = Intent(this, MyBookActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        myProfile_cv_toBuyMessageList.setOnClickListener {
            val intent = Intent(this, MessageListActivity::class.java)
            intent.putExtra("mode", "buy")
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        myProfile_cv_toSellMessageList.setOnClickListener {
            val intent = Intent(this, MessageListActivity::class.java)
            intent.putExtra("mode", "sell")
            startActivity(intent)
            overridePendingTransition(0, 0)

        }

        myProfile_cv_toInterested.setOnClickListener {
            val intent = Intent(this, InterestedActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}
