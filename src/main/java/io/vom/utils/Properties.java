package io.vom.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

public class Properties {

    public static final Duration DEFAULT_SCROLL_DURATION = Duration.ofMillis(Integer.parseInt(Properties.getInstance().getProperty("default_scroll_duration_in_millis", "100")));
    public static final int DEFAULT_SCROLL_LENGTH = Integer.parseInt(Properties.getInstance().getProperty("default_scroll_length", "300"));


    private java.util.Properties properties;

    private static Properties _instance;

    public String getUserDir(){
        return System.getProperty("user.dir");
    }

    public static Properties getInstance(){
        if (_instance == null){
            java.util.Properties props = new java.util.Properties();
            File file = new File(System.getProperty("user.dir"), "properties.properties");
            if (!file.isFile()){
               file = new File( Objects.requireNonNull(Properties.class.getResource("/properties.properties")
                       ,"properties was not found create properties.properties into project directory or into resource directory").getFile());
            }
            try {
                props.load(new FileReader(file));
               _instance = new Properties();
               _instance.properties = props;
            } catch (IOException e) {
               throw new RuntimeException(e);
            }
        }

        return _instance;
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }

    public String getProperty(String key,String defaultValue){
        return properties.getProperty(key,defaultValue);
    }
}
