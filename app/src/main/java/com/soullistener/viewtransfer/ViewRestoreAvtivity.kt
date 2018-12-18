package com.soullistener.viewtransfer

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_viewrestore.*
import java.io.File


/**
 * @description
 * @author kuan
 * Created on 2018/12/18.
 */
class ViewRestoreAvtivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewrestore)

        val file = File(Environment.getExternalStorageDirectory(), "viewsave.txt")

        val viewBeanList =  ViewTransUtils.file2Bean(file)
        val viewList = ViewTransUtils.bean2ViewTrans(this,viewBeanList)
        viewList.forEach { ll_viewrestore.addView(it) }

    }
}