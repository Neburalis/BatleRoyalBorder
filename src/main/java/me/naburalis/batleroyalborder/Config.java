package me.naburalis.batleroyalborder;

import net.fabricmc.loader.api.FabricLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.file.Path;

public class Config {
    /*{
          "simpleroyal": {
            "delayTime": 10000,
            "shrinkTime": 30000,
            "shrinkAmount": 10.0,
            "bossBar": {
              "enabled": true,
              "title": {
                "countdown": "Border will shrink to {size} blocks in {time}",
                "shrinking": "Shrinking border to {size} blocks...",
                "completed": "Border has reached its final size."
              },
              "color": "RED"
            },
            "chatNotification": {
              "enabled": false,
              "messages": {
                "countdown": "Border will shrink to {size} blocks in {time}",
                "shrinking": "Shrinking border to {size} blocks...",
                "completed": "Border has reached its final size."
              },
              "notify": [
                "1s", "2s", "3s", "4s", "5s",
                "10s", "20s", "30s",
                "1m", "2m", "3m", "4m", "5m",
                "10m", "20m", "30m",
                "1h", "2h", "3h", "4h", "5h",
                "10h", "20h", "30h"
              ],
              "sound": {
                "enabled": true,
                "name": ""
              }
            }
          }
    }*/

    public static Path configPath = FabricLoader.getInstance().getConfigDir();
    public static String properties = configPath + "/batleroyalborder.json";

    public static Integer delayTime;
    public static Integer shrinkTime;
    public static Double shrinkAmount;
    public static String bossBarColor;
    
    // Store the entire config structure in memory
    private static JsonObject configInMemory;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void loadConfig() {
        try (FileReader reader = new FileReader(properties)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            
            // Store the entire config in memory
            configInMemory = root.deepCopy();
            
            JsonObject simpleroyal = root.getAsJsonObject("simpleroyal");
            
            if (simpleroyal != null) {
                delayTime = simpleroyal.has("delayTime") ? simpleroyal.get("delayTime").getAsInt() : 10000;
                shrinkTime = simpleroyal.has("shrinkTime") ? simpleroyal.get("shrinkTime").getAsInt() : 30000;
                shrinkAmount = simpleroyal.has("shrinkAmount") ? simpleroyal.get("shrinkAmount").getAsDouble() : 10.0;
                
                JsonObject bossBar = simpleroyal.getAsJsonObject("bossBar");
                if (bossBar != null && bossBar.has("color")) {
                    bossBarColor = bossBar.get("color").getAsString();
                } else {
                    bossBarColor = "RED";
                }
            } else {
                // Fallback to defaults if simpleroyal section is missing
                delayTime = 10000;
                shrinkTime = 30000;
                shrinkAmount = 10.0;
                bossBarColor = "RED";
            }
            
        } catch (IOException e) {
            BatleRoyalBorder.LOGGER.error("Failed to load config file!", e);
            // Set defaults if config loading fails
            delayTime = 10000;
            shrinkTime = 30000;
            shrinkAmount = 10.0;
            bossBarColor = "RED";
        }
    }

    public static void saveConfig() {
        if (configInMemory == null) {
            BatleRoyalBorder.LOGGER.error("No config in memory to save!");
            return;
        }
        
        try (FileWriter writer = new FileWriter(properties)) {
            gson.toJson(configInMemory, writer);
        } catch (IOException e) {
            BatleRoyalBorder.LOGGER.error("Failed to save config!", e);
        }
    }

    // Generic getter methods to access config values from memory by path
    public static Integer getInteger(String path) {
        if (configInMemory == null) return null;
        
        String[] parts = path.split("\\.");
        JsonObject current = configInMemory;
        
        for (int i = 0; i < parts.length - 1; i++) {
            if (!current.has(parts[i]) || !current.get(parts[i]).isJsonObject()) {
                return null;
            }
            current = current.getAsJsonObject(parts[i]);
        }
        
        String lastPart = parts[parts.length - 1];
        if (current.has(lastPart) && current.get(lastPart).isJsonPrimitive()) {
            return current.get(lastPart).getAsInt();
        }
        
        return null;
    }

    public static Double getDouble(String path) {
        if (configInMemory == null) return null;
        
        String[] parts = path.split("\\.");
        JsonObject current = configInMemory;
        
        for (int i = 0; i < parts.length - 1; i++) {
            if (!current.has(parts[i]) || !current.get(parts[i]).isJsonObject()) {
                return null;
            }
            current = current.getAsJsonObject(parts[i]);
        }
        
        String lastPart = parts[parts.length - 1];
        if (current.has(lastPart) && current.get(lastPart).isJsonPrimitive()) {
            return current.get(lastPart).getAsDouble();
        }
        
        return null;
    }

    public static String getString(String path) {
        if (configInMemory == null) return null;
        
        String[] parts = path.split("\\.");
        JsonObject current = configInMemory;
        
        for (int i = 0; i < parts.length - 1; i++) {
            if (!current.has(parts[i]) || !current.get(parts[i]).isJsonObject()) {
                return null;
            }
            current = current.getAsJsonObject(parts[i]);
        }
        
        String lastPart = parts[parts.length - 1];
        if (current.has(lastPart) && current.get(lastPart).isJsonPrimitive()) {
            return current.get(lastPart).getAsString();
        }
        
        return null;
    }

    public static Boolean getBoolean(String path) {
        if (configInMemory == null) return null;
        
        String[] parts = path.split("\\.");
        JsonObject current = configInMemory;
        
        for (int i = 0; i < parts.length - 1; i++) {
            if (!current.has(parts[i]) || !current.get(parts[i]).isJsonObject()) {
                return null;
            }
            current = current.getAsJsonObject(parts[i]);
        }
        
        String lastPart = parts[parts.length - 1];
        if (current.has(lastPart) && current.get(lastPart).isJsonPrimitive()) {
            return current.get(lastPart).getAsBoolean();
        }
        
        return null;
    }

    public static JsonObject getJsonObject(String path) {
        if (configInMemory == null) return null;
        
        String[] parts = path.split("\\.");
        JsonObject current = configInMemory;
        
        for (String part : parts) {
            if (!current.has(part) || !current.get(part).isJsonObject()) {
                return null;
            }
            current = current.getAsJsonObject(part);
        }
        
        return current;
    }

    public static JsonArray getJsonArray(String path) {
        if (configInMemory == null) return null;
        
        String[] parts = path.split("\\.");
        JsonObject current = configInMemory;
        
        for (int i = 0; i < parts.length - 1; i++) {
            if (!current.has(parts[i]) || !current.get(parts[i]).isJsonObject()) {
                return null;
            }
            current = current.getAsJsonObject(parts[i]);
        }
        
        String lastPart = parts[parts.length - 1];
        if (current.has(lastPart) && current.get(lastPart).isJsonArray()) {
            return current.getAsJsonArray(lastPart);
        }
        
        return null;
    }

    public static JsonObject getConfigInMemory() {
        return configInMemory;
    }

    public static boolean exist() {
        return new File(properties).exists();
    }

    public static void writeDefaultConfig() {
        /*
        {
          "simpleroyal": {
            "delayTime": 10000,
            "shrinkTime": 30000,
            "shrinkAmount": 10.0,
            "bossBar": {
              "enabled": true,
              "title": {
                "countdown": "Border will shrink to {size} blocks in {time}",
                "shrinking": "Shrinking border to {size} blocks...",
                "completed": "Border has reached its final size."
              },
              "color": "RED"
            },
            "chatNotification": {
              "enabled": false,
              "messages": {
                "countdown": "Border will shrink to {size} blocks in {time}",
                "shrinking": "Shrinking border to {size} blocks...",
                "completed": "Border has reached its final size."
              },
              "notify": [
                "1s", "2s", "3s", "4s", "5s",
                "10s", "20s", "30s",
                "1m", "2m", "3m", "4m", "5m",
                "10m", "20m", "30m",
                "1h", "2h", "3h", "4h", "5h",
                "10h", "20h", "30h"
              ],
              "sound": {
                "enabled": true,
                "name": ""
              }
            }
          }
        }
         */
        JsonObject root = new JsonObject();
        JsonObject simpleroyal = new JsonObject();
        
        // Basic settings
        simpleroyal.addProperty("delayTime", "10s");
        simpleroyal.addProperty("shrinkTime", "30s");
        simpleroyal.addProperty("shrinkAmount", "10");
        
        // Boss bar settings
        JsonObject bossBar = new JsonObject();
        bossBar.addProperty("enabled", true);
        
        JsonObject bossBarTitle = new JsonObject();
        bossBarTitle.addProperty("countdown", "Border will shrink to {size} blocks in {time}");
        bossBarTitle.addProperty("shrinking", "Shrinking border to {size} blocks...");
        bossBarTitle.addProperty("completed", "Border has reached its final size.");
        bossBar.add("title", bossBarTitle);
        
        bossBar.addProperty("color", "RED");
        simpleroyal.add("bossBar", bossBar);
        
        // Chat notification settings
        JsonObject chatNotification = new JsonObject();
        chatNotification.addProperty("enabled", false);
        
        JsonObject chatMessages = new JsonObject();
        chatMessages.addProperty("countdown", "Border will shrink to {size} blocks in {time}");
        chatMessages.addProperty("shrinking", "Shrinking border to {size} blocks...");
        chatMessages.addProperty("completed", "Border has reached its final size.");
        chatNotification.add("messages", chatMessages);
        
        JsonArray chatNotify = new JsonArray();
        chatNotify.add("1s");
        chatNotify.add("2s");
        chatNotify.add("3s");
        chatNotify.add("4s");
        chatNotify.add("5s");
        chatNotify.add("10s");
        chatNotify.add("20s");
        chatNotify.add("30s");
        chatNotify.add("1m");
        chatNotify.add("2m");
        chatNotify.add("3m");
        chatNotify.add("4m");
        chatNotify.add("5m");
        chatNotify.add("10m");
        chatNotify.add("20m");
        chatNotify.add("30m");
        chatNotify.add("1h");
        chatNotify.add("2h");
        chatNotify.add("3h");
        chatNotify.add("4h");
        chatNotify.add("5h");
        chatNotify.add("10h");
        chatNotify.add("20h");
        chatNotify.add("30h");
        chatNotification.add("notify", chatNotify);
        
        JsonObject chatSound = new JsonObject();
        chatSound.addProperty("enabled", true);
        chatSound.addProperty("name", "");
        chatNotification.add("sound", chatSound);
        
        simpleroyal.add("chatNotification", chatNotification);
        
        root.add("simpleroyal", simpleroyal);
        
        // Store the default config in memory
        configInMemory = root.deepCopy();
        
        try (FileWriter writer = new FileWriter(properties)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            BatleRoyalBorder.LOGGER.error("Failed to write default config!", e);
        }
    }

    public static void createConfig() {
        File configFile = new File(properties);
        try {
            configFile.createNewFile();
        }
        catch (IOException e) {
            BatleRoyalBorder.LOGGER.error("Failed to create config file!", e);
        }
    }

    // Generic setter methods to update config values in memory by path, with type check
    public static boolean setInteger(String path, int value) {
        if (configInMemory == null) return false;
        String[] parts = path.split("\\.");
        JsonObject current = configInMemory;
        for (int i = 0; i < parts.length - 1; i++) {
            if (!current.has(parts[i]) || !current.get(parts[i]).isJsonObject()) {
                // create intermediate objects if missing
                JsonObject newObj = new JsonObject();
                current.add(parts[i], newObj);
                current = newObj;
            } else {
                current = current.getAsJsonObject(parts[i]);
            }
        }
        String lastPart = parts[parts.length - 1];
        if (current.has(lastPart)) {
            if (!current.get(lastPart).isJsonPrimitive() || !current.get(lastPart).getAsJsonPrimitive().isNumber()) {
                return false;
            }
        }
        current.addProperty(lastPart, value);
        return true;
    }

    public static boolean setDouble(String path, double value) {
        if (configInMemory == null) return false;
        String[] parts = path.split("\\.");
        JsonObject current = configInMemory;
        for (int i = 0; i < parts.length - 1; i++) {
            if (!current.has(parts[i]) || !current.get(parts[i]).isJsonObject()) {
                JsonObject newObj = new JsonObject();
                current.add(parts[i], newObj);
                current = newObj;
            } else {
                current = current.getAsJsonObject(parts[i]);
            }
        }
        String lastPart = parts[parts.length - 1];
        if (current.has(lastPart)) {
            if (!current.get(lastPart).isJsonPrimitive() || !current.get(lastPart).getAsJsonPrimitive().isNumber()) {
                return false;
            }
        }
        current.addProperty(lastPart, value);
        return true;
    }

    public static boolean setString(String path, String value) {
        if (configInMemory == null) return false;
        String[] parts = path.split("\\.");
        JsonObject current = configInMemory;
        for (int i = 0; i < parts.length - 1; i++) {
            if (!current.has(parts[i]) || !current.get(parts[i]).isJsonObject()) {
                JsonObject newObj = new JsonObject();
                current.add(parts[i], newObj);
                current = newObj;
            } else {
                current = current.getAsJsonObject(parts[i]);
            }
        }
        String lastPart = parts[parts.length - 1];
        if (current.has(lastPart)) {
            if (!current.get(lastPart).isJsonPrimitive() || !current.get(lastPart).getAsJsonPrimitive().isString()) {
                return false;
            }
        }
        current.addProperty(lastPart, value);
        return true;
    }

    public static boolean setBoolean(String path, boolean value) {
        if (configInMemory == null) return false;
        String[] parts = path.split("\\.");
        JsonObject current = configInMemory;
        for (int i = 0; i < parts.length - 1; i++) {
            if (!current.has(parts[i]) || !current.get(parts[i]).isJsonObject()) {
                JsonObject newObj = new JsonObject();
                current.add(parts[i], newObj);
                current = newObj;
            } else {
                current = current.getAsJsonObject(parts[i]);
            }
        }
        String lastPart = parts[parts.length - 1];
        if (current.has(lastPart)) {
            if (!current.get(lastPart).isJsonPrimitive() || !current.get(lastPart).getAsJsonPrimitive().isBoolean()) {
                return false;
            }
        }
        current.addProperty(lastPart, value);
        return true;
    }
}
