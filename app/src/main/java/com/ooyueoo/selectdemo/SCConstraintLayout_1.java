package com.ooyueoo.selectdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 说明
 * 作用：当所有必需要进行选择的CheckBox选择了、必需输入的Edittext输入了限定长度的内容后
 * 使功能按钮可用
 *
 */
public class SCConstraintLayout_1 extends ConstraintLayout {

    private static final String TAG = SCConstraintLayout_1.class.getSimpleName();

    private boolean isInit=false;

    private ArrayList<View> targetViewS=new ArrayList<>();

    public SCConstraintLayout_1(Context context) {
        super(context);
    }

    public SCConstraintLayout_1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SCConstraintLayout_1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed, left, top, right, bottom);

        if(!isInit){

            getInfoAndAddListener();

        }

    }


    private int[] status;

    /**
     *   //初始化 存储view的属性信息以及添加checkbox和edittext的监听
     */
    private void getInfoAndAddListener() {

        final int childCount = getChildCount();

        status=new int[childCount];

        for (int i = 0; i < childCount; i++) {

            View childView = getChildAt(i);

            final int finalI = i;

            ViewGroup.LayoutParams layoutParams1 = childView.getLayoutParams();

            if(layoutParams1 instanceof SCConstraintLayout_1.MyLayoutParam ){

                MyLayoutParam myLayoutParam = (MyLayoutParam) layoutParams1;



                if(childView instanceof CheckBox){

                    CheckBox checkBox = (CheckBox) childView;

                    boolean mustChoose = myLayoutParam.mustChoose;

                    if(mustChoose){

                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                // TODO: 2019-12-09 1判断当前checkbox是否符合规则 2讲结果存入规则数组 3根据规则数组处理结果
                                if(b){

                                    status[finalI]=1;

                                }else {

                                    status[finalI]=0;

                                }

                                queryJudgment();


                            }
                        });
                    }else {

                        status[i]=1;

                    }
                }else if(childView instanceof EditText ){

                    EditText editText = (EditText) childView;

                    boolean mustChoose = myLayoutParam.mustChoose;

                    final int strMiniLength = myLayoutParam.strMiniLength;

                    if(mustChoose){


                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                                String str = editable.toString();

                                int strLength = str.length();


                                if(strMiniLength!=0){

                                    if(strLength>=strMiniLength){

                                        status[finalI]=1;

                                    }else {

                                        status[finalI]=0;

                                    }
                                }else {
                                    if(strLength==0){

                                        status[finalI]=0;

                                    }else {

                                        status[finalI]=1;

                                    }
                                }

                                queryJudgment();

                            }
                        });
                    }else {
                        status[finalI]=1;
                    }


                }else if(childView instanceof Button && !(childView instanceof CheckBox) ){

                    status[finalI]=1;


                    boolean targetBtn = myLayoutParam.targetView;

                    if(targetBtn){

                        targetViewS.add(childView);

                    }

                }else {
                    status[finalI]=1;
                }



            }

        }

        isInit=true;

    }

    /**
     * 查询判断进行View的设置
     * @param
     */
    private void queryJudgment() {

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {

            boolean enable = queryView();

            targetViewEnable(enable);

        }


        long endTime = System.currentTimeMillis();

        Log.e(TAG, "queryJudgment: "+(endTime-startTime) );

    }

    /**
     * 判断是否满足条件
     * @return
     * @param
     */
    private boolean queryView() {

        boolean ret=false;

        for (int i = 0; i < status.length; i++) {

            int status = this.status[i];

            if(status!=1) return false;

        }

        ret=true;

        return ret;
    }

    /**
     * 根据判断去设置targetView 是否可用
     * @param enable
     */
    private void targetViewEnable(boolean enable){

        int size = targetViewS.size();

        if(size>0){

            for (int i = 0; i < size; i++) {

                    targetViewS.get(i).setEnabled(enable);

            }
        }

    }


    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof SCConstraintLayout_1.MyLayoutParam;
    }


    //当在xml布局中加载的时候不会调用这个方法
    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {

        return new SCConstraintLayout_1.MyLayoutParam(p);

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {

        return new SCConstraintLayout_1.MyLayoutParam(getContext(),attrs);

    }

    public class MyLayoutParam extends LayoutParams{

        private boolean mustChoose ; //是否是必须选择的checkbox 或者是edittext

        private boolean targetView; //最终要设置是否可用的view

        private int strMiniLength;  //针对edittext，是否对数据长度有要求 0 或者  大于限定长度的输入

        public MyLayoutParam(Context context, AttributeSet attrs) {

            super(context, attrs);

            TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.SCConstraintLayout);

            mustChoose = a.getBoolean(R.styleable.SCConstraintLayout_mustChoose,false);

            targetView = a.getBoolean(R.styleable.SCConstraintLayout_targetView,false);

            strMiniLength = a.getInteger(R.styleable.SCConstraintLayout_strMiniLength,0);

            a.recycle();
        }

        public MyLayoutParam(int width, int height) {
            super(width, height);
        }

        public MyLayoutParam(SCConstraintLayout_1.MyLayoutParam source) {
            super(source);

        }

        public MyLayoutParam(ViewGroup.LayoutParams source){
            super(source);
        }
    }


}


//
//attrs.xml
//<declare-styleable name="SCConstraintLayout">
//<attr name="mustChoose" format="boolean" ></attr>
//<attr name="targetBtn" format="boolean"></attr>
//<attr name="strMiniLength" format="integer"></attr>
//</declare-styleable>
