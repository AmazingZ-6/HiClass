package com.example.hiclass.utils

object TypeSwitcher {
    fun charToInt(char: Char):Int{
        var res = 0
        when(char){
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
}