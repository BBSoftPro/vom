package io.vom.utils;

/**
 * Basisbank.ge
 * Lasha Bibilashvili
 * Date: 22-Jul-26 Time: 12:57 AM
 */
public class PortManager {
    private static final ThreadLocal<String> portThreadLocal = new ThreadLocal<>();

    public static void setPort(String port) {
        portThreadLocal.set(port);
    }

    public static String getPort() {
        return portThreadLocal.get();
    }

    public static void clear() {
        portThreadLocal.remove();
    }
}
