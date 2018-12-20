package com.soullistener.viewtransfer.model

import java.io.Serializable
import java.util.*

/**
 * @description
 * @author kuan
 * Created on 2018/12/3.
 */

data class ViewTreeBean(var childViews : ArrayList<ViewTreeBean>, val viewInfo : ViewSaveBean) : Serializable {

    /**存储子View */

    /**存储本View信息 */

}

/**
 * 保存View的数据信息
 */
data class ViewSaveBean(val viewContent: ViewContent, val viewResource: ViewResource, val viewInfo: ViewInfo) : Serializable {

    /**
     * 储存view的内容
     */
    data class ViewContent(val name: String = "", val content: String = ""): Serializable {
        override fun toString(): String {
            return super.toString() + "name:$name ,content:$content"
        }
    }

    /**
     * 储存view的背景等
     */
    data class ViewResource(val backColor: Int = 0,val backImage : String = "") : Serializable{
        override fun toString(): String {
            return super.toString() + "backColor:$backColor,backImage:$backImage"
        }
    }

    /**
     * 储存view的坐标、宽高
     */
    data class ViewInfo(val x: Int = 0, val y: Int = 0, val width: Int = 0, val height: Int = 0) : Serializable {
        override fun toString(): String {
            return super.toString() + "x:$x,y:$y,width:$width,height:$height"
        }
    }

}

/**
 * View列表的个数
 */
data class ViewNumber(val number: Int) : Serializable