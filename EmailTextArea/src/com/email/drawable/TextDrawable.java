package com.email.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.WindowManager;

import com.email.global.DefaultGlobal;
import com.email.util.PxConvertUtil;

/**
 * 
 * @author tangjiabing
 * 
 * @see 开源时间：2016年03月31日
 * 
 *      记得给我个star哦~
 * 
 */
public class TextDrawable extends Drawable {

	private Context mContext;
	private TextPaint mPaint;
	private String mContent;

	private int mMaxWidth;
	private int mMaxHeight;
	private int mDrawableWidth;
	private int mDrawableHeight;
	private int mContentWidth;
	private int mContentHeight;

	private int mPaddingLeft;
	private int mPaddingRight;
	private int mPaddingTop;
	private int mPaddingBottom;

	private int mRoundRectRadiusX;
	private int mRoundRectRadiusY;

	private int mMarginLeft;
	private int mMarginRight;
	private int mMarginTop;
	private int mMarginBottom;

	private int mTextSize;
	private float mSpacingMult;
	private float mSpacingAdd;

	private int mTextBgColor;
	private int mTextFgColor;

	public TextDrawable(Context context, String text) {
		this(context, text, ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth(), ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getHeight());
	}

	public TextDrawable(Context context, String text, int maxWidth,
			int maxHeight) {
		mContext = context;
		mContent = text;
		mMaxWidth = maxWidth;
		mMaxHeight = maxHeight;

		mPaddingLeft = PxConvertUtil.dp2px(mContext,
				DefaultGlobal.ROUND_RECT_PADDING_LEFT);
		mPaddingRight = PxConvertUtil.dp2px(mContext,
				DefaultGlobal.ROUND_RECT_PADDING_RIGHT);
		mPaddingTop = PxConvertUtil.dp2px(mContext,
				DefaultGlobal.ROUND_RECT_PADDING_TOP);
		mPaddingBottom = PxConvertUtil.dp2px(mContext,
				DefaultGlobal.ROUND_RECT_PADDING_BOTTOM);

		mRoundRectRadiusX = PxConvertUtil.dp2px(mContext,
				DefaultGlobal.ROUND_RECT_RADIUS_X);
		mRoundRectRadiusY = PxConvertUtil.dp2px(mContext,
				DefaultGlobal.ROUND_RECT_RADIUS_Y);

		mMarginLeft = PxConvertUtil.dp2px(mContext,
				DefaultGlobal.ROUND_RECT_MARGIN_LEFT);
		mMarginRight = PxConvertUtil.dp2px(mContext,
				DefaultGlobal.ROUND_RECT_MARGIN_RIGHT);
		mMarginTop = PxConvertUtil.dp2px(mContext,
				DefaultGlobal.ROUND_RECT_MARGIN_TOP);
		mMarginBottom = PxConvertUtil.dp2px(mContext,
				DefaultGlobal.ROUND_RECT_MARGIN_BOTTOM);

		mTextSize = PxConvertUtil.sp2px(mContext, DefaultGlobal.TEXT_SIZE);
		mSpacingMult = DefaultGlobal.SPACING_MULT;
		mSpacingAdd = DefaultGlobal.SPACING_ADD;

		mTextBgColor = DefaultGlobal.VALID_TEXT_BG_COLOR;
		mTextFgColor = DefaultGlobal.TEXT_FG_COLOR;

		initPaint();
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.save();

		mPaint.setColor(Color.TRANSPARENT);
		Rect rect = new Rect(0, 0, mDrawableWidth, mDrawableHeight);
		canvas.drawRect(rect, mPaint);

		mPaint.setColor(mTextBgColor);
		RectF rectf = new RectF(mMarginLeft, mMarginTop, mDrawableWidth
				- mMarginRight, mDrawableHeight - mMarginBottom);
		canvas.drawRoundRect(rectf, mRoundRectRadiusX, mRoundRectRadiusY,
				mPaint);

		canvas.translate(mMarginLeft + mPaddingLeft, mMarginTop + mPaddingTop);
		canvas.clipRect(0, 0, mContentWidth, mContentHeight);

		mPaint.setColor(mTextFgColor);
		StaticLayout layout = new StaticLayout(mContent, mPaint, mContentWidth,
				Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, true);
		layout.draw(canvas);

		canvas.restore();
	}

	@Override
	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mPaint.setColorFilter(cf);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSPARENT;
	}

	@Override
	public int getIntrinsicWidth() {
		int textWidth = (int) (StaticLayout.getDesiredWidth(mContent, mPaint) + 0.5f);
		int width = 0;
		if (textWidth + mPaddingLeft + mPaddingRight + mMarginLeft
				+ mMarginRight > mMaxWidth) {
			width = mMaxWidth;
			mContentWidth = width - mPaddingLeft - mPaddingRight - mMarginLeft
					- mMarginRight;
		} else {
			width = textWidth + mPaddingLeft + mPaddingRight + mMarginLeft
					+ mMarginRight;
			mContentWidth = textWidth;
		}
		return width;
	}

	@Override
	public int getIntrinsicHeight() {
		StaticLayout layout = new StaticLayout(mContent, mPaint, mContentWidth,
				Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, true);
		int textHeight = layout.getHeight();
		int height = 0;
		if (textHeight + mPaddingTop + mPaddingBottom + mMarginTop
				+ mMarginBottom > mMaxHeight) {
			height = mMaxHeight;
			mContentHeight = height - mPaddingTop - mPaddingBottom - mMarginTop
					- mMarginBottom;
		} else {
			height = textHeight + mPaddingTop + mPaddingBottom + mMarginTop
					+ mMarginBottom;
			mContentHeight = textHeight;
		}
		return height;
	}

	// *****************************************************************
	// 公有方法

	public void setBounds() {
		mPaint.setTextSize(mTextSize);
		mDrawableWidth = getIntrinsicWidth();
		mDrawableHeight = getIntrinsicHeight();
		setBounds(0, 0, mDrawableWidth, mDrawableHeight);
	}

	public void setPadding(int left, int right, int top, int bottom) {
		mPaddingLeft = PxConvertUtil.dp2px(mContext, left);
		mPaddingRight = PxConvertUtil.dp2px(mContext, right);
		mPaddingTop = PxConvertUtil.dp2px(mContext, top);
		mPaddingBottom = PxConvertUtil.dp2px(mContext, bottom);
	}

	public void setMargin(int left, int right, int top, int bottom) {
		mMarginLeft = PxConvertUtil.dp2px(mContext, left);
		mMarginRight = PxConvertUtil.dp2px(mContext, right);
		mMarginTop = PxConvertUtil.dp2px(mContext, top);
		mMarginBottom = PxConvertUtil.dp2px(mContext, bottom);
	}

	public void setTextBgColor(int color) {
		mTextBgColor = color;
	}

	public void setTextFgColor(int color) {
		mTextFgColor = color;
	}

	public void setTextSize(int size) {
		mTextSize = PxConvertUtil.sp2px(mContext, size);
	}

	public void setRoundRectRadius(int x, int y) {
		mRoundRectRadiusX = PxConvertUtil.dp2px(mContext, x);
		mRoundRectRadiusY = PxConvertUtil.dp2px(mContext, y);
	}

	public void setLineSpacing(float spacingMult, float spacingAdd) {
		mSpacingMult = spacingMult;
		mSpacingAdd = spacingAdd;
	}

	// *****************************************************************
	// 私有方法

	private void initPaint() {
		mPaint = new TextPaint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
	}

}
