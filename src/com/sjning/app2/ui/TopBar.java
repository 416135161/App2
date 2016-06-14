package com.sjning.app2.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sjning.app2.R;
import com.sjning.app2.intrface.TopBarClickListener;

public class TopBar extends RelativeLayout {
	private Button leftBtn;
	private ImageButton rightBtn;
	private TextView title;
	private TopBarClickListener topBarClickListener;
	private String titleStr;

	private RelativeLayout.LayoutParams leftBtnLayoutParams, titleLayoutParams,
			rightBtnLayoutParams;
	private static int LEFT_BTN_ID = 1;
	private static int TITLE_ID = 2;
	private static int RIGHT_BTN_ID = 3;

	private Drawable leftBackground;
	private int titleTextColor;

	public TopBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.TopBar);
		this.titleStr = ta.getString(R.styleable.TopBar_title);
		this.leftBackground = ta.getDrawable(R.styleable.TopBar_leftBackground);
		this.titleTextColor = ta.getColor(R.styleable.TopBar_titleTextColor, 0);

		ta.recycle();

		leftBtn = new Button(context);
		title = new TextView(context);
		rightBtn = new ImageButton(context);

		leftBtn.setId(LEFT_BTN_ID);
		title.setId(TITLE_ID);
		rightBtn.setId(RIGHT_BTN_ID);

		leftBtnLayoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		titleLayoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		rightBtnLayoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		leftBtnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
				RelativeLayout.TRUE);
		leftBtnLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL,
				RelativeLayout.TRUE);

		//titleLayoutParams.addRule(RelativeLayout.LEFT_OF, RIGHT_BTN_ID);
		titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, LEFT_BTN_ID);

		titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT,
				RelativeLayout.TRUE);
		rightBtnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
				RelativeLayout.TRUE);
		rightBtnLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL,
				RelativeLayout.TRUE);

		leftBtn.setBackgroundDrawable(leftBackground);
		// leftBtn.setGravity(Gravity.CENTER);
		int left = context.getResources().getDimensionPixelSize(
				R.dimen.backbtn_paddingleft);
		int right = context.getResources().getDimensionPixelSize(
				R.dimen.backbtn_paddingright);
		int top = context.getResources().getDimensionPixelSize(
				R.dimen.backbtn_paddingtop);
		int bottom = context.getResources().getDimensionPixelSize(
				R.dimen.backbtn_paddingbottom);
		leftBtn.setPadding(left, top, right, bottom);
		leftBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources()
				.getDimensionPixelSize(R.dimen.back_btn_font_size));
		leftBtn.setTextColor(context.getResources().getColor(
				R.color.white_color));
		leftBtn.setVisibility(View.GONE);
		leftBtn.setText("返回");

		rightBtn.setVisibility(View.GONE);
		rightBtn.setBackgroundColor(0x00ffffff);

		title.setEllipsize(TruncateAt.MIDDLE);
		title.setGravity(Gravity.CENTER_HORIZONTAL);
		title.setSingleLine(true);
		title.setText(titleStr);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources()
				.getDimensionPixelSize(R.dimen.title_font_size));
		title.setTextColor(titleTextColor);
		title.setGravity(Gravity.CENTER);

		addView(leftBtn, leftBtnLayoutParams);
		addView(title, titleLayoutParams);
		addView(rightBtn, rightBtnLayoutParams);
		leftBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (topBarClickListener != null) {
					topBarClickListener.leftBtnClick();
				}
			}
		});

		rightBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (topBarClickListener != null) {
					topBarClickListener.rightBtnClick();
				}
			}
		});

	}

	public void setTopBarClickListener(TopBarClickListener topBarClickListener) {
		this.topBarClickListener = topBarClickListener;
	}


	public void setTitle(String title) {
		this.title.setText(title);
	}

	
	public void hiddenLeftButton(boolean b) {
		if (b) {
			leftBtn.setVisibility(View.GONE);
		} else {
			leftBtn.setVisibility(View.VISIBLE);
		}
	}

	// �����Ұ�ť
	public void hiddenRightButton(boolean b) {
		if (b) {
			rightBtn.setVisibility(View.GONE);
		} else {
			rightBtn.setVisibility(View.VISIBLE);
		}
	}

	
	public void setLeftDrawable(int id) {
		this.leftBtn.setBackgroundResource(id);
	}

	public void setLeftText(String text) {
		leftBtn.setText(text);
	}


	public void setRightDrawable(int id) {
		this.rightBtn.setImageResource(id);
	}

	public void destroySelf() {
		this.setVisibility(View.INVISIBLE);
	}
}