package com.example.hiclass.utils

object TypeSwitcher {
    fun charToInt(char: Char): Int {
        var res = 0
        when (char) {
            '1' -> res = 1
            '2' -> res = 2
            '3' -> res = 3
            '4' -> res = 4
            '5' -> res = 5
            '6' -> res = 6
            '7' -> res = 7
            '8' -> res = 8
            '9' -> res = 9
            '0' -> res = 0
        }
        return res
    }

    fun chineseToInt(char: Char): Int {
        when (char) {
            '一' -> {
                return 1
            }
            '二' -> {
                return 2
            }
            '三' -> {
                return 3
            }
            '四' -> {
                return 4
            }
            '五' -> {
                return 5
            }
            '六' -> {
                return 6
            }
            '日' -> {
                return 7
            }
        }
        return 0
    }

    fun charToChinese(char: Char): Char {
        when (char) {
            '1' -> {
                return '一'
            }
            '2' -> {
                return '二'
            }
            '3' -> {
                return '三'
            }
            '4' -> {
                return '四'
            }
            '5' -> {
                return '五'
            }
            '6' -> {
                return '六'
            }
            '7' -> {
                return '日'
            }
        }
        return '一'
    }

    fun intToWeekday(int: Int): String {
        when (int) {
            0 -> {
                return "周一"
            }
            1 -> {
                return "周二"
            }
            2 -> {
                return "周三"
            }
            3 -> {
                return "周四"
            }
            4 -> {
                return "周五"
            }
            5 -> {
                return "周六"
            }
            6 -> {
                return "周日"
            }
            7 -> {
                return "明天"
            }
            8 -> {
                return "今天"
            }
        }
        return "周一"
    }

    fun subStTermDay(termDay: String): List<String> {
        val nTemp = termDay.split("周")[1].substring(1)
        val wTemp = termDay.split("周")[0]
        return listOf(nTemp, wTemp)
    }
}