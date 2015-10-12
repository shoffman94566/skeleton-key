package com.kp.skey;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewMainActivity extends Activity {

	private static NewMainActivity instance;

	private EditText mEnterSiteNameInput;
	private TextView mPasswordGenerated;
	private EditText mPasswordGeneratedInput;
	private Button mSubmit;
	private Button mClear;
	private Button mCloseTheApp;
	private Button mInstructionsSuggestions;
	private Button mChangeSkey;
	private CheckBox mCapsButton;
	private CheckBox mNumeralsButton;
	private CheckBox mSymbolsButton;
	private LinearLayout mConditionsLayout;
	private TextView mCreateTag;

	private EditText passwordLength;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main1);
		instance = this;
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		SharedPrefs.init(this);

		mEnterSiteNameInput = (EditText) findViewById(R.id.enter_site_name_input);
		mPasswordGenerated = (TextView) findViewById(R.id.password_generated);
		mCreateTag = (TextView) findViewById(R.id.create_tag);
		mPasswordGeneratedInput = (EditText) findViewById(R.id.password_generated_input);
		mSubmit = (Button) findViewById(R.id.submit);
		mClear = (Button) findViewById(R.id.clear);
		mCloseTheApp = (Button) findViewById(R.id.close_the_app);
		mInstructionsSuggestions = (Button) findViewById(R.id.instructions_suggestions);
		mChangeSkey = (Button) findViewById(R.id.change_s_key);
		mConditionsLayout = (LinearLayout) findViewById(R.id.conditions);

		mEnterSiteNameInput.addTextChangedListener(Util.tw);

		passwordLength = (EditText) findViewById(R.id.length_edit);
		mCapsButton = (CheckBox) findViewById(R.id.caps_button);
		mNumeralsButton = (CheckBox) findViewById(R.id.numerals_button);
		mSymbolsButton = (CheckBox) findViewById(R.id.symbols_button);

		mCapsButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				generatePassword(true);
			}
		});

		mNumeralsButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						generatePassword(true);
					}
				});

		mSymbolsButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						generatePassword(true);
					}
				});

		passwordLength.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				generatePassword(true);
			}

		});

		mSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mEnterSiteNameInput.getText() == null
						|| mEnterSiteNameInput.getText().length() == 0) {
					Toast.makeText(instance, "Enter Site name",
							Toast.LENGTH_LONG).show();
					return;
				}

				View view = instance.getCurrentFocus();
				if (view != null) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				String sitename = mEnterSiteNameInput.getText().toString();
				String s = SkeyApplication.getMyMap(sitename);
				if (s == null || s.length() == 0) {
					// make others visible.
					if (!arePasswordCreateElementsShown()) {
						showPasswordCreateElements();
					} else {
						generatePassword(false);
						return;
					}
				}
				showPasswordGenerated(s);

			}
		});

		mClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hidePasswordGenerated();
				hidePasswordCreateElements();
			}
		});

		mCloseTheApp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				instance.finish();
			}
		});

		mChangeSkey.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
               showPasswordCreateElements();
			}
		});

	}

	private void generatePassword(boolean status) {

		String sitename = mEnterSiteNameInput.getText().toString();
		View view = instance.getCurrentFocus();

		if (mEnterSiteNameInput.getText() == null
				|| mEnterSiteNameInput.getText().length() == 0) {
			Toast.makeText(instance, "Enter Site name", Toast.LENGTH_LONG)
					.show();
			return;
		}
		if (mEnterSiteNameInput.getText().length() < 2) {
			Toast.makeText(instance,
					"Enter Site name should alteast be 2 charaters",
					Toast.LENGTH_LONG).show();
			return;
		}
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
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
					mNumeralsButton.isChecked(), mSymbolsButton.isChecked(),
					mCapsButton.isChecked(), len);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		if (SkeyApplication.containsKey(sitename) && !status) {
			Toast.makeText(
					instance,
					"Password " + SkeyApplication.getMyMap(sitename)
							+ " previously created for this site.",
					Toast.LENGTH_LONG).show();
			return;
		}
		SkeyApplication.put(sitename, passkey);
		showPasswordGenerated(passkey);
	}

	private void hidePasswordGenerated() {
		mPasswordGenerated.setVisibility(View.GONE);
		mPasswordGeneratedInput.setText("");
		mPasswordGeneratedInput.setVisibility(View.GONE);
	}

	private void showPasswordGenerated(String password) {
		mPasswordGenerated.setVisibility(View.VISIBLE);
		mPasswordGeneratedInput.setText(password);
		mPasswordGeneratedInput.setVisibility(View.VISIBLE);
	}

	private void showPasswordCreateElements() {
		mCreateTag.setVisibility(View.VISIBLE);
		mConditionsLayout.setVisibility(View.VISIBLE);
	}

	private void hidePasswordCreateElements() {
		mCreateTag.setVisibility(View.GONE);
		mConditionsLayout.setVisibility(View.GONE);
	}

	private boolean arePasswordCreateElementsShown() {
		if (mConditionsLayout.getVisibility() == View.VISIBLE)
			return true;
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		hidePasswordGenerated();
		hidePasswordCreateElements();
	}

}
