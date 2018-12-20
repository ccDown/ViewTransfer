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
import com.soullistener.viewtransfer.model.ViewNumber;
import com.soullistener.viewtransfer.model.ViewSaveBean;

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
     * @param parentView
     * @return
     */
    public static ArrayList<View> getAllChildViews(View parentView){
        ArrayList<View> arrayList = new ArrayList();

        if (parentView instanceof  ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parentView;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childView = viewGroup.getChildAt(i);
                arrayList.add(childView);
                arrayList.addAll(getAllChildViews(childView));
            }
        }

        return arrayList;
    }

    /**
     * 将View转换为Bean对象
     * @param viewList
     * @return
     */
    public static ArrayList<ViewSaveBean> viewTrans2Bean(ArrayList<View> viewList) {

        ArrayList<ViewSaveBean> list = new ArrayList<>();

        for (View childView : viewList) {

            //通过反射获取控件名称
            String content = "";
            if (childView instanceof ViewGroup){

            } else {
                //非view不能转换为TextView
                content = ((TextView)childView).getText().toString();
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

            int backColor = -1;

            String backImage = "";

            ViewSaveBean.ViewInfo  viewInfo = new ViewSaveBean.ViewInfo(x,y,dpWidth,dpHeight);
            ViewSaveBean.ViewContent viewContent = new ViewSaveBean.ViewContent(name,content);
            ViewSaveBean.ViewResource viewResource = new ViewSaveBean.ViewResource(backColor,backImage);
            ViewSaveBean viewSaveBean = new ViewSaveBean(viewContent,viewResource,viewInfo);
            Logger.i( "viewSaveBean: "+viewSaveBean.toString());

            list.add(viewSaveBean);
        }

        return list;
    }

    /**
     * Bean输出到文件中进行保存
     * @param list
     * @return
     */
    public static boolean bean2File(File file,ArrayList<ViewSaveBean> list){
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

            ViewNumber viewNumber = new ViewNumber(list.size());
            objectOutputStream.writeObject(viewNumber);

            for (ViewSaveBean viewSaveBean : list) {
                Logger.i( "bean2File: "+viewSaveBean.toString());
                objectOutputStream.writeObject(viewSaveBean);
            }

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
     * @param file
     * @return
     */
    public static ArrayList<ViewSaveBean> file2Bean(File file){
        ArrayList<ViewSaveBean> viewSaveBeanArrayList = new ArrayList<>();
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));

            ViewNumber viewNumber = (ViewNumber)objectInputStream.readObject();
            int number = viewNumber.getNumber();

            for (int i = 0; i < number; i++) {
                ViewSaveBean viewSaveBean = (ViewSaveBean) objectInputStream.readObject();
                Logger.i( "file2Bean: "+viewSaveBean.toString());

                viewSaveBeanArrayList.add(viewSaveBean);
            }

            return viewSaveBeanArrayList;

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
     * 将Bean转换为View
     * @param viewSaveBeans
     * @return
     */
    public static ArrayList<View> bean2ViewTrans(Context context,ArrayList<ViewSaveBean> viewSaveBeans){
        ArrayList<View> views = new ArrayList<>();

        for (ViewSaveBean viewSaveBean : viewSaveBeans){
            View view = ViewTransUtils.viewTransfer(context,viewSaveBean.getViewContent().getName());
            view.setX(viewSaveBean.getViewInfo().getX());
            view.setY(viewSaveBean.getViewInfo().getY());

            int dpWidth = viewSaveBean.getViewInfo().getWidth();
            int dpHeight = viewSaveBean.getViewInfo().getHeight();

            int pxWidth = ViewUnitConventUtils.dp2px(dpWidth);
            int pxHeight = ViewUnitConventUtils.dp2px(dpHeight);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    pxWidth,
                    pxHeight);
            view.setLayoutParams(params);

            int backColor = viewSaveBean.getViewResource().getBackColor();

            if (-1 != backColor){
                view.setBackgroundColor(Color.parseColor("#"+backColor));
            }

            String backImage = viewSaveBean.getViewResource().getBackImage();
            if (!TextUtils.isEmpty(backImage)){
                view.setBackgroundColor(Color.parseColor(backImage));
            }
            String text = viewSaveBean.getViewContent().getContent();
            if (!TextUtils.isEmpty(text)){
                ((TextView)view ).setText(text);
            }

            views.add(view);
        }
        return views;
    }


    /**
     * 获取某一点的颜色
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

        return GBData.getColor(x,y);

    }

    /**
     * 获取view坐标
     * @param view
     * @return
     */
    public static int[] getXY(View view){
        //获取坐标
        int[] locations = new int[2];
        view.getLocationOnScreen(locations);
        return locations;
    }

    /**
     * 获取view 宽高
     * @param view
     * @return
     */
    public static int[] getWH(View view){
        int[] wh = new int[2];
        //获取宽、高
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        wh[0] = view.getMeasuredWidth();
        wh[1] = view.getMeasuredHeight();
        return wh;
    }


}
