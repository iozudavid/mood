package com.knightlore;

import java.io.BufferedReader;
import java.io.FileReader;

import com.knightlore.game.GameMode;
import com.knightlore.game.manager.GameManager;
import com.knightlore.network.ConnectionDetails;
import com.knightlore.utils.Utils;

/**
 * A parser for the game config file located at res/config.txt
 * 
 * @author James
 *
 */
public class ConfigParser {
    
    private static final String PREFIX = "//";
    private static String filePath = "res/config.txt";
    
    public ConfigParser() {
        
    }
    
    /**
     * Parses the config file located at res/config.txt. This sets various
     * important game variables before the game is loaded.
     */
    public static void doParse() {
        
        try (FileReader file = new FileReader(filePath); BufferedReader reader = new BufferedReader(file)) {
            
            String line = reader.readLine();
            while (line != null) {
                
                // remove whitespace
                line.trim();
                // ignore comments
                if (line.startsWith(PREFIX) || line.length() == 0) {
                    line = reader.readLine();
                    continue;
                }
                // parse line
                String[] data = line.split("=");
                // length check
                if (data.length != 2) {
                    System.err.println("Invalid line in config file, ignoring.");
                    line = reader.readLine();
                    continue;
                }
                
                // try parse the line
                parseLine(data[0].trim(), data[1].trim());
                
                // next line
                line = reader.readLine();
                
            }
            
        } catch (Exception e) {
            System.err.println("Failed to load config");
            e.printStackTrace();
        }
    }
    
    private static void parseLine(String keyStr, String valStr) {
        
        switch (keyStr) {
        case "dest_port":
            if (GameSettings.isServer()) {
                break;
            }
            try {
                int destPort = Integer.parseInt(valStr);
                ConnectionDetails.DEFAULT_PORT = destPort;
            } catch (Exception e) {
                System.err.println("Failed to read dest_port, value given=" + valStr);
            }
            break;
        case "map_width":
            try {
                int map_width = Integer.parseInt(valStr);
                if (map_width < 16) {
                    map_width = 16;
                }
                GameSettings.mapWidth = map_width;
            } catch (Exception e) {
                System.err.println("Failed to read map_width, value given=" + valStr);
            }
            break;
        case "map_height":
            try {
                int map_height = Integer.parseInt(valStr);
                if (map_height < 16) {
                    map_height = 16;
                }
                GameSettings.mapHeight = map_height;
            } catch (Exception e) {
                System.err.println("Failed to read map_height, value given=" + valStr);
            }
            break;
        case "random_map":
            try {
                boolean random_map = Boolean.parseBoolean(valStr);
                GameSettings.randomMap = random_map;
            } catch (Exception e) {
                System.err.println("Failed to read random_map, value given=" + valStr);
            }
            break;
        case "map_seed":
            try {
                int map_seed = Integer.parseInt(valStr);
                GameSettings.mapSeed = map_seed;
            } catch (Exception e) {
                System.err.println("Failed to read map_seed, value given=" + valStr);
            }
            break;
        case "listen_port":
            if (GameSettings.isClient()) {
                break;
            }
            try {
                int listenPort = Integer.parseInt(valStr);
                ConnectionDetails.PORT = listenPort;
            } catch (Exception e) {
                System.err.println("Failed to read listen_port, value given=" + valStr);
            }
            break;
        case "game_mode":
            if (valStr.equals("FFA")) {
                GameManager.desiredGameMode = GameMode.FFA;
            } else if (valStr.equals("TDM")) {
                GameManager.desiredGameMode = GameMode.TDM;
            } else if (valStr.equals("ZOM")) {
                GameManager.desiredGameMode = GameMode.SURVIVAL;
            } else {
                System.err.println("Failed to read game_mode, value given=" + valStr);
            }
            break;
        case "num_bots":
            try {
                int numBots = Integer.parseInt(valStr);
                if (numBots < 0) {
                    System.err.println("number of bots must be between 0 and 20");
                    numBots = Utils.clamp(numBots, 0, 20);
                }
                GameManager.numBots = numBots;
            } catch (Exception e) {
                System.err.println("Failed to read num_bots value given=" + valStr);
            }
            break;
        case "num_zombies":
            try {
                int numZoms = Integer.parseInt(valStr);
                if (numZoms < 0) {
                    System.err.println("number of zombies must be between 0 and 50");
                    numZoms = Utils.clamp(numZoms, 0, 50);
                }
                GameManager.numZombies = numZoms;
            } catch (Exception e) {
                System.err.println("Failed to read num_bots value given=" + valStr);
            }
            break;
        default:
            System.err.println("unknown config: " + keyStr + "=" + valStr);
            break;
        }
        
    }
    
}
