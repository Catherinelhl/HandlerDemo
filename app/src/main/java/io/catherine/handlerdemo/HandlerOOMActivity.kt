package io.catherine.handlerdemo

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference

/*
+--------------+---------------------------------
+ author       +   Catherine Liu
+--------------+---------------------------------
+ since        +   2019-05-16 11:42
+--------------+---------------------------------
+ projectName  +   HandlerDemo
+--------------+---------------------------------
+ packageName  +   io.catherine.handlerdemo
+--------------+---------------------------------
+ description  +   验证Handler的内存泄漏问题
+--------------+---------------------------------
+ version      +  link:https://www.jianshu.com/p/ed9e15eff47a
+--------------+---------------------------------
*/

class HandlerOOMActivity : Activity() {

    private lateinit var handler: Handler//创建一个内部类handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handler_oom)
        initView()
        initListener()
    }

    /**
     * 通过建立内部类和外部类来实例化Handler实现线程之间的通信，但是这里会存在一个OOM的问题。
     * 1：因为内部类和外部类都会依赖父类，且这里没有单独指定Looper，故自动绑定当前线程（主线程）的Looper，MessageQueue
     *
     * WARNING:
     * This Handler class should be static or leaks might occur (anonymous android.os.Handler) less... (⌘F1)
    Inspection info:Since this Handler is declared as an inner class, it may prevent the outer class from being
    garbage collected. If the Handler is using a Looper or MessageQueue for a thread other than the main thread,
    then there is no issue. If the Handler is using the Looper or MessageQueue of the main thread, you need to
    fix your Handler declaration, as follows: Declare the Handler as a static class; In the outer class,
    instantiate a WeakReference to the outer class and pass this object to your Handler when you
    instantiate the Handler; Make all references to members of the outer class using the WeakReference
    object.  Issue id: HandlerLeak
     */
    private fun initView() {
        //First method:内部类的形式
//        innerClassOfHandlerMethod()
        //Second method:外部类的形式
        outerClassOfHandlerMethod()


    }

    private fun innerClassOfHandlerMethod() {
        //1：实例化一个内部类的handler类对象
        handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            //通过复写handleMessage来确定更新UI的操作
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                msg ?: return
                when (msg.what) {
                    1 -> {
                        println("inner thread 1 message")
                    }
                    2 -> {
                        println("inner thread 2 message")
                    }
                }
            }

        }

        //2：启动子线程1
        object : Thread() {
            override fun run() {
                super.run()
                sleep(1000)
                //a. 定义要发送的消息
                val msg: Message = Message.obtain()
                msg.what = 1//消息标示
                msg.obj = "Catherine"//消息存放
                //b. 传入主线程的Handler & 向其他MessageQueue发送消息
                handler.sendMessage(msg)
            }
        }.start()
        //3:启动子线程2
        object : Thread() {
            override fun run() {
                super.run()
                sleep(5000)
                //a. 定义要发送的消息
                val msg: Message = Message.obtain()
                msg.what = 2//消息标示
                msg.obj = "Catherine hello"//消息存放
                //b. 传入主线程的Handler & 向其他MessageQueue发送消息
                handler.sendMessage(msg)
            }
        }.start()
    }


    private fun initListener() {
        tv.setOnClickListener {
            handler.sendEmptyMessageDelayed(1, 1000)
        }
    }


    /**
     * Second method:通过外部类的方式
     */

    private fun outerClassOfHandlerMethod() {

        //1：实例化一个外部类的handler类对象
        handler = OutHandler(this)

        //2：启动子线程1
        object : Thread() {
            override fun run() {
                super.run()
                sleep(1000)
                //a. 定义要发送的消息
                val msg: Message = Message.obtain()
                msg.what = 1//消息标示
                msg.obj = "Catherine"//消息存放
                //b. 传入主线程的Handler & 向其他MessageQueue发送消息
                handler.sendMessage(msg)
            }
        }.start()
        //3:启动子线程2
        object : Thread() {
            override fun run() {
                super.run()
                sleep(5000)
                //a. 定义要发送的消息
                val msg: Message = Message.obtain()
                msg.what = 2//消息标示
                msg.obj = "Catherine hello"//消息存放
                //b. 传入主线程的Handler & 向其他MessageQueue发送消息
                handler.sendMessage(msg)
            }
        }.start()

    }

    /**
     *  建立一个外部类
     *
     *  Modify:OOM
     */
    private class OutHandler(activity: Activity) : Handler() {
        //定义成弱类型
        var reference: WeakReference<Activity> = activity as WeakReference<Activity>


        //通过复写handleMessage来确定更新UI的操作
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            msg ?: return
            when (msg.what) {
                1 -> {
                    println("outer thread 1 message")
                }
                2 -> {
                    println("outer thread 2 message")
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        handler ?: return
        //Second Method to avoid OOM,当外部类结束生命周期时，晴空handler内消息队列
        handler.removeCallbacksAndMessages(null)
    }

}