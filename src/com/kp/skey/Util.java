package com.kp.skey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

public class Util {

	private static final String TAG = Util.class.getName();

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static boolean copyToClipboard(Context context, String text) {
		try {
			int sdk = android.os.Build.VERSION.SDK_INT;
			if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
				android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
						.getSystemService(context.CLIPBOARD_SERVICE);
				clipboard.setText(text);
			} else {
				android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
						.getSystemService(context.CLIPBOARD_SERVICE);
				android.content.ClipData clip = android.content.ClipData
						.newPlainText("text", text);
				clipboard.setPrimaryClip(clip);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static TextWatcher tw = new TextWatcher() {

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void afterTextChanged(Editable s) {
			for (int i = s.length(); i > 0; i--) {
				if (s.subSequence(i - 1, i).toString().equals("\n"))
					s.replace(i - 1, i, "");
			}
			String myTextString = s.toString();
		}
	};
	
}
