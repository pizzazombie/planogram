package com.adidas.tsar.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogUtils {

	public String buildTimeLog(long executionTimeInNano){
		long executionTimeInMillis = executionTimeInNano / 1000000;
		if (executionTimeInMillis < 1000)
			return String.format("%d millis", executionTimeInMillis);
		if (executionTimeInMillis < 60000)
			return String.format("%d seconds", executionTimeInMillis / 1000);
		if (executionTimeInMillis < 3600000)
			return String.format("%d minutes", executionTimeInMillis / 60000);
		else{
			return String.format("%d hours and %d minutes", executionTimeInMillis / 3600000, executionTimeInMillis % 3600000);
		}
	}

	public String buildTimeLogInSeconds(long executionTimeInNano) {
		long executionTimeInMillis = executionTimeInNano / 1000000;
		return String.format("%d seconds", executionTimeInMillis / 1000);
	}

	public String buildTimeLogInMillis(long executionTimeInNano) {
		long executionTimeInMillis = executionTimeInNano / 1000000;
		return String.format("%d millis", executionTimeInMillis);
	}
}
