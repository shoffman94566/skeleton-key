package com.kp.skey;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import android.app.Application;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public class SkeyApplication extends Application {

	private static HashMap<String, String> myMap;

	private static HashMap<String, PasswordParameters> newMyMap;

	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		init();
	}

	public static void init() {
		myMap = new HashMap<>();
		SharedPrefs.init(context);
	}

	public static String getMyMap(String sitename) {
		if (myMap == null || myMap.size() == 0) {
			myMap = SharedPrefs.getAll();
		}
		return myMap.get(sitename);
	}

	public static boolean containsKey(String sitename) {
		if (myMap == null)
			myMap = SharedPrefs.getAll();
		return myMap.containsKey(sitename);
	}

	public static void put(String siteName, int size, String passkey, long timestamp,
						   boolean lowercase, boolean uppercase, boolean numerical,
						   boolean symbol) throws JSONException {
		if (myMap == null)
			myMap = SharedPrefs.getAll();

		PasswordParameters passwordParameters = new PasswordParameters();
		passwordParameters.setHashedSiteName(siteName);
		passwordParameters.setSize(size);
		passwordParameters.setLowerCase(new Boolean(lowercase));
		passwordParameters.setUpperCase(new Boolean(uppercase));
		passwordParameters.setNumerical(new Boolean(numerical));
		passwordParameters.setSymbol(new Boolean(symbol));
		passwordParameters.setTimestamp(timestamp);
		String passwordParametersJsonString = passwordParameters.convertToJson();



		myMap.put(siteName, passwordParametersJsonString);
		SharedPrefs.setAll(myMap);
	}

	public static String getPassword(String siteName) {
		String jsonString = myMap.get(siteName);
		String password = "";
		try {
			JSONObject passwordParemetersJson = new JSONObject(jsonString);
			try {
				password = PasswordGenerator.generate(siteName, passwordParemetersJson.getBoolean("numerical"),
						passwordParemetersJson.getBoolean("symbol"), passwordParemetersJson.getBoolean("upperCase"),
						passwordParemetersJson.getBoolean("lowerCase"), passwordParemetersJson.getInt("size"),
						passwordParemetersJson.getLong("timestamp"));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	return password;
	}
}


