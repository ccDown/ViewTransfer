package com.soullistener.viewtransfer;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * @author kuan
 * Created on 2018/12/18.
 * @description
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
            String content = ((TextView)childView).getText().toString();
            String name = childView.getClass().getName();

            //获取宽、高
            childView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            int width = childView.getMeasuredWidth();
            int height = childView.getMeasuredHeight();

            //获取坐标
            int[] locations = new int[2];
            childView.getLocationOnScreen(locations);
            int x = locations[0];
            int y = locations[1];

            ViewSaveBean.ViewInfo  viewInfo = new ViewSaveBean.ViewInfo(x,y,width,height);
            ViewSaveBean viewSaveBean = new ViewSaveBean(name,content,viewInfo);
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
                Log.e("bean2File",viewSaveBean.toString());
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
                Log.e("file2Bean",viewSaveBean.toString());
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
     * 将Bean转换为View
     * @param viewSaveBeans
     * @return
     */
    public static ArrayList<View> bean2ViewTrans(Context context,ArrayList<ViewSaveBean> viewSaveBeans){
        ArrayList<View> views = new ArrayList<>();

        for (ViewSaveBean viewSaveBean : viewSaveBeans){
            View view = ViewTransUtils.viewTransfer(context,viewSaveBean.getName());
            view.setX(viewSaveBean.getViewInfo().getX());
            view.setY(viewSaveBean.getViewInfo().getY());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    viewSaveBean.getViewInfo().getWidth(),
                    viewSaveBean.getViewInfo().getHeight());
            view.setLayoutParams(params);

            if (!TextUtils.isEmpty(viewSaveBean.getContent())){
                ((TextView)view ).setText(viewSaveBean.getContent());
            }

            views.add(view);
        }
        return views;
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

            Log.e("viewTransfer",constructor.newInstance(context).getClass().getName());

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
}
