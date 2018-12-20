package com.soullistener.viewtransfer.widget

import android.content.Context
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout

/**
 * @description 实现子view的滑动
 * @author kuan
 * Created on 2018/3/21.
 */
class DragView(context: Context,attrs: AttributeSet) : LinearLayout(context,attrs) {


    fun change(): Unit {
        Log.e("TAG","asd")
    }

    override fun addView(child: View?) {
        super.addView(child)
    }
    private var viewDragHelper :ViewDragHelper? = null
    private var mViewDragLeft :Int? = null
    private var mViewDragTop :Int? = null

    init {

        viewDragHelper = ViewDragHelper.create(this, 1.0f, object : ViewDragHelper.Callback() {
            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                // 右侧边界
                if (left > getWidth() - child.getMeasuredWidth()) {
                    return getWidth() - child.getMeasuredWidth()
                }
                // 左侧边界
                else if (left < 0) {
                    return 0
                }
                return left

            }

            /**
             * 处理竖直方向的滑动
             */
            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                //顶部边界
                if (top >height-child.measuredHeight){
                    return height-child.measuredHeight
                }
                //底部边界
                else if (top<0){
                    return 0
                }
                return top
            }

            /**
             * true  捕获子View
             * false 不对子View进行捕获
             */
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                //限定可移动的View种类
//                if (child is CircleView) {
//                    return true
//                }

                return canMoved
            }

            /**
             * 事件正在被执行
             */
            override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
                super.onViewCaptured(capturedChild, activePointerId)
                mViewDragLeft = capturedChild.left
                mViewDragTop = capturedChild.top
            }

            /**
             * 事件结束
             */
            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                super.onViewReleased(releasedChild, xvel, yvel)
                viewDragHelper!!.settleCapturedViewAt(mViewDragLeft!!,mViewDragTop!!)
                invalidate()
            }


            //对于clickable = 1 的按钮等进行移动
            override fun getViewHorizontalDragRange(child: View): Int {
                return 1
            }

            override fun getViewVerticalDragRange(child: View): Int {
                return 1
            }
        })
    }


    override fun computeScroll() {
        super.computeScroll()

        if (canTouched) {
            if (viewDragHelper!!.continueSettling(true)) {
                invalidate()
            }
        }
    }

    /**
     * 拦截事件
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper!!.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        viewDragHelper!!.processTouchEvent(ev)
        return true
    }

    /*可回弹*/
    public var canTouched = false

    public fun canTouch(touched: Boolean): Unit {
        canTouched = touched
    }

    /*可移动*/
    public var canMoved = true
    public fun canMoved(moved: Boolean): Unit {
        canMoved = moved
    }


    /**
     *
     * ViewDragHelper.CallBack

    tryCaptureView：如果返回true表示捕获相关View，你可以根据第一个参数child决定捕获哪个View。
    clampViewPositionVertical：计算child垂直方向的位置，top表示y轴坐标（相对于ViewGroup），默认返回0（如果不复写该方法）。
    这里，你可以控制垂直方向可移动的范围。
    clampViewPositionHorizontal：与clampViewPositionVertical类似，只不过是控制水平方向的位置。


     */

}