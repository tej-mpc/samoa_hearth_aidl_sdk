

package com.samoa.hearth_sdk;

import android.os.Build;
import java.io.File;

public class Utils {

	public static boolean isRooted() {

		// get from build info
		String buildTags = Build.TAGS;
		if (buildTags != null && buildTags.contains("test-keys")) {
			return true;
		}

		// check if /system/app/Superuser.apk is present
		try {
			File file = new File("/system/app/Superuser.apk");
			if (file.exists()) {
				return true;
			}
		} catch (Exception e1) {
			// ignore
		}

		// try executing commands
		return canExecuteCommand("su -0");
	}

	// executes a command on the system
	private static boolean canExecuteCommand(String command) {
		boolean executedSuccesfully;
		try {
			Runtime.getRuntime().exec(command);
			executedSuccesfully = true;
		} catch (Exception e) {
			executedSuccesfully = false;
		}

		return executedSuccesfully;
	}
}
