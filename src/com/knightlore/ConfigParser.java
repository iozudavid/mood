package com.knightlore;

import java.io.BufferedReader;
import java.io.FileReader;

import com.knightlore.game.GameMode;
import com.knightlore.game.manager.GameManager;
import com.knightlore.network.ConnectionDetails;

/**
 * A parser for the game config file located at res/config.txt
 *
 * @author James
 */
public class ConfigParser {
    
    private static final String PREFIX = "//";
    private static final String FILE_PATH = "res/config.txt";
    
    private static final String DEST_PORT_FIELD = "dest_port";
    private static final String MAP_WIDTH_FIELD = "map_width";
    private static final String MAP_HEIGHT_FIELD = "map_height";
    private static final String RANDOM_MAP_FIELD = "random_map";
    private static final String MAP_SEED_FIELD = "map_seed";
    private static final String LISTEN_PORT_FIELD = "listen_port";
    private static final String GAME_MODE_FIELD = "game_mode";
    private static final String NUM_BOTS_FIELD = "num_bots";
    private static final String NUM_ZOMBIES_FIELD = "num_zombies";
    
    private static final String FFA_MODE_STR = "FFA";
    private static final String TDM_MODE_STR = "TDM";
    private static final String SURVIVAL_MODE_STR = "ZOM";
    
    private static final int MAX_BOTS = 20;
    private static final int MAX_ZOMBIES = 50;
    
    public ConfigParser() {
    }
    
    /**
     * Parses the config file located at res/config.txt. This sets various
     * important game variables before the game is loaded.
     */
    public static void doParse() {
        
        try (FileReader file = new FileReader(FILE_PATH); BufferedReader reader = new BufferedReader(file)) {
            
            String line = reader.readLine();
            while (line != null) {
                
                // remove whitespace
                line = line.trim();
                // ignore comments
                if (line.startsWith(PREFIX) || line.isEmpty()) {
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
            case DEST_PORT_FIELD:
                if (GameSettings.isServer()) {
                    break;
                }
                try {
                    ConnectionDetails.DEFAULT_PORT = clamp(Integer.parseInt(valStr), 1, 65535);
                } catch (Exception e) {
                    System.err.println("Failed to read dest_port, value given=" + valStr);
                }
                break;
            case MAP_WIDTH_FIELD:
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
            case MAP_HEIGHT_FIELD:
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
            case RANDOM_MAP_FIELD:
                try {
                    GameSettings.randomMap = Boolean.parseBoolean(valStr);
                } catch (Exception e) {
                    System.err.println("Failed to read random_map, value given=" + valStr);
                }
                break;
            case MAP_SEED_FIELD:
                try {
                    GameSettings.mapSeed = Integer.parseInt(valStr);
                } catch (Exception e) {
                    System.err.println("Failed to read map_seed, value given=" + valStr);
                }
                break;
            case LISTEN_PORT_FIELD:
                if (GameSettings.isClient()) {
                    break;
                }
                try {
                    ConnectionDetails.PORT = clamp(Integer.parseInt(valStr), 1, 65535);
                } catch (Exception e) {
                    System.err.println("Failed to read listen_port, value given=" + valStr);
                }
                break;
            case GAME_MODE_FIELD:
                switch (valStr) {
                    case FFA_MODE_STR:
                        GameManager.desiredGameMode = GameMode.FFA;
                        break;
                    case TDM_MODE_STR:
                        GameManager.desiredGameMode = GameMode.TDM;
                        break;
                    case SURVIVAL_MODE_STR:
                        GameManager.desiredGameMode = GameMode.SURVIVAL;
                        break;
                    default:
                        System.err.println("Failed to read game_mode, value given=" + valStr);
                        break;
                }
                break;
            case NUM_BOTS_FIELD:
                try {
                    int numBots = Integer.parseInt(valStr);
                    if (numBots < 0) {
                        System.err.println("number of bots must be between 0 and " + MAX_BOTS);
                        numBots = clamp(numBots, 0, MAX_BOTS);
                    }
                    GameManager.numBots = numBots;
                } catch (Exception e) {
                    System.err.println("Failed to read num_bots value given=" + valStr);
                }
                break;
            case NUM_ZOMBIES_FIELD:
                try {
                    int numZoms = Integer.parseInt(valStr);
                    if (numZoms < 0 || numZoms > MAX_ZOMBIES) {
                        System.err.println("number of zombies must be between 0 and " + MAX_ZOMBIES);
                        numZoms = clamp(numZoms, 0, MAX_ZOMBIES);
                    }
                    GameManager.numZombies = numZoms;
                } catch (Exception e) {
                    System.err.println("Failed to read num_zombies value given=" + valStr);
                }
                
                break;
            default:
                System.err.println("unknown config: " + keyStr + "=" + valStr);
                break;
        }
    }
    
    private static int clamp(int val, int min, int max) {
        if (val < min) {
            return min;
        }
        if (val > max) {
            return max;
        }
        return val;
    }
}
