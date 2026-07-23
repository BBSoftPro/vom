package io.vom.appium;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.vom.utils.DeviceManager;
import io.vom.utils.Properties;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Basisbank.ge
 * Lasha Bibilashvili
 * Date: 21-Jul-26 Time: 4:18 PM
 */
public class CapabilitiesFactory {
    public static Capabilities createCapabilities(String platform) {
        var prop = Properties.getInstance();
        Map<String, Object> platformCaps = loadCapsFromJson(platform, prop);

        String currentDeviceId = DeviceManager.getDeviceId();

        if ("android".equalsIgnoreCase(platform)) {
            UiAutomator2Options options = new UiAutomator2Options();
            platformCaps.forEach(options::setCapability);

            if (currentDeviceId != null && !currentDeviceId.isEmpty()) {
                options.setUdid(currentDeviceId);
                options.setDeviceName(currentDeviceId);
            }

//            System.out.println("--> [CapabilitiesFactory] Loaded Android Caps for device [" + currentDeviceId + "]: " + options.asMap());
            return options;

        } else if ("ios".equalsIgnoreCase(platform)) {
            XCUITestOptions options = new XCUITestOptions();
            platformCaps.forEach(options::setCapability);

            if (currentDeviceId != null && !currentDeviceId.isEmpty()) {
                options.setUdid(currentDeviceId);
                options.setDeviceName(currentDeviceId);
            }

//            System.out.println("--> [CapabilitiesFactory] Loaded iOS Caps for device [" + currentDeviceId + "]: " + options.asMap());
            return options;
        }

        MutableCapabilities generalCaps = new MutableCapabilities();
        platformCaps.forEach(generalCaps::setCapability);
        if (currentDeviceId != null && !currentDeviceId.isEmpty()) {
            generalCaps.setCapability("udid", currentDeviceId);
            generalCaps.setCapability("appium:deviceName", currentDeviceId);
        }
        return generalCaps;
    }

    private static Map<String, Object> loadCapsFromJson(String platform, Properties prop) {
        String capabilitiesPath = prop.getProperty("capabilities_json_path", "capabilities.json");
        Reader reader = null;

        try {
            File file = new File(System.getProperty("user.dir"), capabilitiesPath);

            if (file.exists()) {
                reader = new FileReader(file);
            } else {
                InputStream inputStream = CapabilitiesFactory.class.getClassLoader().getResourceAsStream(capabilitiesPath);
                if (inputStream == null) {
                    inputStream = CapabilitiesFactory.class.getResourceAsStream("/" + capabilitiesPath);
                }

                if (inputStream != null) {
                    reader = new InputStreamReader(inputStream);
                }
            }

            if (reader == null) {
                System.err.println("❌ ERROR: Could not find " + capabilitiesPath + " in project root or resources folder!");
                return Collections.emptyMap();
            }

            // ✅ ტიპი შევცვალეთ: Map<String, List<Map<String, Object>>>
            Type type = new TypeToken<Map<String, List<Map<String, Object>>>>() {
            }.getType();
            Map<String, List<Map<String, Object>>> map = new Gson().fromJson(reader, type);
            reader.close();

            if (map != null && map.containsKey(platform)) {
                List<Map<String, Object>> platformCapsList = map.get(platform);
                if (!platformCapsList.isEmpty()) {
                    // იღებს პირველ კონფიგურაციას სიიდან, როგორც ბაზისურ Cap-ს
                    return platformCapsList.get(0);
                }
            } else {
                System.err.println("❌ ERROR: Platform '" + platform + "' not found in JSON file!");
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to load capabilities JSON from path: " + capabilitiesPath);
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }
}
