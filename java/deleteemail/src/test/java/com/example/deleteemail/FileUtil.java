package com.example.deleteemail;

import java.io.File;

public class FileUtil {

	/**
	 * 创建空目录
	 */
	public static void createEmptyDirectory(String directoryPath) {
		File file = new File(directoryPath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
}