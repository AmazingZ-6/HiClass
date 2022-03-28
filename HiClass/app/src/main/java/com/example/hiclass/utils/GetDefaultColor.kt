package com.example.hiclass.utils

import com.example.hiclass.R
import com.example.hiclass.data_class.ItemDataBean
import java.util.regex.Pattern

object GetDefaultColor {
    fun returnColor(time: String): Int {
        val p = Pattern.compile("[0-9]")
        var classStart = 0
        var classEnd = 0
        val m = p.matcher(time)
        val s: MutableList<Int> = ArrayList()
        var a = 0
        while (m.find()) {
            s.add(a++, m.group().toInt())
        }
        if (s.size == 3) {
            classStart = s[1]
            classEnd = s[2]
        }
        if (s.size == 4) {
            classStart = s[1]
            classEnd = s[2] * 10 + s[3]
        }
        if (s.size == 5) {
            classStart = s[1] * 10 + s[2]
            classEnd = s[3] * 10 + s[4]
        }
        val height = ((classStart + 1) / 2 - 1) * 360
        if (height <= 360) return R.color.colorTrans1
        if (height in 361..1080) return R.color.colorTrans2
        if (height > 1080) return R.color.colorTrans3
        return R.color.colorTrans1
    }
}