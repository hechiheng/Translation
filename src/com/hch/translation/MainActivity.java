package com.hch.translation;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.translate.demo.Main;
import com.baidu.translate.demo.TransApi;
import com.google.gson.Gson;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {

	private Button button1, button2, button3, button4, button5;
	private EditText text1, text2;
	private String input, output;

	private int transMode = 1;// 翻译模式（1：中英，2：英中）
	private String from = "zh", to = "en";// 翻译参数

	private static final String APP_ID = "20171006000086579";
	private static final String SECURITY_KEY = "QjgNguYi3DNjSrhaWdlJ";
	private TransApi api;

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			text2.setText(output);

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		button4 = (Button) findViewById(R.id.button4);
		button5 = (Button) findViewById(R.id.button5);
		text1 = (EditText) findViewById(R.id.text1);
		text2 = (EditText) findViewById(R.id.text2);

		api = new TransApi(APP_ID, SECURITY_KEY);

		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				transMode = transMode == 1 ? 2 : 1;
				if (transMode == 1) {
					button1.setText("中文");
					button3.setText("英语");
					from = "zh";
					to = "en";
				} else {
					button1.setText("英语");
					button3.setText("中文");
					from = "en";
					to = "zh";
				}
			}
		});

		button4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				input = text1.getText().toString();
				if (TextUtils.isEmpty(input)) {
					Toast.makeText(MainActivity.this, "请输入你要翻译的文字",
							Toast.LENGTH_SHORT).show();
					return;
				}

				new Thread() {
					public void run() {
						String result = api.getTransResult(input, from, to);
						// 返回json结构：{"from":"en","to":"zh","trans_result":[{"src":"apple","dst":"\u82f9\u679c"}]}
						Gson gson = new Gson();
						TransResponse response = gson.fromJson(result,
								TransResponse.class);
						String dst = response.getTrans_result().get(0).getDst();
						output = dst;
						handler.sendEmptyMessage(0);
					};

				}.start();

			}
		});

		button5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String outputText = text2.getText().toString();
				if (TextUtils.isEmpty(outputText)) {
					return;
				}
				copy(outputText, MainActivity.this);
				Toast.makeText(MainActivity.this, "已复制到剪贴板", Toast.LENGTH_SHORT)
						.show();
			}
		});

	}

	/**
	 * 实现文本复制功能
	 * 
	 * @param content
	 */
	public static void copy(String content, Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
	}
}
