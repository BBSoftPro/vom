package io.vom.utils;

import java.util.Objects;

public class Selector {
    private String name;
    private String type;
    private String value;
    private String platform;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }



    public static Selector from(String type,String value){
        Objects.requireNonNull(type);
        Objects.requireNonNull(value);

        var s = new Selector();
        s.type = type;
        s.value = value;

        return s;
    }

    public static Selector from(String name,String type,String value){
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        Objects.requireNonNull(value);

        var s = new Selector();
        s.name = name;
        s.type = type;
        s.value = value;

        return s;
    }

    public static Selector from(String name,String type,String value,String platform){
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        Objects.requireNonNull(value);
        Objects.requireNonNull(platform);

        var s = new Selector();
        s.name = name;
        s.type = type;
        s.value = value;
        s.platform = platform;

        return s;
    }

    @Override
    public String toString() {
        return "Selector{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }
}
