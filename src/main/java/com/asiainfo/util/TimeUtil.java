package com.asiainfo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class TimeUtil {

	public static String thisSDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(thisDDate());
	}

	public static Date thisDDate() {
		return new Date();
	}

}
