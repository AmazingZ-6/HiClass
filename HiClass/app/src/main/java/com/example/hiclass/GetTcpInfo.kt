package com.example.hiclass

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hiclass.setting.App.Companion.context
import com.example.hiclass.utils.ConnectTcp
import com.example.hiclass.utils.StatusUtil
import es.dmoral.toasty.Toasty
import java.util.*
import java.util.regex.Pattern
import kotlin.concurrent.thread

class GetTcpInfo : AppCompatActivity() {

    private lateinit var viewModel: GetTcpInfoViewModel
    private val classItem = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusUtil.setStatusBarMode(this, true, R.color.little_white)
        setContentView(R.layout.activity_getinfo)
        viewModel = ViewModelProvider(this).get(GetTcpInfoViewModel::class.java)
        val btnLogin: Button = findViewById(R.id.login)
        val btnPass: Button = findViewById(R.id.pass)
        val loadingProgressBar: ContentLoadingProgressBar = findViewById(R.id.load_progressbar)
        val userSno: EditText = findViewById(R.id.username)
        viewModel.isLoginFailed.observe(this, androidx.lifecycle.Observer {
            if (it) {
                Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show()
                loadingProgressBar.visibility = GONE
            }
        })
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
            if (userSno.text.toString().length != 10) {
                Toast.makeText(this, "请输入正确的学号！", Toast.LENGTH_SHORT).show()
            } else {
                loadingProgressBar.visibility = VISIBLE
                loadingProgressBar.indeterminateDrawable.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        android.R.color.darker_gray
                    ), PorterDuff.Mode.MULTIPLY
                );
                thread {
                    val userInfo = ConnectTcp.login(userSno.text.toString())
                    if (userInfo != null) {
                        if (userInfo.length > 10)
                            dealInfo(userInfo)
                        else
                            viewModel.loginFailed()
                    } else {
                        viewModel.loginFailed()
                    }

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
        } else {
//            loadingProgressBar.visibility = GONE
            Toast.makeText(this, "请检查学号正确性或网络连接状况！", Toast.LENGTH_LONG).show()
        }

    }
}