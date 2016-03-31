package com.email.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.email.R;
import com.email.view.EmailTextArea;
import com.email.view.EmailTextArea.OnDeleteObjListener;

public class MainActivity extends Activity {

	private EmailTextArea mEmailTextArea = null;
	private EditText mAllowInputStatusText = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAllowInputStatusText = (EditText) findViewById(R.id.allowInputStatusText);

		mEmailTextArea = (EmailTextArea) findViewById(R.id.emailTextArea);
		mEmailTextArea.setOnDeleteObjListener(new OnDeleteObjListener() {
			@Override
			public void delete(String key) {
				Log.i("My", "delete = " + key);
			}
		});

		mAllowInputStatusText.setHint("isAllowInput = "
				+ mEmailTextArea.isAllowInputText());

		insertDatas();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mEmailTextArea.deleteDatas();
		mEmailTextArea.onDestroy();
	}

	// ***************************************************************************

	public void testDrawable(View v) {
		mEmailTextArea.setDirtyTextBgColor(Color.rgb(0xff, 0x66, 0x66),
				Color.RED);
		mEmailTextArea.setValidTextBgColor(Color.rgb(0x66, 0x66, 0xff),
				Color.BLUE);
		mEmailTextArea.setDirtyTextFgColor(Color.GREEN, Color.WHITE);
		mEmailTextArea.setValidTextFgColor(Color.BLACK, Color.RED);
		mEmailTextArea.setRoundRectPadding(5, 5, 5, 5);
		mEmailTextArea.setRoundRectRadius(10, 10);
		mEmailTextArea.setTextSizeOfRoundRect(10);
	}

	public void recoverDrawable(View v) {
		mEmailTextArea.recoverDefaultValues();
	}

	public void testSetContent(View v) {
		String[] names = new String[] { "name1", "name2", "name3(测量3)" };
		String[] addresses = new String[] { "address1", "address2",
				"(address3)" };
		String[] keys = new String[] { "key1", "key2", "key3" };
		mEmailTextArea.setContent(names, addresses, keys);
	}

	public void testAppendContent(View v) {
		String[] names = new String[] { "name1", "name2", "name3" };
		String[] addresses = new String[] { "address1", "address2", "address3" };
		String[] keys = new String[] { "key1", "key2", "key3" };
		mEmailTextArea.appendContent(names, addresses, keys);
	}

	public void testClearContent(View v) {
		mEmailTextArea.clearContent();
	}

	public void testSetAllowInputText(View v) {
		mEmailTextArea.setAllowInputText(!mEmailTextArea.isAllowInputText());
		mAllowInputStatusText.setHint("isAllowInput = "
				+ mEmailTextArea.isAllowInputText());
	}

	public void testGetOutKeys(View v) {
		String[] outKeys = mEmailTextArea.getOutKeys();
		for (int i = 0; outKeys != null && i < outKeys.length; i++)
			Log.i("My", "outKey = " + outKeys[i]);
	}

	// ***************************************************************************

	private void insertDatas() {
		String[] names = new String[] { "小米", "红米", "卡卡西", "波风水门", "鸣人", "自来也",
				"大蛇丸", "佐助" };
		String[] addresses = new String[] { "123456789", "012345678",
				"987654321", "696478844", "238974587", "85545615", "889821231",
				"927122132" };
		String[] outKeys = new String[] { "k-小米", "k-红米", "k-卡卡西", "k-波风水门",
				"k-鸣人", "k-自来也", "k-大蛇丸", "k-佐助" };
		mEmailTextArea.insertDatas(names, addresses, outKeys);
	}

}
