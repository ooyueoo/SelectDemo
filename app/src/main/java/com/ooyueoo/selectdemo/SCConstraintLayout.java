package com.ooyueoo.selectdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
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
public class SCConstraintLayout extends ConstraintLayout {

    private static final String TAG = SCConstraintLayout.class.getSimpleName();

    private boolean isInit=false;

    private ArrayList<View> targetViewS=new ArrayList<>();

    private Map checkBoxHashMap=new HashMap<String, CheckBox>();

    private Map editTextHashMap=new HashMap<String, EditText>();

    private Map strLengthMap=new HashMap<String,Integer>();

    public SCConstraintLayout(Context context) {
        super(context);
    }

    public SCConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SCConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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

    /**
     *   //初始化 存储view的属性信息以及添加checkbox和edittext的监听
     */
    private void getInfoAndAddListener() {

        for (int i = 0; i < getChildCount(); i++) {

            View childView = getChildAt(i);

            String tag="Tag"+i;

            childView.setTag(tag);

            ViewGroup.LayoutParams layoutParams1 = childView.getLayoutParams();

            if(layoutParams1 instanceof SCConstraintLayout.MyLayoutParam ){

                MyLayoutParam myLayoutParam = (MyLayoutParam) layoutParams1;

                if(childView instanceof CheckBox){

                    CheckBox checkBox = (CheckBox) childView;

                    boolean mustChoose = myLayoutParam.mustChoose;

                    if(mustChoose){

                        checkBoxHashMap.put(tag,checkBox);

                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                // TODO: 2019-12-09  1判断当前checkbox是否符合规则 2讲结果存入规则数组 3根据规则数组处理结果

                                queryJudgment();

                            }
                        });
                    }
                }

                if(childView instanceof EditText ){

                    EditText editText = (EditText) childView;

                    boolean mustChoose = myLayoutParam.mustChoose;

                    int strMiniLength = myLayoutParam.strMiniLength;

                    if(mustChoose){

                        editTextHashMap.put(tag,editText);

                        strLengthMap.put(tag,strMiniLength);

                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                                queryJudgment();

                            }
                        });
                    }


                }

                if(childView instanceof Button && !(childView instanceof CheckBox) ){

                    boolean targetBtn = myLayoutParam.targetView;

                    if(targetBtn){

                        targetViewS.add(childView);

                    }

                }

            }

        }

        isInit=true;

    }

    /**
     * 查询判断进行View的设置
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
     */
    private boolean queryView() {

        boolean ret=false;

        for (Object obj : checkBoxHashMap.keySet()) {

            String key = (String) obj;

            CheckBox checkBox = (CheckBox) checkBoxHashMap.get(key);

            if(checkBox.isChecked()==false){
                return false;
            }
        }

        for (Object obj : editTextHashMap.keySet()) {

            String key = (String) obj;

            EditText editText = (EditText) editTextHashMap.get(key);

            int strMiniLength = (int) strLengthMap.get(key);

            String dataContent = editText.getText().toString();

            if(strMiniLength>0){

                if (dataContent.length() < strMiniLength) {

                    return false;
                }

            }else {
                if(TextUtils.isEmpty(dataContent)){
                    return false;
                }
            }

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
        return p instanceof SCConstraintLayout.MyLayoutParam;
    }


    //当在xml布局中加载的时候不会调用这个方法
    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {

        return new SCConstraintLayout.MyLayoutParam(p);

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {

        return new SCConstraintLayout.MyLayoutParam(getContext(),attrs);

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

        public MyLayoutParam(SCConstraintLayout.MyLayoutParam source) {
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
