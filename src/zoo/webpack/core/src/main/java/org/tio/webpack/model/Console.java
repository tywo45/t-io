package org.tio.webpack.model;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Console {
	private boolean log;

	private boolean info;

	private boolean error;

	public void setLog(boolean log) {
		this.log = log;
	}

	public boolean getLog() {
		return this.log;
	}

	public void setInfo(boolean info) {
		this.info = info;
	}

	public boolean getInfo() {
		return this.info;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public boolean getError() {
		return this.error;
	}

	public static Console fill(JSONObject jsonobj) {
		Console entity = new Console();
		if (jsonobj.containsKey("log")) {
			entity.setLog(jsonobj.getBoolean("log"));
		}
		if (jsonobj.containsKey("info")) {
			entity.setInfo(jsonobj.getBoolean("info"));
		}
		if (jsonobj.containsKey("error")) {
			entity.setError(jsonobj.getBoolean("error"));
		}
		return entity;
	}

	public static List<Console> fillList(JSONArray jsonarray) {
		if (jsonarray == null || jsonarray.size() == 0)
			return null;
		List<Console> olist = new ArrayList<Console>();
		for (int i = 0; i < jsonarray.size(); i++) {
			olist.add(fill(jsonarray.getJSONObject(i)));
		}
		return olist;
	}
}