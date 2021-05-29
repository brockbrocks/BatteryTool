package app.nehc.batterytool.utils;

import java.io.File;

public class FileUtil {
    public static boolean isFileExists(String path) {
        File file = new File(path);
        if (file.exists())
            return true;
        else
            return false;
    }
}
