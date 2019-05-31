package io.catherine.handlerdemo

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message

/*
+--------------+---------------------------------
+ author       +   Catherine Liu
+--------------+---------------------------------
+ since        +   2019-05-17 10:39
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

class HandlerUseActivity : Activity() {

    /**
     *  练习
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handler_use)

        initView()

        initListener()
    }

    /**
     *有两种调动消息发送。
     * Handler.sendMessage()
     * Handler.post()
     *
     */
    private fun initView() {
        val handler: Handler = object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                println("handleMessage receive the message")
            }

        }

        object : Thread() {

            override fun run() {
                super.run()
                println("start the thread...")
                //there are two method to get the message.the one is new(),and the other one is obtain().
                val message: Message = Message.obtain()
                //method 1:sendMessage()
                handler.sendMessage(message)


            }
        }.start()


        object : Thread() {

            override fun run() {
                super.run()
                println("start the second thread...${System.currentTimeMillis()}")
                sleep(1000)
                //there are two method to get the message.the one is new(),and the other one is obtain().
                val message: Message = Message.obtain()
                //method 1:sendMessage()
                handler.post {
                    println("handler post ....${System.currentTimeMillis()}")

                }


            }
        }.start()

    }

    private fun initListener() {

    }
}