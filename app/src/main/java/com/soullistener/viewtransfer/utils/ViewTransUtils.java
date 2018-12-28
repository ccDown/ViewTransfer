package com.soullistener.viewtransfer.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.orhanobut.logger.Logger;
import com.soullistener.viewtransfer.model.ViewSaveBean;
import com.soullistener.viewtransfer.model.ViewTreeBean;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * @author kuan
 * Created on 2018/12/18.
 * @description View传输工具
 */
public class ViewTransUtils {

    public static final File FileSavePath = Environment.getExternalStorageDirectory();
    public static String FileName = "viewsave.txt";

    /**
     * 通过递归获取全部的子view
     *
     * @param parentView
     * @return
     */
    public static ViewTreeBean rootViewToViewTreeBean(View parentView) {

        ArrayList<ViewTreeBean> viewTreeBeanArrayList = new ArrayList();
        //设置本view的信息
        ViewSaveBean viewSaveBean = viewTransBean(parentView);
        ViewTreeBean viewTreeBean = new ViewTreeBean();
        viewTreeBean.setViewSaveBean(viewSaveBean);

        if (parentView instanceof ViewGroup) {

            //对本view的子view进行遍历获取自view的信息并赋值给父view
            ViewGroup viewGroup = (ViewGroup) parentView;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childView = viewGroup.getChildAt(i);

                //遍历子view的
                ViewTreeBean childViewTreeBean = rootViewToViewTreeBean(childView);
                viewTreeBeanArrayList.add(childViewTreeBean);

            }
            //设置本view的子view信息
            viewTreeBean.setViewTreeBeans(viewTreeBeanArrayList);
        }
        return viewTreeBean;
    }

    /**
     * 将view信息转换为ViewSaveBean
     *
     * @param childView
     * @return
     */
    public static ViewSaveBean viewTransBean(View childView) {
        //通过反射获取控件名称
        String content = "";
        if (childView instanceof ViewGroup) {

        } else {
            //非view不能转换为TextView
            content = ((TextView) childView).getText().toString();
        }

        String name = childView.getClass().getName();


        //获取宽、高
        int[] pxwh = getWH(childView);
        int pxWidth = pxwh[0];
        int pxHeight = pxwh[1];

        //获取坐标
        int[] locations = getXY(childView);
        int x = locations[0];
        int y = locations[1];

        int dpWidth = ViewUnitConventUtils.px2dp(pxWidth);
        int dpHeight = ViewUnitConventUtils.px2dp(pxHeight);

        Logger.e("view---->>>>>bean"+childView.getClass().getName()+","+dpWidth+","+dpHeight+","+x+","+y);
        int backColor = -1;

        String backImage = "";

        ViewSaveBean.ViewInfo viewInfo = new ViewSaveBean.ViewInfo(x, y, dpWidth, dpHeight);
        ViewSaveBean.ViewContent viewContent = new ViewSaveBean.ViewContent(name, content);
        ViewSaveBean.ViewResource viewResource = new ViewSaveBean.ViewResource(backColor, backImage);
        ViewSaveBean viewSaveBean = new ViewSaveBean(viewContent, viewResource, viewInfo);
        return viewSaveBean;
    }

    /**
     * Bean输出到文件中进行保存
     *
     * @param file
     * @param viewTreeBean
     * @return
     */
    public static boolean bean2File(File file, ViewTreeBean viewTreeBean) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));

            Logger.i("bean2File: " + viewTreeBean.toString());
            objectOutputStream.writeObject(viewTreeBean);

            objectOutputStream.flush();
            objectOutputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }


    /**
     * 读取文件并转换为Bean
     *
     * @param file
     * @return
     */
    public static ViewTreeBean file2Bean(File file) {

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            ViewTreeBean viewTreeBean = (ViewTreeBean) objectInputStream.readObject();

            Logger.i("file2Bean: " + viewTreeBean.toString());

            return viewTreeBean;

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 根据控件名称生成实体控件
     *
     * @param context
     * @param name
     * @return
     */
    public static View viewTransfer(Context context, String name) {
        try {
            Class viewClazz = context.getClassLoader().loadClass(name);
            Constructor constructor = viewClazz.getConstructor(new Class[]{Context.class});
            return (View) constructor.newInstance(context);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根布局添加ViewTreeBean转换为的view
     *
     * @param parentView    父控件
     * @param viewTreeBeans
     */
    public static View viewTreeBeanAdded(ViewGroup parentView, ViewTreeBean viewTreeBeans) {
        Context context = parentView.getContext();

        //本view
        View view = beanTransView(context, viewTreeBeans.getViewSaveBean());
        if (view instanceof LinearLayout) {
            ((LinearLayout) view).setOrientation(LinearLayout.VERTICAL);
            view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        ArrayList<ViewTreeBean> childTreeBeans = viewTreeBeans.getViewTreeBeans();

        //如果是ViewGroup进行遍历添加view
        if (view instanceof ViewGroup) {
            //获取子view结构
            for (ViewTreeBean viewTreeBean : childTreeBeans) {

                viewTreeBeanAdded((ViewGroup) view, viewTreeBean);
            }
        }

        parentView.addView(view);

        return parentView;
    }

    /**
     * ViewSaveBean转换为View
     *
     * @param context
     * @param viewSaveBean
     * @return
     */
    public static View beanTransView(Context context, ViewSaveBean viewSaveBean) {
        View view = ViewTransUtils.viewTransfer(context, viewSaveBean.getViewContent().getName());

        view.setX(viewSaveBean.getViewInfo().getX());
        view.setY(viewSaveBean.getViewInfo().getY());

        int dpWidth = viewSaveBean.getViewInfo().getWidth();
        int dpHeight = viewSaveBean.getViewInfo().getHeight();

        int pxWidth = ViewUnitConventUtils.dp2px(dpWidth);
        int pxHeight = ViewUnitConventUtils.dp2px(dpHeight);

        Logger.e("bean---->>>>>view"+view.getClass().getName()+","+dpWidth+","+dpHeight+","+viewSaveBean.getViewInfo().getX()+","+viewSaveBean.getViewInfo().getY());

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(pxWidth,pxHeight);
        view.setLayoutParams(params);

        int backColor = viewSaveBean.getViewResource().getBackColor();

        if (-1 != backColor) {
            view.setBackgroundColor(Color.parseColor("#" + backColor));
        }

        String backImage = viewSaveBean.getViewResource().getBackImage();
        if (!TextUtils.isEmpty(backImage)) {
            view.setBackgroundColor(Color.parseColor(backImage));
        }
        String text = viewSaveBean.getViewContent().getContent();
        if (!TextUtils.isEmpty(text)) {
            ((TextView) view).setText(text);
        }

        return view;
    }

    /**
     * 获取某一点的颜色
     *
     * @param view
     * @param x
     * @param y
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static int getColor(View view, int x, int y) {

//        Point size = new Point();
//        DisplayMetrics metrics = new DisplayMetrics();
//        Display defaultDisplay = view.getContext().getWindow().getWindowManager().getDefaultDisplay();
//        defaultDisplay.getSize(size);
//        defaultDisplay.getMetrics(metrics);
//
//        final ImageReader imageReader = ImageReader.newInstance(size.x, size.y, PixelFormat.RGBA_8888, 1);
//        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
//                size.x, size.y, metrics.densityDpi,
//                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
//                imageReader.getSurface(), null, null);
//
//        GBData.reader = imageReader;

        return GBData.getColor(x, y);

    }

    /**
     * 获取view坐标
     *
     * @param view
     * @return
     */
    public static int[] getXY(View view) {
        //获取坐标
        int[] locations = new int[2];
        view.getLocationOnScreen(locations);
        return locations;
    }

    /**
     * 获取view 宽高
     *
     * @param view
     * @return
     */
    public static int[] getWH(View view) {
        int[] wh = new int[2];
        int widthModel = 0;
        int heightModel = 0;
        /**
         * 获取宽、高
         *
         * 分别遍历width和height的三种测量模式UNSPECIFIED(未指定)、EXACTLY(完全)、AT_MOST(至多)下的数据
         */

        while (widthModel < 3 || heightModel < 3) {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, (widthModel << 30)),
                    View.MeasureSpec.makeMeasureSpec(0, (heightModel << 30)));
            //getWidth 获取屏幕中view的宽度（跟parent有关）
            //getMeasuredWidth 获取实际的view高度
            wh[0] = view.getMeasuredWidth();
            wh[1] = view.getMeasuredHeight();

            if (wh[0] == 0 && widthModel < 3) {
                widthModel += 1;
            } else {
                widthModel = 4;
            }

            if (wh[1] == 0 && heightModel < 3) {
                heightModel += 1;
            } else {
                heightModel = 4;
            }
        }

        return wh;
    }

}
