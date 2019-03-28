# ViewTransfer View传输 #
需求:实现一个页面View的传输到另一个页面，实现跨进程通讯
--------------------------------------------------------------------

##2018-11-23

1.实现界面ContentView的保存与传递？实现XML动态加载布局（思路不正确，改变实现形式）

2.跨进程通讯可选用: AIDL、文件、socket

--------------------------------------------------------------------

##2018-11-25
通过saveInstanceState实现界面的传输？（思路不正确，改变实现形式）

--------------------------------------------------------------------

##2018-12-18  
界面通过对view关键性信息进行保存，如控件包名 控件背景 宽 高 等信息。
对1进行思路修改:

1.自定义协议进行View相关内容的保存（通过链表以多叉树的形式保存）

2.将对象输出到文件目录（文件共享形式实现跨进程）

3.进行文件目录的传输

4.进行文件目录的读取并通过先序遍历还原为View

5.将View根据Tree的形式添加到界面中（递归遍历添加）

6.屏幕适配

--------------------------------------------------------------

##2018-12-21
>对1进行思路另一种实现形式:通过XML实现界面的复制？

通过view生成xml文件
动态加载xml文件生成view
--------------------------------------------------------------

#遇到的问题:

1、在Activity的OnCreate()事件输出view的x、y、height、width等参数全为0，要等UI控件都加载完了才能获取到这些数据。

            //直接调用textview.measuredWidth,textview.measuredHeight只能得到0，需首先调用下列方法
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
详情:https://www.jianshu.com/p/d18f0c96acb8

2、将数据类实现Serializable接口进行存储

3、权限动态申请

4、获取全部子view

     /**
     * 通过递归获取全部的子view
     * @param parentView
     * @return
     */
    public static ArrayList<View> getAllChildViews(View parentView){
        ArrayList arrayList = new ArrayList();

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
    
5、view无法直接进行width和height的设置，需要使用LayoutParams进行转化

6、自定义View通过findviewbyid返回为null解决方法原因是在自定义View的时候自动生成了一个只有一个带context的构造函数，而通过XML文件创建的view对象为两个参数即（context,attr）的对象，手动添加第二个构造函数之后，没有把super里面的第二个参数加上。

7、DecoreView子view包含title和contentView

8、view的背景颜色通过获取坐标？

9、通过反射创建自定义view的时候失败

原因:代码中反射创建时参数个数不正确

10、获取ViewGroup的宽度和高度不正确？

--------------------------------------------------------------
