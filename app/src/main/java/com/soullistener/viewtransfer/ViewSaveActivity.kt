package com.soullistener.viewtransfer

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import com.soullistener.viewtransfer.utils.ToastUtils
import com.soullistener.viewtransfer.utils.ViewTransUtils
import com.soullistener.viewtransfer.widget.DragView
import kotlinx.android.synthetic.main.activity_viewsave.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


class ViewSaveActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    var permission = false

    var perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewsave)

        if (EasyPermissions.hasPermissions(this, perms.toString())) {

            // 已经申请过权限，做想做的事
        } else {
            // 没有申请过权限，现在去申请
            EasyPermissions.requestPermissions(
                this, perms[0], 0,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        textview.setOnClickListener {

            if (!permission){
                ToastUtils.showShort("未获取权限")
                return@setOnClickListener
            }

            val file = File(Environment.getExternalStorageDirectory(),"viewsave.txt")

            val viewroot = findViewById<DragView>(R.id.ll_viewsave)

            val viewList = ViewTransUtils.rootViewToViewTreeBean(viewroot)
            val isSuccessed = ViewTransUtils.bean2File(file,viewList)

            if (isSuccessed) {
                val intent = Intent(this@ViewSaveActivity, ViewRestoreAvtivity::class.java)
                startActivity(intent)
            } else {
                ToastUtils.showShort("写入文件失败")
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        permission = true
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        permission = false
    }


}

