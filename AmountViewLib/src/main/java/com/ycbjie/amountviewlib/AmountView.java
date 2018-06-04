package com.ycbjie.amountviewlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * <pre>
 *     @author yangchong
 *     blog  :
 *     time  : 2018/05/30
 *     desc  : 购物车自定义加减控件【组合控件】
 *     revise:
 * </pre>
 */
public class AmountView extends LinearLayout implements View.OnClickListener {

    private ImageView mIvSubtract;
    private EditText mEtAmount;
    private ImageView mIvAdd;
    private Context mContext;

    /**
     * 初始化时购买数量
     */
    private int amount = 1;
    /**
     * 商品库存，最大值
     */
    private int max_num = 1;
    /**
     * 最少购买数
     */
    private int min_num = 1;


    public AmountView(Context context) {
        this(context, null);
    }

    public AmountView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AmountView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 做初始化的操作
     * @param context           上下文
     */
    private void init(Context context) {
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.amount_view, this);
        mIvSubtract = findViewById(R.id.iv_subtract);
        mEtAmount = findViewById(R.id.et_amount);
        mIvAdd = findViewById(R.id.iv_add);

        mIvSubtract.setOnClickListener(this);
        mIvAdd.setOnClickListener(this);
        mEtAmount.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_subtract) {
            subtract();
        } else if (i == R.id.iv_add) {
            add();
        } else {
            Log.e("AmountView","其他");
        }
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.e("AmountView","beforeTextChanged");
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.e("AmountView","onTextChanged");
        }

        @Override
        public void afterTextChanged(Editable s) {
            //一定要添加这句话，否则会报StackOverflowError: stack size 8MB
            //先添加remove，然后也需要添加add
            mEtAmount.removeTextChangedListener(this);
            Log.e("AmountView","afterTextChanged");
            //注意不能为空
            if(mEtAmount.getText().toString().length() == 0){
                return;
            }
            if(Integer.valueOf(mEtAmount.getText().toString()) > max_num){
                mEtAmount.setText(String.valueOf(max_num));
                amount = max_num;
            }else if(Integer.valueOf(mEtAmount.getText().toString())<min_num){
                mEtAmount.setText(String.valueOf(min_num));
                amount = min_num;
            }else{
                //这句话会报错
                amount = Integer.valueOf(mEtAmount.getText().toString());
                mEtAmount.setText(String.valueOf(amount));
            }
            //将光标移到最后
            mEtAmount.setSelection(mEtAmount.getText().length());
            mEtAmount.addTextChangedListener(this);
        }
    };


    /**
     * 做减法操作
     */
    private void subtract() {
        mEtAmount.removeTextChangedListener(textWatcher);
        if(amount <= min_num){
            showToast(mContext,"购买数量最少是一");
            return;
        }
        amount--;
        mEtAmount.setText(String.valueOf(amount));
        mEtAmount.addTextChangedListener(textWatcher);
    }

    /**
     * 加法操作
     */
    private void add() {
        mEtAmount.removeTextChangedListener(textWatcher);
        if(amount >= max_num){
            showToast(mContext,"购买数量不能超过库存数");
            return;
        }
        amount++;
        mEtAmount.setText(String.valueOf(amount));
        mEtAmount.addTextChangedListener(textWatcher);
    }

    /**
     * 改变状态
     */
    private void changeState(){
        if(amount<=min_num){
            mIvSubtract.setImageResource(R.drawable.icon_subtract_goods_no_click);
            if(amount<max_num){
                mIvAdd.setImageResource(R.drawable.icon_increase_goods_yes_click);
            }else{
                mIvAdd.setImageResource(R.drawable.icon_increase_goods_no_click);
            }
        }else if(amount>=max_num){
            mIvSubtract.setImageResource(R.drawable.icon_subtract_goods_yes_click);
            mIvAdd.setImageResource(R.drawable.icon_increase_goods_no_click);
        }else{
            mIvSubtract.setImageResource(R.drawable.icon_subtract_goods_yes_click);
            mIvAdd.setImageResource(R.drawable.icon_increase_goods_yes_click);
        }
        mEtAmount.setSelection(mEtAmount.getText().toString().length());
    }



    /*----------------------------------------用户设置方法--------------------------------------*/

    /**
     * 设置购物车加减控件的参数，必须设置
     * @param amount                默认值
     * @param max_num               最大值
     * @param min_num               最小值
     */
    public void setAmountNum(int amount, int max_num , int min_num){
        this.amount = amount;
        this.max_num = max_num;
        this.min_num = min_num;
        mEtAmount.setText(String.valueOf(amount));
        changeState();
    }

    /**
     * 获取当前购物车的数量
     * @return                      当前购物车数量
     */
    public int getAmount(){
        return amount;
    }

    /**
     * 吐司工具类    避免点击多次导致吐司多次，最后导致Toast就长时间关闭不掉了
     * @param context       注意：这里如果传入context会报内存泄漏；传递activity.getApplicationContext()
     * @param content       吐司内容
     */
    private Toast toast;
    @SuppressLint("ShowToast")
    private void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }



}

