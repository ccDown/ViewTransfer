package com.soullistener.viewtransfer.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author kuan
 * Created on 2018/12/20.
 * @description
 */
public class ViewTreeBean implements Serializable {

    /**
     * 子view
     */
    private ArrayList<ViewTreeBean> viewTreeBeans;


    /**
     * 本view信息
     */
    private ViewSaveBean viewSaveBean;

    public ViewTreeBean() {
    }

    public ViewTreeBean(ArrayList<ViewTreeBean> viewTreeBeans, ViewSaveBean viewSaveBean) {
        this.viewTreeBeans = viewTreeBeans;
        this.viewSaveBean = viewSaveBean;
    }

    public ArrayList<ViewTreeBean> getViewTreeBeans() {
        return viewTreeBeans;
    }

    public ViewSaveBean getViewSaveBean() {
        return viewSaveBean;
    }

    public int getChildViewCount() {
        return viewTreeBeans != null ? viewTreeBeans.size() : 0;
    }

    public void setViewSaveBean(ViewSaveBean viewSaveBean) {
        this.viewSaveBean = viewSaveBean;
    }

    public void setViewTreeBeans(ArrayList<ViewTreeBean> viewTreeBeans) {
        this.viewTreeBeans = viewTreeBeans;
    }

    public void printViewTreeBean() {

    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ViewTreeBean ChildViewCount:"+getChildViewCount()+",");
        stringBuilder.append(viewSaveBean.toString());
        if (viewTreeBeans != null && viewTreeBeans.size() > 0) {
            for (ViewTreeBean viewTreeBean : viewTreeBeans) {
                stringBuilder.append(viewTreeBean.toString());
            }
        }

        return stringBuilder.toString();
    }
}
