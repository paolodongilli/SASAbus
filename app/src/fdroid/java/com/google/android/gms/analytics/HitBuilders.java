package com.google.android.gms.analytics;

public class HitBuilders {

	public static class HitBuilder {
		public void build() {
			return;
		}
	}

	public static class ScreenViewBuilder {
		public HitBuilder build() {
			return new HitBuilder();
		}
	}
}
