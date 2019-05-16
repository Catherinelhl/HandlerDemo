package io.catherine.handlerdemo

/*
+--------------+---------------------------------
+ author       +   Catherine Liu
+--------------+---------------------------------
+ since        +   2019-05-16 18:20
+--------------+---------------------------------
+ projectName  +   HandlerDemo
+--------------+---------------------------------
+ packageName  +   io.catherine.handlerdemo
+--------------+---------------------------------
+ description  +
+--------------+---------------------------------
+ version      +
+--------------+---------------------------------
*/

import android.app.Activity
import android.os.Handler
import android.os.Message
import android.util.Log

import java.lang.ref.WeakReference

import android.content.ContentValues.TAG

class test {
    // 分析1：自定义Handler子类
    // 设置为：静态内部类
    private class FHandler// 在构造方法中传入需持有的Activity实例
        (activity: Activity) : Handler() {

        // 定义 弱引用实例
        private val reference: WeakReference<Activity>

        init {
            // 使用WeakReference弱引用持有Activity实例
            reference = WeakReference(activity)
        }

        // 通过复写handlerMessage() 从而确定更新UI的操作
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> Log.d(TAG, "收到线程1的消息")
                2 -> Log.d(TAG, " 收到线程2的消息")
            }
        }
    }
}
