package com.kp.skey;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static MainActivity instance;
	private Button closeApp;
	private Button recallSubmit;
	private EditText recallSubmitText;
	private Button createSubmit;
	private EditText createSubmitText;
	private EditText enterSiteName;
	private EditText enterSiteNameCreate;
	private EditText passwordLength;

	private CheckBox capsButton;
	private CheckBox numeralsButton;
	private CheckBox symbolsButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		instance = this;
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		SharedPrefs.init(this);
		closeApp = (Button) findViewById(R.id.close);
		enterSiteName = (EditText) findViewById(R.id.check_site_name);
		enterSiteName.addTextChangedListener(Util.tw);
		enterSiteNameCreate = (EditText) findViewById(R.id.site_name_create);
		enterSiteNameCreate.addTextChangedListener(Util.tw);
		recallSubmit = (Button) findViewById(R.id.pass_recall_submit);
		recallSubmitText = (EditText) findViewById(R.id.pass_recall_show);
		recallSubmitText.addTextChangedListener(Util.tw);
		recallSubmitText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					String value = String.valueOf(createSubmitText.getText());
					if (Util.copyToClipboard(instance, value)) {
						Toast.makeText(instance, "Password copied",
								Toast.LENGTH_LONG).show();
					}
				}

			}
		});

		passwordLength = (EditText) findViewById(R.id.length_edit);

		createSubmit = (Button) findViewById(R.id.password);
		createSubmitText = (EditText) findViewById(R.id.password_show);
		createSubmitText.addTextChangedListener(Util.tw);

		createSubmitText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					String value = String.valueOf(createSubmitText.getText());
					if (Util.copyToClipboard(instance, value)) {
						Toast.makeText(instance, "Password copied",
								Toast.LENGTH_LONG).show();
					}
				}

			}
		});

		capsButton = (CheckBox) findViewById(R.id.caps_button);
		numeralsButton = (CheckBox) findViewById(R.id.numerals_button);
		symbolsButton = (CheckBox) findViewById(R.id.symbols_button);

		recallSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (enterSiteName.getText() == null
						|| enterSiteName.getText().length() == 0) {
					Toast.makeText(instance, "Enter Site name",
							Toast.LENGTH_LONG).show();
					return;
				}

				View view = instance.getCurrentFocus();
				if (view != null) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				String sitename = enterSiteName.getText().toString();
				String s = SkeyApplication.getMyMap(sitename);
				if (s == null || s.length() == 0) {
					enterSiteName.setText("");
					Toast.makeText(
							instance,
							"Please create a SkeletonKey password for this site.",
							Toast.LENGTH_LONG).show();
					return;
				}
				recallSubmitText.setText(s);
				recallSubmitText.setVisibility(View.VISIBLE);
				recallSubmit.setVisibility(View.GONE);
			}
		});

		createSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (enterSiteNameCreate.getText() == null
						|| enterSiteNameCreate.getText().length() == 0) {
					Toast.makeText(instance, "Enter Site name",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (enterSiteNameCreate.getText().length() < 2) {
					Toast.makeText(instance,
							"Enter Site name should alteast be 2 charaters",
							Toast.LENGTH_LONG).show();
					return;
				}
				View view = instance.getCurrentFocus();
				if (view != null) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				String sitename = enterSiteNameCreate.getText().toString();
				MessageDigest md = null;
				try {
					md = MessageDigest.getInstance("MD5");
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}

				// String passkey = checkConditions(Util.MD5(sitename
				// + System.currentTimeMillis()));
				//
				Integer len = 5;
				try {
					len = Integer.valueOf(passwordLength.getText().toString());
					if (len < 5) {
						len = 5;
						passwordLength.setText(5);
					} else if (len > 24) {
						len = 24;
						passwordLength.setText(24);
					}

				} catch (Exception e) {
					e.printStackTrace();
					len = 5;
				}

				String passkey = null;
				try {
					passkey = PasswordGenerator.generate(sitename,
							numeralsButton.isChecked(),
							symbolsButton.isChecked(), capsButton.isChecked(),
							len);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}

				if (SkeyApplication.containsKey(sitename)) {
					Toast.makeText(
							instance,
							"Password " + SkeyApplication.getMyMap(sitename)
									+ " previously created for this site.",
							Toast.LENGTH_LONG).show();
					return;
				}
				SkeyApplication.put(sitename, passkey);
				createSubmitText.setText(SkeyApplication.getMyMap(sitename));
				createSubmitText.setVisibility(View.VISIBLE);
				createSubmit.setVisibility(View.GONE);
			}
		});

		closeApp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				instance.finish();
			}
		});

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		recallSubmit.setVisibility(View.VISIBLE);
		recallSubmitText.setVisibility(View.GONE);
		createSubmit.setVisibility(View.VISIBLE);
		createSubmitText.setVisibility(View.GONE);
	}

}
