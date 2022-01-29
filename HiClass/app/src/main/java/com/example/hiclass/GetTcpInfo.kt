package com.example.hiclass

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import com.example.hiclass.utils.ConnectTcp
import es.dmoral.toasty.Toasty
import java.util.*
import java.util.regex.Pattern
import kotlin.concurrent.thread

class GetTcpInfo : AppCompatActivity() {

    private val classItem = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_getinfo)
        val btnLogin: Button = findViewById(R.id.login)
        val btnPass: Button = findViewById(R.id.pass)
        val loadingProgressBar: ContentLoadingProgressBar = findViewById(R.id.load_progressbar)
        val userSno: EditText = findViewById(R.id.username)
        btnPass.setOnClickListener {
            val mIntent = intent
            val loginFlag = mIntent.getStringExtra("isReLogin")
            val intent = Intent(this, StartActivity::class.java)
            intent.putExtra("class_info", "123456789123")
            if (loginFlag != null && loginFlag == "true") {
                intent.putExtra("isReLogin", "true")
            }
            startActivity(intent)
            finish()
        }
        btnLogin.setOnClickListener {
            if (userSno.text.toString().length != 10){
                Toast.makeText(this,"请输入正确的学号！",Toast.LENGTH_SHORT).show()
            }else{
                loadingProgressBar.visibility = VISIBLE
                thread {
                    val userInfo = ConnectTcp.login(userSno.text.toString())
                    dealInfo(userInfo)
                }
            }
        }
    }

    private fun dealInfo(info: String?) {
        if (info != null) {
            val p = Pattern.compile("(?<=')[\\u4e00-\\u9fa5_0-9a-zA-Z].*?(?=')")
            val m = p.matcher(info)
            var a = 0
            while (m.find()) {
                classItem.add(a++, m.group())
            }
            val loginFlag = intent.getStringExtra("isReLogin")
//            val sp = getSharedPreferences("data", MODE_PRIVATE)
//            val editor = sp.edit()
//            editor.putString("username", userSno.text.toString())
//            editor.apply()
            //                    String data = getResources().getString(R.string.username);
//                    data = String.format(data, userName);
            val intent = Intent(this, StartActivity::class.java)
            intent.putExtra("class_info", classItem.toString())
            if (loginFlag != null && loginFlag == "true") {
                intent.putExtra("isReLogin", "true")
            }
            startActivity(intent)
            finish()
        }else{
//            loadingProgressBar.visibility = GONE
            Toast.makeText(this,"请检查学号正确性或网络连接状况！",Toast.LENGTH_LONG).show()
        }

    }
}