package org.tio.webpack.model;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Compress {
	private boolean js;

	private boolean css;

	private boolean html;

	public void setJs(boolean js) {
		this.js = js;
	}

	public boolean getJs() {
		return this.js;
	}

	public void setCss(boolean css) {
		this.css = css;
	}

	public boolean getCss() {
		return this.css;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

	public boolean getHtml() {
		return this.html;
	}

	public static Compress fill(JSONObject jsonobj) {
		Compress entity = new Compress();
		if (jsonobj.containsKey("js")) {
			entity.setJs(jsonobj.getBoolean("js"));
		}
		if (jsonobj.containsKey("css")) {
			entity.setCss(jsonobj.getBoolean("css"));
		}
		if (jsonobj.containsKey("html")) {
			entity.setHtml(jsonobj.getBoolean("html"));
		}
		return entity;
	}

	public static List<Compress> fillList(JSONArray jsonarray) {
		if (jsonarray == null || jsonarray.size() == 0)
			return null;
		List<Compress> olist = new ArrayList<Compress>();
		for (int i = 0; i < jsonarray.size(); i++) {
			olist.add(fill(jsonarray.getJSONObject(i)));
		}
		return olist;
	}
}
