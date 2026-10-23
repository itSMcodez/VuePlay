package com.itsmcodez.vueplay.common

interface CountDownTimerCallback {
    fun onTick(millisUntilFinished: Long)
    fun onFinish()
    fun onCancel()
}