package com.chanyouji.android.map.model;

import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerOptionsWrap {
	private MarkerOptions options;

	private String webIconPath;

	public MarkerOptionsWrap() {
		options = new MarkerOptions();
	}

	public MarkerOptionsWrap(MarkerOptions options) {
		if (null == options) {
			options = new MarkerOptions();
		}
		this.options = options;
	}

	public MarkerOptions getOptions() {
		return options;
	}

	public void setOptions(MarkerOptions options) {
		this.options = options;
	}

	public String getWebIconPath() {
		return webIconPath;
	}

	public void setWebIconPath(String webIconPath) {
		this.webIconPath = webIconPath;
	}

}
