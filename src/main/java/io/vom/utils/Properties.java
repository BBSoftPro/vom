package io.vom.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

public class Properties {

    public static final Duration DEFAULT_SCROLL_DURATION;
    public static final int DEFAULT_SCROLL_LENGTH;

    static {
        int durationMillis = 100;
        int length = 300;
        try {
            durationMillis = Integer.parseInt(Properties.getInstance().getProperty("default_scroll_duration_in_millis", "100"));
            length = Integer.parseInt(Properties.getInstance().getProperty("default_scroll_length", "300"));
        } catch (Exception ignore) {
            // თუ ინიციალიზაციისას ფაილი ჯერ მზად არაა, ვიყენებთ default მნიშვნელობებს
        }
        DEFAULT_SCROLL_DURATION = Duration.ofMillis(durationMillis);
        DEFAULT_SCROLL_LENGTH = length;
    }

    private java.util.Properties properties;
    private static volatile Properties _instance; // volatile უზრუნველყოფს ხილვადობას ნაკადებს შორის

    public String getUserDir() {
        return System.getProperty("user.dir");
    }

    public static Properties getInstance() {
        Properties result = _instance;
        if (result == null) {
            synchronized (Properties.class) {
                result = _instance;
                if (result == null) {
                    java.util.Properties props = new java.util.Properties();
                    File file = new File(System.getProperty("user.dir"), "properties.properties");
                    if (!file.isFile()) {
                        file = new File(Objects.requireNonNull(Properties.class.getResource("/properties.properties")
                                , "properties was not found").getFile());
                    }
                    try (FileReader reader = new FileReader(file)) {
                        props.load(reader);
                        _instance = result = new Properties();
                        result.properties = props;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return result;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}