package com.kp.skey;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

	private static SharedPreferences prefs = null;

	public static void init(Context context) {
		if (prefs == null)
			prefs = context.getSharedPreferences("skey", Context.MODE_PRIVATE);
	}

	public static void setAll(HashMap<String, String> myMap) {
		JSONArray jsonArray = new JSONArray();
		ArrayList<String> keys = new ArrayList<>(myMap.keySet());
		for (int i = 0; i < myMap.size(); i++) {
			try {
				jsonArray.put(new JSONObject().put("key", keys.get(i)).put(
						"value", myMap.get(keys.get(i))));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		prefs.edit().putString("MyMap", jsonArray.toString()).commit();
	}

	public static HashMap<String, String> getAll() {
		HashMap<String, String> myMap = new HashMap<>();
		String value = prefs.getString("MyMap", "");
		if (value == "")
			return myMap;
		else {
			try {
				JSONArray jsonArray = new JSONArray(value);
				JSONObject obj = new JSONObject();
				for (int i = 0; i < jsonArray.length(); i++) {
					obj = jsonArray.getJSONObject(i);
					myMap.put(obj.getString("key"), obj.getString("value"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return myMap;
		}
	}

}
