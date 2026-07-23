package io.vom.utils;

/**
 * Basisbank.ge
 * Lasha Bibilashvili
 * Date: 21-Jul-26 Time: 5:29 PM
 */
public class DeviceManager {
    private static final ThreadLocal<String> deviceIdThreadLocal = new InheritableThreadLocal<>();

    public static void setDeviceId(String deviceId) {
        deviceIdThreadLocal.set(deviceId);
    }

    public static String getDeviceId() {
        return deviceIdThreadLocal.get();
    }

    public static void clear() {
        deviceIdThreadLocal.remove();
    }
}
