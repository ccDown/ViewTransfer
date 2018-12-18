package com.soullistener.viewtransfer

import java.io.Serializable

/**
 * @description
 * @author kuan
 * Created on 2018/12/3.
 */

/**
 * 保存View的数据信息
 */
data class ViewSaveBean(val name :String ,val content : String,val viewInfo: ViewInfo) : Serializable{
    override fun toString(): String {

        return super.toString() +"name:${name} ,content:${content},viewInfo:${viewInfo}"
    }
    data class ViewInfo(val x :Int,val y:Int,val width :Int,val height:Int) : Serializable{
        override fun toString(): String {
            return super.toString() + "x:${x},y:${y},width:${width},height:${height}"
        }
    }

}

/**
 * View列表的个数
 */
data class ViewNumber(val number : Int) : Serializable