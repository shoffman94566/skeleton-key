package com.kp.skey;

import java.util.HashMap;

import android.app.Application;
import android.content.Context;

public class SkeyApplication extends Application {

	private static HashMap<String, String> myMap;

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

	public static void put(String sitename, String passkey) {
		if (myMap == null)
			myMap = SharedPrefs.getAll();
		myMap.put(sitename, passkey);
		SharedPrefs.setAll(myMap);
	}

}
