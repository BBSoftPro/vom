package io.vom.utils;

import java.io.File;
import java.util.Objects;

public class FileUtil {
    public static String getFullPath(String path){
        Objects.requireNonNull(path);
        if (path.startsWith("/")){
            return path;
        }

        return Properties.getInstance().getUserDir()+ File.separator + path;
    }
}
