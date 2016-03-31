package com.email.view;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.email.drawable.TextDrawable;
import com.email.entity.ChooseObjEntity;
import com.email.global.DatabaseGlobal;
import com.email.global.DefaultGlobal;
import com.email.res.LayoutRes;
import com.email.res.ViewRes;
import com.email.util.DbUtil;
import com.email.util.ResUtil;

/**
 * 
 * @author tangjiabing
 * 
 * @see 开源时间：2016年03月31日
 * 
 *      记得给我个star哦~
 * 
 */
public class EmailTextArea extends MultiAutoCompleteTextView {

	private int mDirtyTextBgColor;
	private int mDirtySelectedTextBgColor;
	private int mDirtyTextFgColor;
	private int mDirtySelectedTextFgColor;

	private int mValidTextBgColor;
	private int mValidSelectedTextBgColor;
	private int mValidTextFgColor;
	private int mValidSelectedTextFgColor;

	private int mTextSize;
	private int mMinValidLength;
	private boolean mIsAllowInputText;

	private int mRoundRectPaddingLeft;
	private int mRoundRectPaddingRight;
	private int mRoundRectPaddingTop;
	private int mRoundRectPaddingBottom;

	private int mRoundRectRadiusX;
	private int mRoundRectRadiusY;

	private ArrayList<ChooseObjEntity> mChooseObjList;
	private OnDeleteObjListener mDeleteObjListener;

	public EmailTextArea(Context context) {
		this(context, null);
	}

	public EmailTextArea(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.autoCompleteTextViewStyle);
	}

	public EmailTextArea(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		registerListener();
	}

	@Override
	protected void onSelectionChanged(int selStart, int selEnd) {
		Editable editable = getEditableText();
		setSelection(editable.length());
	}

	// ******************************************************************
	// init，registerListener

	private void init() {
		recoverDefaultValues();
		mChooseObjList = new ArrayList<ChooseObjEntity>();
		setThreshold(1);
		// setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		setTokenizer(new CustomTokenizer(DefaultGlobal.SPACE_TOKENIZER));
		AutoCursorAdapter adapter = new AutoCursorAdapter();
		setAdapter(adapter);
		allowInputTextControl();
	}

	private void registerListener() {
		setOnKeyListener(new AutoKeyListener());
		addTextChangedListener(new AutoTextChangedListener());
		setMovementMethod(new AutoLinkMovementMethod());
		setOnFocusChangeListener(new AutoFocusChangeListener());
	}

	// ******************************************************************
	// 公有方法

	public void insertDatas(String[] names, String[] addresses, String[] outKeys) {
		if (names != null && addresses != null && outKeys != null) {
			DbUtil dbUtil = DbUtil.getInstance(getContext());
			String[] fieldNames = new String[] { DatabaseGlobal.FIELD_NAME,
					DatabaseGlobal.FIELD_ADDRESS, DatabaseGlobal.FIELD_OUT_KEY };
			ArrayList<Object[]> dataList = new ArrayList<Object[]>();
			for (int i = 0; i < names.length && i < addresses.length
					&& i < outKeys.length; i++)
				dataList.add(new Object[] { names[i], addresses[i], outKeys[i] });
			dbUtil.insertMult(DatabaseGlobal.TABLE_EMAIL_OBJS, fieldNames,
					dataList);
		}
	}

	public void deleteDatas() {
		DbUtil dbUtil = DbUtil.getInstance(getContext());
		dbUtil.delete(DatabaseGlobal.TABLE_EMAIL_OBJS);
	}

	public void appendContent(String[] names, String[] addresses,
			String[] outKeys) {
		if (names != null && addresses != null && outKeys != null) {
			Editable editable = getEditableText();
			forceAllowInputText();
			for (int i = 0; i < names.length && i < addresses.length
					&& i < outKeys.length; i++) {
				SpannableString sp = getSpannableString(false, names[i],
						addresses[i], null, null);
				editable.append(sp);
				confirmInput(sp, outKeys[i]);
			}
			allowInputTextControl();
		}
	}

	public void setContent(String[] names, String[] addresses, String[] outKeys) {
		clearContent();
		appendContent(names, addresses, outKeys);
	}

	public void clearContent() {
		for (ChooseObjEntity objEntity : mChooseObjList)
			objEntity.isSelected = true;
		int size = mChooseObjList.size();
		for (int i = 0; i < size; i++)
			delete();
		getEditableText().clear();
	}

	public String[] getOutKeys() {
		if (mChooseObjList.isEmpty())
			return null;
		else {
			String[] outKeys = new String[mChooseObjList.size()];
			for (int i = 0; i < mChooseObjList.size(); i++) {
				ChooseObjEntity objEntity = mChooseObjList.get(i);
				outKeys[i] = objEntity.outKey;
			}
			return outKeys;
		}
	}

	public void setAllowInputText(boolean isAllow) {
		mIsAllowInputText = isAllow;
		allowInputTextControl();
	}

	public boolean isAllowInputText() {
		return mIsAllowInputText;
	}

	public void setOnDeleteObjListener(OnDeleteObjListener listener) {
		mDeleteObjListener = listener;
	}

	public void setDirtyTextBgColor(int dirtyTextBgColor,
			int dirtySelectedTextBgColor) {
		mDirtyTextBgColor = dirtyTextBgColor;
		mDirtySelectedTextBgColor = dirtySelectedTextBgColor;
	}

	public void setValidTextBgColor(int validTextBgColor,
			int validSelectedTextBgColor) {
		mValidTextBgColor = validTextBgColor;
		mValidSelectedTextBgColor = validSelectedTextBgColor;
	}

	public void setDirtyTextFgColor(int dirtyTextFgColor,
			int dirtySelectedTextFgColor) {
		mDirtyTextFgColor = dirtyTextFgColor;
		mDirtySelectedTextFgColor = dirtySelectedTextFgColor;
	}

	public void setValidTextFgColor(int validTextFgColor,
			int validSelectedTextFgColor) {
		mValidTextFgColor = validTextFgColor;
		mValidSelectedTextFgColor = validSelectedTextFgColor;
	}

	public void setRoundRectPadding(int left, int top, int right, int bottom) {
		mRoundRectPaddingLeft = left;
		mRoundRectPaddingRight = right;
		mRoundRectPaddingTop = top;
		mRoundRectPaddingBottom = bottom;
	}

	public void setRoundRectRadius(int x, int y) {
		mRoundRectRadiusX = x;
		mRoundRectRadiusY = y;
	}

	public void setTextSizeOfRoundRect(int spSize) {
		mTextSize = spSize;
	}

	public void setMinValidLength(int len) {
		mMinValidLength = len;
	}

	public void recoverDefaultValues() {
		mDirtyTextBgColor = DefaultGlobal.DIRTY_TEXT_BG_COLOR;
		mDirtySelectedTextBgColor = DefaultGlobal.DIRTY_SELECTED_TEXT_BG_COLOR;
		mDirtyTextFgColor = DefaultGlobal.TEXT_FG_COLOR;
		mDirtySelectedTextFgColor = DefaultGlobal.SELECTED_TEXT_FG_COLOR;

		mValidTextBgColor = DefaultGlobal.VALID_TEXT_BG_COLOR;
		mValidSelectedTextBgColor = DefaultGlobal.VALID_SELECTED_TEXT_BG_COLOR;
		mValidTextFgColor = DefaultGlobal.TEXT_FG_COLOR;
		mValidSelectedTextFgColor = DefaultGlobal.SELECTED_TEXT_FG_COLOR;

		mRoundRectPaddingLeft = DefaultGlobal.ROUND_RECT_PADDING_LEFT;
		mRoundRectPaddingRight = DefaultGlobal.ROUND_RECT_PADDING_RIGHT;
		mRoundRectPaddingTop = DefaultGlobal.ROUND_RECT_PADDING_TOP;
		mRoundRectPaddingBottom = DefaultGlobal.ROUND_RECT_PADDING_BOTTOM;

		mRoundRectRadiusX = DefaultGlobal.ROUND_RECT_RADIUS_X;
		mRoundRectRadiusY = DefaultGlobal.ROUND_RECT_RADIUS_Y;

		mTextSize = DefaultGlobal.TEXT_SIZE;
		mMinValidLength = DefaultGlobal.MIN_VALID_LENGTH;
	}

	public void onDestroy() {
		for (ChooseObjEntity objEntity : mChooseObjList) {
			objEntity.drawable.setCallback(null);
			objEntity.drawable = null;
		}
		mChooseObjList.clear();
		mChooseObjList = null;
		DbUtil.closeDb();
	}

	// ******************************************************************
	// 私有方法

	private void forceAllowInputText() {
		setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				DefaultGlobal.MAX_INPUT_LENGTH) });
	}

	private void allowInputTextControl() {
		int len = DefaultGlobal.MAX_INPUT_LENGTH;
		if (mIsAllowInputText == false)
			len = getEditableText().length();
		setFilters(new InputFilter[] { new InputFilter.LengthFilter(len) });
	}

	private SpannableString getSpannableString(SpannableString spanStr) {
		return getSpannableString(spanStr, DefaultGlobal.SPACE_TOKENIZER);
	}

	private SpannableString getSpannableString(SpannableString spanStr,
			char customChar) {
		SpannableString sp = new SpannableString(spanStr + (customChar + " "));
		TextUtils.copySpansFrom((Spanned) spanStr, 0, spanStr.length(),
				Object.class, sp, 0);
		return sp;
	}

	private SpannableString getSpannableString(boolean isDirty, String name,
			String address, ChooseObjEntity objEntity) {
		return getSpannableString(isDirty, name, address, null, objEntity);
	}

	private SpannableString getSpannableString(boolean isDirty, String name,
			String address, TextDrawable drawable, ChooseObjEntity objEntity) {
		String value = getValue(name, address);
		SpannableString spanStr = getUncompleteSpanStr(isDirty, value,
				drawable, objEntity);
		return getSpannableString(spanStr);
	}

	private SpannableString getUncompleteSpanStr(boolean isDirty, String value,
			TextDrawable drawable, ChooseObjEntity objEntity) {
		return getUncompleteSpanStr(isDirty, value, null, drawable, objEntity);
	}

	private SpannableString getUncompleteSpanStr(boolean isDirty, String value,
			String outKey, TextDrawable drawable, ChooseObjEntity objEntity) {
		if (drawable == null)
			drawable = getTextDrawable(isDirty, value);
		SpannableString spanStr;
		if (outKey == null || "".equals(outKey))
			spanStr = new SpannableString(value);
		else
			spanStr = new SpannableString(value + outKey);
		AutoClickableImageSpan imageSpan = new AutoClickableImageSpan(drawable);
		imageSpan.setDirty(isDirty);
		imageSpan.setChooseObjEntity(objEntity);
		spanStr.setSpan(imageSpan, 0, value.length(),
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		return spanStr;
	}

	private String getValue(String name, String address) {
		return name + DefaultGlobal.SEPARATOR_LEFT + address
				+ DefaultGlobal.SEPARATOR_RIGHT;
	}

	public String getOutKey(SpannableString spanStr) {
		String str = spanStr.toString();
		return str
				.substring(str.lastIndexOf(DefaultGlobal.SEPARATOR_RIGHT) + 1);
	}

	private TextDrawable getTextDrawable(boolean isDirty, String value) {
		TextDrawable drawable = new TextDrawable(getContext(), value);
		if (isDirty) {
			drawable.setTextBgColor(mDirtyTextBgColor);
			drawable.setTextFgColor(mDirtyTextFgColor);
		} else {
			drawable.setTextBgColor(mValidTextBgColor);
			drawable.setTextFgColor(mValidTextFgColor);
		}
		drawable.setTextSize(mTextSize);
		drawable.setPadding(mRoundRectPaddingLeft, mRoundRectPaddingRight,
				mRoundRectPaddingTop, mRoundRectPaddingBottom);
		drawable.setRoundRectRadius(mRoundRectRadiusX, mRoundRectRadiusY);
		drawable.setBounds();
		return drawable;
	}

	private void confirmInput(SpannableString spanStr, String outKey) {
		String value = spanStr.toString();
		ChooseObjEntity objEntity = new ChooseObjEntity();
		objEntity.name = value.substring(0,
				value.lastIndexOf(DefaultGlobal.SEPARATOR_LEFT));
		objEntity.address = value.substring(
				value.lastIndexOf(DefaultGlobal.SEPARATOR_LEFT) + 1,
				value.lastIndexOf(DefaultGlobal.SEPARATOR_RIGHT));
		objEntity.outKey = outKey;
		if (mChooseObjList.isEmpty())
			objEntity.start = 0;
		else {
			ChooseObjEntity lastEntity = mChooseObjList.get(mChooseObjList
					.size() - 1);
			objEntity.start = lastEntity.end;
		}
		objEntity.end = objEntity.start + value.length();
		mChooseObjList.add(objEntity);

		AutoClickableImageSpan[] imageSpan = spanStr.getSpans(0,
				value.length(), AutoClickableImageSpan.class);
		objEntity.drawable = (TextDrawable) imageSpan[0].getDrawable();
		objEntity.isDirty = imageSpan[0].isDirty();
		imageSpan[0].setChooseObjEntity(objEntity);
	}

	private boolean delete() {
		Editable editable = getEditableText();
		if (!mChooseObjList.isEmpty()) {
			for (ChooseObjEntity objEntity : mChooseObjList) {
				if (objEntity.isSelected) {
					if (mDeleteObjListener != null)
						mDeleteObjListener.delete(objEntity.outKey);
					deleteTextAndObj(editable, objEntity);
					resetChooseObjList();
					allowInputTextControl();
					return true;
				}
			}
			ChooseObjEntity lastEntity = mChooseObjList.get(mChooseObjList
					.size() - 1);
			if (lastEntity.end == editable.length()) {
				if (lastEntity.isSelected)
					deleteTextAndObj(editable, lastEntity);
				else
					updateText(lastEntity);
				return true;
			}
		}
		return false;
	}

	private void deleteTextAndObj(Editable editable, ChooseObjEntity objEntity) {
		deleteText(editable, objEntity);
		objEntity.drawable.setCallback(null);
		objEntity.drawable = null;
		mChooseObjList.remove(objEntity);
	}

	private void deleteText(Editable editable, ChooseObjEntity objEntity) {
		removeSpan(editable, objEntity);
		editable.delete(objEntity.start, objEntity.end);
	}

	private void removeSpan(Editable editable, ChooseObjEntity objEntity) {
		AutoClickableImageSpan[] imageSpans = editable.getSpans(
				objEntity.start, objEntity.end, AutoClickableImageSpan.class);
		for (int i = 0; imageSpans != null && i < imageSpans.length; i++)
			editable.removeSpan(imageSpans[i]);
	}

	private void resetChooseObjList() {
		if (!mChooseObjList.isEmpty()) {
			int start = 0;
			for (ChooseObjEntity objEntity : mChooseObjList) {
				int len = objEntity.end - objEntity.start;
				objEntity.start = start;
				objEntity.end = start + len;
				start = objEntity.end;
			}
		}
	}

	private synchronized void dealDirty() {
		addDirtyObj();
		clearDirtyData();
	}

	private void addDirtyObj() {
		Editable editable = getEditableText();
		int length = editable.length();
		if (length > 1) {
			char endCh1 = editable.charAt(length - 1);
			char endCh2 = editable.charAt(length - 2);
			String dirty = null;
			if (mChooseObjList.isEmpty()) {
				if (endCh1 == DefaultGlobal.SPACE_TOKENIZER
						&& endCh2 != DefaultGlobal.SPACE_TOKENIZER) {
					dirty = editable.subSequence(0, length - 1).toString()
							.trim();
					dirty = dirty.replaceAll("\\"
							+ DefaultGlobal.SPACE_TOKENIZER, "");
					editable.delete(0, length);
				}
			} else {
				ChooseObjEntity lastEntity = mChooseObjList.get(mChooseObjList
						.size() - 1);
				if (endCh1 == DefaultGlobal.SPACE_TOKENIZER
						&& endCh2 != DefaultGlobal.SPACE_TOKENIZER
						&& lastEntity.end < length - 1) {
					dirty = editable.subSequence(lastEntity.end, length - 1)
							.toString().trim();
					dirty = dirty.replaceAll("\\"
							+ DefaultGlobal.SPACE_TOKENIZER, "");
					editable.delete(lastEntity.end, length);
				}
			}
			if (dirty != null && dirty.equals("") == false) {
				boolean isDirty = dirty.length() >= mMinValidLength ? false
						: true;
				SpannableString sp = getSpannableString(isDirty, dirty, dirty,
						null);
				editable.append(sp);
				confirmInput(sp, dirty);
			}
		}
	}

	private void clearDirtyData() {
		Editable editable = getEditableText();
		if (!mChooseObjList.isEmpty()) {
			int length = editable.length();
			if (mChooseObjList.size() == 1) {
				String data = editable.toString();
				if (data.startsWith(DefaultGlobal.SPACE_TOKENIZER + "")) {
					ChooseObjEntity lastEntity = mChooseObjList.get(0);
					if (length > lastEntity.end)
						editable.delete(0, length - lastEntity.end);
				}
			} else {
				ChooseObjEntity lastEntity = mChooseObjList.get(mChooseObjList
						.size() - 1);
				if (length > lastEntity.end) {
					String data = editable
							.subSequence(lastEntity.start, length).toString();
					if (data.startsWith(DefaultGlobal.SPACE_TOKENIZER + ""))
						editable.delete(lastEntity.start, lastEntity.start
								+ (length - lastEntity.end));
				}
			}
		}
	}

	private void updateText(ChooseObjEntity objEntity) {
		if (objEntity.isSelected) {
			objEntity.isSelected = false;
			if (objEntity.isDirty) {
				objEntity.drawable.setTextBgColor(mDirtyTextBgColor);
				objEntity.drawable.setTextFgColor(mDirtyTextFgColor);
			} else {
				objEntity.drawable.setTextBgColor(mValidTextBgColor);
				objEntity.drawable.setTextFgColor(mValidTextFgColor);
			}
		} else {
			objEntity.isSelected = true;
			if (objEntity.isDirty) {
				objEntity.drawable.setTextBgColor(mDirtySelectedTextBgColor);
				objEntity.drawable.setTextFgColor(mDirtySelectedTextFgColor);
			} else {
				objEntity.drawable.setTextBgColor(mValidSelectedTextBgColor);
				objEntity.drawable.setTextFgColor(mValidSelectedTextFgColor);
			}
		}

		SpannableString sp = getSpannableString(objEntity.isDirty,
				objEntity.name, objEntity.address, objEntity.drawable,
				objEntity);

		Editable editable = getEditableText();
		removeSpan(editable, objEntity);
		editable.replace(objEntity.start, objEntity.end, sp);
	}

	private void drawable2Text() {
		setSingleLine(true);
		Editable editable = getEditableText();
		for (ChooseObjEntity objEntity : mChooseObjList) {
			objEntity.isSelected = false;
			if (objEntity.isDirty) {
				objEntity.drawable.setTextBgColor(mDirtyTextBgColor);
				objEntity.drawable.setTextFgColor(mDirtyTextFgColor);
			} else {
				objEntity.drawable.setTextBgColor(mValidTextBgColor);
				objEntity.drawable.setTextFgColor(mValidTextFgColor);
			}
			removeSpan(editable, objEntity);
		}
	}

	private void text2Drawable() {
		setSingleLine(false);
		Editable editable = getEditableText();
		SpannableString sp = new SpannableString(editable.toString());
		for (ChooseObjEntity objEntity : mChooseObjList)
			addSpan(sp, objEntity);
		editable.replace(0, sp.length(), sp);
		setSelection(editable.length());
	}

	private void addSpan(SpannableString sp, ChooseObjEntity objEntity) {
		AutoClickableImageSpan imageSpan = new AutoClickableImageSpan(
				objEntity.drawable);
		imageSpan.setDirty(objEntity.isDirty);
		imageSpan.setChooseObjEntity(objEntity);
		sp.setSpan(imageSpan, objEntity.start, objEntity.end - 1,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
	}

	// ******************************************************************
	// 自定义的类

	public interface OnDeleteObjListener {
		public void delete(String outKey);
	}

	private class AutoFocusChangeListener implements OnFocusChangeListener {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus == false)
				drawable2Text();
			else
				text2Drawable();
		}
	}

	private class AutoKeyListener implements OnKeyListener {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if (keyCode == KeyEvent.KEYCODE_DEL)
					return delete();
			}
			return false;
		}
	}

	private class AutoTextChangedListener implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			dealDirty();
		}
	}

	private class AutoCursorAdapter extends CursorAdapter {

		private DbUtil dbUtil = null;
		private ResUtil resUtil = null;

		public AutoCursorAdapter() {
			super(getContext(), null, 0);
			dbUtil = DbUtil.getInstance(getContext());
			resUtil = new ResUtil(getContext());
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			return inflater.inflate(
					resUtil.getIdFromLayout(LayoutRes.emailtextarea), null);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView nameText = (TextView) view.findViewById(resUtil
					.getIdFromView(ViewRes.nameText));
			TextView addressText = (TextView) view.findViewById(resUtil
					.getIdFromView(ViewRes.addressText));
			String name = cursor.getString(cursor
					.getColumnIndex(DatabaseGlobal.FIELD_NAME));
			String address = cursor.getString(cursor
					.getColumnIndex(DatabaseGlobal.FIELD_ADDRESS));
			nameText.setText(name);
			addressText.setText(address);
		}

		@Override
		public CharSequence convertToString(Cursor cursor) {
			String name = cursor.getString(cursor
					.getColumnIndex(DatabaseGlobal.FIELD_NAME));
			String address = cursor.getString(cursor
					.getColumnIndex(DatabaseGlobal.FIELD_ADDRESS));
			String outKey = cursor.getString(cursor
					.getColumnIndex(DatabaseGlobal.FIELD_OUT_KEY));
			return getUncompleteSpanStr(false, getValue(name, address), outKey,
					null, null);
		}

		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			return dbUtil.queryByLike(DatabaseGlobal.TABLE_EMAIL_OBJS,
					new String[] { DatabaseGlobal.FIELD_NAME,
							DatabaseGlobal.FIELD_ADDRESS,
							DatabaseGlobal.FIELD_OUT_KEY },
					constraint.toString());
		}

	}

	private class CustomTokenizer implements Tokenizer {

		private char customChar = ',';

		public CustomTokenizer(char ch) {
			customChar = ch;
		}

		@Override
		public int findTokenStart(CharSequence text, int cursor) {
			int i = cursor;

			while (i > 0 && text.charAt(i - 1) != customChar) {
				i--;
			}
			while (i < cursor && text.charAt(i) == ' ') {
				i++;
			}

			return i;
		}

		@Override
		public int findTokenEnd(CharSequence text, int cursor) {
			int i = cursor;
			int len = text.length();

			while (i < len) {
				if (text.charAt(i) == customChar) {
					return i;
				} else {
					i++;
				}
			}

			return len;
		}

		@Override
		public CharSequence terminateToken(CharSequence text) {
			int i = text.length();

			while (i > 0 && text.charAt(i - 1) == ' ') {
				i--;
			}

			if (i > 0 && text.charAt(i - 1) == customChar) {
				return text;
			} else {
				if (text instanceof Spanned) {

					String outKey = getOutKey((SpannableString) text);
					text = text.subSequence(0, text.length() - outKey.length());

					SpannableString sp = getSpannableString(
							(SpannableString) text, customChar);

					confirmInput(sp, outKey);

					return sp;
				} else {
					return text + (customChar + " ");
				}
			}
		}
	}

	private class AutoClickableImageSpan extends ImageSpan {

		private boolean isDirty;
		private ChooseObjEntity objEntity;

		public AutoClickableImageSpan(Drawable d) {
			super(d);
		}

		public void onClick(View v) {
			updateText(objEntity);
		}

		public void setDirty(boolean flag) {
			isDirty = flag;
		}

		public boolean isDirty() {
			return isDirty;
		}

		public void setChooseObjEntity(ChooseObjEntity obj) {
			objEntity = obj;
		}

	}

	private class AutoLinkMovementMethod extends LinkMovementMethod {

		@Override
		public boolean onTouchEvent(TextView widget, Spannable buffer,
				MotionEvent event) {

			int action = event.getAction();

			if (action == MotionEvent.ACTION_UP
					|| action == MotionEvent.ACTION_DOWN) {
				int x = (int) event.getX();
				int y = (int) event.getY();

				x -= widget.getTotalPaddingLeft();
				y -= widget.getTotalPaddingTop();

				x += widget.getScrollX();
				y += widget.getScrollY();

				Layout layout = widget.getLayout();
				int line = layout.getLineForVertical(y);
				int off = layout.getOffsetForHorizontal(line, x);

				AutoClickableImageSpan[] image = buffer.getSpans(off, off,
						AutoClickableImageSpan.class);

				if (image.length != 0) {
					if (action == MotionEvent.ACTION_UP) {
						image[0].onClick(widget);
						// Selection.removeSelection(buffer);
					} else if (action == MotionEvent.ACTION_DOWN) {
						// Selection.setSelection(buffer,
						// buffer.getSpanStart(image[0]),
						// buffer.getSpanEnd(image[0]));
					}

					return true;
				} else {
					Selection.removeSelection(buffer);
				}
			}

			return super.onTouchEvent(widget, buffer, event);
		}

	}

}
