package com.soullistener.viewtransfer.model

import java.io.Serializable

/**
 * @description
 * @author kuan
 * Created on 2018/12/3.
 */

/**
 * 保存View的数据信息
 */
data class ViewSaveBean(var viewContent: ViewContent, var viewResource: ViewResource, var viewInfo: ViewInfo) : Serializable {

    /**
     * 储存view的内容
     */
    data class ViewContent(var name: String = "", var content: String = ""): Serializable {
        override fun toString(): String {
            return "name:$name ,content:$content"
        }
    }

    /**
     * 储存view的背景等
     */
    data class ViewResource(var backColor: Int = 0,var backImage : String = "") : Serializable{
        override fun toString(): String {
            return "backColor:$backColor,backImage:$backImage"
        }
    }

    /**
     * 储存view的坐标、宽高
     */
    data class ViewInfo(var x: Int = 0, var y: Int = 0, var width: Int = 0, var height: Int = 0) : Serializable {
        override fun toString(): String {
            return "x:$x,y:$y,width:$width,height:$height" +">>>>>>>>>>>>"
        }
    }

}

/**
 * View列表的个数
 */
data class ViewNumber(val number: Int) : Serializable