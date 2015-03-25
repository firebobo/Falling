package bb.stone.falling.file;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileCopyHelper {
	public static boolean copyFile(Context paramContext, int paramInt,
			String paramString) {
		boolean bool = true;
		try {
			File localFile = new File(paramString);
			if (!localFile.exists()) {
				InputStream localInputStream = paramContext.getResources()
						.openRawResource(paramInt);
				FileOutputStream localFileOutputStream = new FileOutputStream(
						localFile);
				byte[] arrayOfByte = new byte[8192];
				while (true) {
					int i = localInputStream.read(arrayOfByte);
					if (i <= 0) {
						localFileOutputStream.close();
						localInputStream.close();
						return bool;
					}
					localFileOutputStream.write(arrayOfByte, 0, i);
				}
			}
		} catch (Exception localException) {
			localException.printStackTrace();
			bool = false;
		}
		return bool;
	}

	public static String fileFromRaw2Path(Context paramContext, int paramInt,
			String paramString1, String paramString2) {
		String str = paramString1 + "/" + paramString2;
		if (isOrCreateDir(paramString1))
			copyFile(paramContext, paramInt, str);
		return str;
	}

	public static String fileFromRaw2SD(Context paramContext, int paramInt,
			String paramString1, String paramString2) {
		return fileFromRaw2Path(paramContext, paramInt, Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ "/"
				+ paramString1 + "/", paramString2);
	}

	public static boolean isOrCreateDir(String paramString) {
		File localFile = new File(paramString);
		return (localFile.exists()) || (localFile.mkdirs());
	}
}