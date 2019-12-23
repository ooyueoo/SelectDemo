package com.ooyueoo.selectdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
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

/**
 *
 */
public class SCConstraintLayout_3 extends ConstraintLayout {

    private static final String TAG = SCConstraintLayout_3.class.getSimpleName();

    private boolean isInit = false;

    private int[] statusArray;

    private int STATUS_OFF = 0;

    private int STATUS_ON = 1;

    private ArrayList<View> targetViewS = new ArrayList<>();


    public SCConstraintLayout_3(Context context) {
        super(context);
    }

    public SCConstraintLayout_3(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SCConstraintLayout_3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed, left, top, right, bottom);

        if (!isInit) {

            getInfoAndAddListener();

            isInit = true;
        }

    }


    /**
     * //初始化
     * <p>
     * 1.定义int数组 数组长度是子view的个数 数组初始状态都是 0
     * 2.遍历子view， 如果属性mustchoose为false，  则对应数组的状态置为1 。 如果属性target为true，状态置为1
     * 3.当对应checkbox 的状态改变后 存储在数组中 选择置为1 非选择置为0  然后去查询判断
     * 4.当对应edittex的状态改变后 判断是否为空且字符长度是否达到最小长度  如果满足 数组置为1 不满足 置为0 然后查询判断
     * 5.查询判断的时候 如果传入false 直接改变targetview为不可用
     * 6.查询判断，如果传入true，查看数组中如果值都为1则满足，如果有0则不满足，然后根据返回结果去改变targetView是否可用
     */
    private void getInfoAndAddListener() {

        int childCount = getChildCount();

        Log.e(TAG, "getInfoAndAddListener: "+childCount );
        statusArray = new int[childCount];

        for (int i = 0; i < childCount; i++) {

            View childView = getChildAt(i);

            final int finalIndex = i;

            ViewGroup.LayoutParams layoutParams = childView.getLayoutParams();

            if (layoutParams instanceof SCConstraintLayout_3.MyLayoutParam) {

                MyLayoutParam myLayoutParam = (MyLayoutParam) layoutParams;

                boolean mustChoose = myLayoutParam.mustChoose;

                if (mustChoose) {

                    if (childView instanceof CheckBox) {

                        CheckBox checkBox = (CheckBox) childView;

                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                                statusArray[finalIndex] = b ? STATUS_ON : STATUS_OFF;

                                queryJudgment(b);

                            }
                        });
                    }else if (childView instanceof EditText) {

                        EditText editText = (EditText) childView;

                        final int strMiniLength = myLayoutParam.strMiniLength;

                        editText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                //
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                               //
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                                int status = STATUS_OFF;

                                String str = editable.toString();

                                int strLength = str.length();

                                if (strLength >= strMiniLength) {

                                    status = STATUS_ON;

                                }

                                statusArray[finalIndex] = status;

                                queryJudgment(status == STATUS_ON);

                            }
                        });

                    }else if (childView instanceof Button && !(childView instanceof CheckBox)) {

                        boolean targetView = myLayoutParam.targetView;

                        if (targetView) {

                            statusArray[finalIndex] = STATUS_ON;

                            targetViewS.add(childView);

                        }

                    }

                } else {

                    statusArray[finalIndex] = STATUS_ON;

                }

            }

        }


    }

    /**
     * 查询判断进行View的设置
     *
     * @param
     * @param result
     */
    private void queryJudgment(boolean result) {

        if (result) {

            result = queryView();

        }

        targetViewEnable(result);

    }

    /**
     * 判断是否满足条件
     *
     * @param
     * @return
     */
    private boolean queryView() {

        for (int status : statusArray) {

            if (status != STATUS_ON) return false;

        }

        return true;
    }

    /**
     * 根据判断去设置targetView 是否可用
     *
     * @param enable
     */
    private void targetViewEnable(boolean enable) {

        if (targetViewS.size() < 1) return;

        for (View targetView : targetViewS) {

            targetView.setEnabled(enable);

        }

    }


    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof SCConstraintLayout_3.MyLayoutParam;
    }


    /**
     * 当在xml布局中加载的时候不会调用这个方法
     *
     * @param p
     * @return
     */
    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {

        return new SCConstraintLayout_3.MyLayoutParam(p);

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {

        return new SCConstraintLayout_3.MyLayoutParam(getContext(), attrs);

    }

    public class MyLayoutParam extends LayoutParams {

        //是否是必须选择的checkbox 或者是edittext
        private boolean mustChoose;

        //最终要设置是否可用的view
        private boolean targetView;

        //针对edittext，是否对数据长度有要求 0 或者  大于限定长度的输入
        private int strMiniLength;

        public MyLayoutParam(Context context, AttributeSet attrs) {

            super(context, attrs);

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SCConstraintLayout);

            mustChoose = a.getBoolean(R.styleable.SCConstraintLayout_mustChoose, false);

            targetView = a.getBoolean(R.styleable.SCConstraintLayout_targetView, false);

            strMiniLength = a.getInteger(R.styleable.SCConstraintLayout_strMiniLength, 0);

            a.recycle();
        }

        public MyLayoutParam(int width, int height) {
            super(width, height);
        }

        public MyLayoutParam(SCConstraintLayout_3.MyLayoutParam source) {
            super(source);

        }

        public MyLayoutParam(ViewGroup.LayoutParams source) {
            super(source);
        }
    }


}


//注意事项：
//1.
//attrs.xml
//<declare-styleable name="SCConstraintLayout">
//<attr name="mustChoose" format="boolean" ></attr>
//<attr name="targetView" format="boolean"></attr>  如果是targetView 那么必须添加  mustChoose=true
//<attr name="strMiniLength" format="integer"></attr>
//</declare-styleable>


//2.  如果不是checkbox edittext , 不添加mustChoose 或者 mustChoose=fase
//如果是targetView 那么必须添加  mustChoose=true




