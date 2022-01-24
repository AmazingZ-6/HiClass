package com.example.hiclass.utils

import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.Socket


object ConnectTcp {
    private val sc: Socket? = null
    private const val ip = "101.43.18.202"  //本地服务端地址
    private const val port = 12345

//    //图片交互接口
//    private val ImageSocket: Socket? = null

    private var dout: OutputStream? = null
    private var din: InputStreamReader? = null

//    //图片交互流
//    private val imageInputStream: InputStream? = null
//    private val imageFileOutputSteam: DataOutputStream? = null
    var isConnect = false
//    var ImageConncet = false

    private fun initConnect() {
        try {
            val sc: Socket = Socket(ip, port)
            din = InputStreamReader(sc.getInputStream(), "utf-8")
            dout = sc.getOutputStream()
            sc.soTimeout = 5000
            if (din != null && dout != null) {
                isConnect = true
            } else {
                initConnect()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun sendSno(sno: String?):Boolean {
        try {
            if (isConnect) {
                if (dout != null && sno != null) {
                    dout!!.write(sno.toByteArray())
                } else {
                    return false
                }
            } else {
                return false
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return true
    }

    private fun receiveItemInfo(): String? {
        var info = ""
        var infoTemp = ""
        try {
            if (isConnect) {
                while (true) {
                    val inMessage = CharArray(20480)
                    val a = din!!.read(inMessage)
                    if (a < 0) {
                        break
                    }
                    infoTemp = String(inMessage, 0, a)
                    println(infoTemp)
                    info += infoTemp
                }
            } else {
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return info
    }

    fun login(sno: String?): String? {
        if (sno?.length != 10){
            return null
        }
        initConnect()
        val mSend: String = sno
        val isSend = sendSno(mSend)
        if (!isSend){
            return null
        }
        var reply: String? = ""
        reply = receiveItemInfo()
        if (reply == null){
            return null
        }
        closeConnect()
        return reply
    }


    private fun closeConnect() {
        try {
            if (din != null) {
                din!!.close()
            }
            if (dout != null) {
                dout!!.close()
            }
            sc?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        isConnect = false
    }

}
