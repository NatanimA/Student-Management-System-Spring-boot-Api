package com.student.management.util;

import java.io.File;

public class Util {

    private static final boolean IS_WINDOW = System.getProperty("os.name").toLowerCase().contains("window");;
    public static String window_path_media=System.getProperty("user.dir")+"\\src\\main\\resources\\assets\\media";
    public static String mac_linux_path_media = System.getProperty("user.dir")+ File.separator+"/src/main/resources/assets/media";

    public static String MediaDirectory(){
        if(IS_WINDOW){
            return window_path_media;
        }else {return  mac_linux_path_media;
        }
    }
}
