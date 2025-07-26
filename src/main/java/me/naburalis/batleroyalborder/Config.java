package me.naburalis.batleroyalborder;

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

public class Config {
    public static Path configPath = FabricLoader.getInstance().getConfigDir();
    public static String properties = configPath + "/batleroyalborder.properties";

    public static Integer delayTime;
    public static Integer shrinkTime;
    public static Double shrinkAmount;

    public static void loadConfig() {
        Properties BRBConfig = new Properties();
        try {
            BRBConfig.load(new FileInputStream(properties));
            delayTime = Integer.parseInt(BRBConfig.getProperty("simpleroyal.delayTime"));
            shrinkTime = Integer.parseInt(BRBConfig.getProperty("simpleroyal.shrinkTime"));
            shrinkAmount = Double.parseDouble(BRBConfig.getProperty("simpleroyal.shrinkAmount"));
        } catch (IOException e) {
            BatleRoyalBorder.LOGGER.error("Failed to load config file!", e);
        }
    }

    public static boolean exist() {
        return new File(properties).exists();
    }

    public static void writeDefaultConfig() {
        try (Writer writer = new FileWriter(properties)){
            writer.write("# Batle Royal Border config file\n");
            writer.write("simpleroyal.delayTime=10000\n"); // 10 seconds
            writer.write("simpleroyal.shrinkTime=30000\n"); // 30 seconds
            writer.write("simpleroyal.shrinkAmount=10.0\n");
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
}
