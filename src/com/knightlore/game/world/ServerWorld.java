package com.knightlore.game.world;

import java.util.Iterator;

import com.knightlore.game.BotInput;
import com.knightlore.game.Team;
import com.knightlore.game.entity.Entity;
import com.knightlore.game.entity.Player;
import com.knightlore.game.entity.SpectatorCamera;
import com.knightlore.game.entity.ZombieServer;
import com.knightlore.game.manager.FFAGameManager;
import com.knightlore.game.manager.GameManager;
import com.knightlore.game.manager.TDMGameManager;
import com.knightlore.utils.Vector2D;

public class ServerWorld extends GameWorld {
    
    @Override
    public void setUpWorld(Long mapSeed) {
        super.setUpWorld(mapSeed);
        switch (GameManager.desiredGameMode) {
        case TDM:
            gameManager = new TDMGameManager();
            break;
        case FFA:
        default:
            gameManager = new FFAGameManager();
            break;
        }
        
        gameManager.init();
        buildEntities();
    }
    
    private void buildEntities() {
        for (int i = 0; i < GameManager.numZombies; i++) {
            ZombieServer zom = new ZombieServer(map.getRandomSpawnPoint());
            zom.init();
            this.addEntity(zom);
        }
        
        for (int i = 0; i < GameManager.numBots; i++) {
            Player botPlayer = new Player(map.getRandomSpawnPoint(), Vector2D.UP);
            botPlayer.setInputModule(new BotInput());
            botPlayer.team = Team.BLUE;
            botPlayer.init();
            botPlayer.setName("bot" + i);
            playerManager.addPlayer(botPlayer);
        }
        
        SpectatorCamera cam = new SpectatorCamera(new Vector2D(10, 20), Vector2D.UP);
        cam.init();
        this.addEntity(cam);
    }
    
    @Override
    public void update() {
        super.update();
        Iterator<Player> playerIter = playerManager.getPlayerIterator();
        while (playerIter.hasNext()) {
            Player player = playerIter.next();
            Iterator<Entity> it = this.getEntityIterator();
            while (it.hasNext()) {
                Entity ent = it.next();
                if (player.getBoundingRectangle().intersects(ent.getBoundingRectangle())) {
                    ent.onCollide(player);
                }
            }
        }
    }
    
    public Player createPlayer() {
        Vector2D pos = map.getRandomSpawnPoint();
        Team team = Team.NONE;
        if (gameManager instanceof TDMGameManager) {
            if (playerManager.numPlayers(Team.BLUE) <= playerManager.numPlayers(Team.RED)) {
                System.out.println("BLUE");
                team = Team.BLUE;
            } else {
                System.out.println("RED");
                team = Team.RED;
            }
        }
        Player player = new Player(pos, Vector2D.UP,team);
        player.init();
        player.sendSystemMessage("System: Player " + player.getName() + " " + " has connected.");
        playerManager.addPlayer(player);
        return player;
    }
    
    @Override
    public void onPostEngineInit() {
        
    }
}
