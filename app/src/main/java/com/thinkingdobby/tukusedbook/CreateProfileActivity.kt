package com.thinkingdobby.tukusedbook

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.thinkingdobby.tukusedbook.data.User
import com.thinkingdobby.tukusedbook.data.departments
import kotlinx.android.synthetic.main.activity_create_profile.*


class CreateProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        createProfile_et_name.addTextChangedListener(object : TextWatcher {
            var prev = ""

            override fun afterTextChanged(p0: Editable?) {
                if (createProfile_et_name.text.toString() != "") {
                    createProfile_et_name.setBackgroundResource(R.drawable.createprofile_et_essential_satisfied)
                } else {
                    createProfile_et_name.setBackgroundResource(R.drawable.createprofile_et_essential)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

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

        // ?????? ??????
        createProfile_btn_department.setOnClickListener {
            val dlg = AlertDialog.Builder(this, R.style.AlertDialogStyle)
            dlg.setItems(departments) { _, which ->
                createProfile_et_department.setText(departments[which])
            }
            dlg.setTitle("?????? ??????")
            dlg.show()
        }

        createProfile_btn_grade.setOnClickListener {
            val dlg = AlertDialog.Builder(this, R.style.AlertDialogStyle)
            val grades = arrayOf("1", "2", "3", "4")
            dlg.setItems(grades) { _, which ->
                createProfile_et_grade.setText(grades[which])
            }
            dlg.setTitle("?????? ??????")
            dlg.show()
        }

        val pref = getSharedPreferences("profile", MODE_PRIVATE)
        val editor = pref.edit()
        val edit = intent.getBooleanExtra("isEdit", false)

        val ref = FirebaseDatabase.getInstance().getReference("User").push()
        var id = ref.key!!

        if (edit) {
            id = pref.getString("user_id", "temp")!!
            createProfile_tv_registerTitle.text = "????????? ??????"
            createProfile_btn_register.text = "????????? ??????"

            Firebase.database.getReference("User").child(id).get().addOnSuccessListener {
                val user = it.getValue(User::class.java)
                createProfile_et_name.setText(user?.name)
                createProfile_et_tel.setText(user?.tel)
                createProfile_et_department.setText(user?.department)
                createProfile_et_grade.setText(user?.grade.toString())
                createProfile_et_intro.setText(user?.intro)
            }
        }

        createProfile_btn_register.setOnClickListener {
            if (createProfile_et_name.text.toString() == "") {
                Toast.makeText(this, "???????????? ??????????????????.", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(
                    id,
                    createProfile_et_name.text.toString(),
                    createProfile_et_tel.text.toString(),
                    createProfile_et_department.text.toString(),
                    createProfile_et_grade.text.toString().toInt(),
                    createProfile_et_intro.text.toString()
                )

                if (edit) Firebase.database.getReference("User/$id").setValue(user)
                else {
                    ref.setValue(user)
                    editor.putString("user_id", id).apply()
                    getSharedPreferences("basic", MODE_PRIVATE).edit().putBoolean("isFirst", false)
                        .apply()
                }

                if (edit) Toast.makeText(this, "???????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
                else Toast.makeText(this, "???????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view: View? = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText && !view.javaClass
                .name.startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x: Float = ev.rawX + view.getLeft() - scrcoords[0]
            val y: Float = ev.rawY + view.getTop() - scrcoords[1]
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) (this.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager).hideSoftInputFromWindow(
                this.window.decorView.applicationWindowToken, 0
            )
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}
