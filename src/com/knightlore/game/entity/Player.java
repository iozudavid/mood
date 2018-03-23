package com.knightlore.game.entity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.collect.ImmutableMap;
import com.knightlore.GameSettings;
import com.knightlore.engine.GameEngine;
import com.knightlore.engine.audio.Grunt;
import com.knightlore.engine.audio.SoundManager;
import com.knightlore.game.InputModule;
import com.knightlore.game.RemoteInput;
import com.knightlore.game.buff.Immune;
import com.knightlore.game.buff.SpawnVision;
import com.knightlore.game.entity.weapon.Weapon;
import com.knightlore.game.entity.weapon.WeaponType;
import com.knightlore.game.manager.GameManager;
import com.knightlore.game.world.ClientWorld;
import com.knightlore.network.NetworkObject;
import com.knightlore.network.NetworkObjectManager;
import com.knightlore.network.protocol.ClientController;
import com.knightlore.network.protocol.ClientProtocol;
import com.knightlore.network.protocol.NetworkUtils;
import com.knightlore.render.PixelBuffer;
import com.knightlore.render.animation.Animation;
import com.knightlore.render.animation.PlayerMoveAnimation;
import com.knightlore.render.animation.PlayerStandAnimation;
import com.knightlore.render.graphic.Graphic;
import com.knightlore.render.graphic.PlayerGraphicMatrix;
import com.knightlore.render.graphic.PlayerGraphicMatrix.Color;
import com.knightlore.render.graphic.PlayerGraphicMatrix.Stance;
import com.knightlore.render.graphic.sprite.DirectionalSprite;
import com.knightlore.utils.Vector2D;

public class Player extends Entity {

    private PlayerMoveAnimation moveAnim = new PlayerMoveAnimation(PlayerGraphicMatrix.getGraphic(
            PlayerGraphicMatrix.Color.BLUE, PlayerGraphicMatrix.Weapon.PISTOL, PlayerGraphicMatrix.Stance.MOVE));

    private PlayerStandAnimation standAnim = new PlayerStandAnimation(
            PlayerGraphicMatrix.getGraphic(PlayerGraphicMatrix.Color.BLUE, PlayerGraphicMatrix.Weapon.PISTOL,
                    PlayerGraphicMatrix.Stance.STAND),
            (long) (GameEngine.UPDATES_PER_SECOND / 10));

    private Animation<DirectionalSprite> currentAnim = standAnim;

    public static final int MAX_HEALTH = 100;
    private static final double SIZE = 0.25;
    // Maps all inputs that the player could be making to their values.
    private final ImmutableMap<ClientController, Runnable> ACTION_MAPPINGS = ImmutableMap
            .<ClientController, Runnable>builder().put(ClientController.FORWARD, this::moveForward)
            .put(ClientController.ROTATE_ANTI_CLOCKWISE, this::rotateAntiClockwise)
            .put(ClientController.BACKWARD, this::moveBackward)
            .put(ClientController.ROTATE_CLOCKWISE, this::rotateClockwise).put(ClientController.LEFT, this::strafeLeft)
            .put(ClientController.RIGHT, this::strafeRight).put(ClientController.SHOOT, this::shoot).build();

    private final BlockingQueue<ByteBuffer> teamMessagesToSend = new LinkedBlockingQueue<>();
    private final BlockingQueue<ByteBuffer> allMessagesToSend = new LinkedBlockingQueue<>();
    private Map<ClientController, Byte> inputState = new HashMap<>();

    private int score = 0;
    private int currentHealth = MAX_HEALTH;
    private Weapon currentWeapon;
    private WeaponType currentWeaponType;
    private boolean shootOnNextUpdate;

    private boolean hasShot;
    private Vector2D prevPos, prevDir;

    private int inertiaX = 0, inertiaY = 0;
    private InputModule inputModule = new RemoteInput();

    private Entity lastInflictor;

    // DO NOT REMOVE, THESE ARE USED FOR CLIENT PREDICTION!!!!!
    private double timeToSend = 0;
    private boolean respawn = false;

    private int previousHealth;
    // END DO NOT REMOVE

    // Returns a new instance. See NetworkObject for details.
    public static NetworkObject build(UUID uuid, ByteBuffer state) {
        System.out.println("Player build, state size: " + state.remaining());
        NetworkObject obj = new Player(uuid, Vector2D.ONE, Vector2D.ONE);
        obj.init();
        obj.deserialize(state);
        return obj;
    }

    public Player(UUID uuid, Vector2D pos, Vector2D dir) {
        super(uuid, SIZE, pos, dir);
        setNetworkConsumers();

        zOffset = 100;
        moveSpeed = 0.060;
        strafeSpeed = moveSpeed / 2;
        rotationSpeed = 0.06;

        // add tick listener to game engine
        // as some buffs will affect the player periodically
        GameEngine.ticker.addTickListener(this);

        this.setCurrentWeaponType(WeaponType.PISTOL);

        // Player.this.finished = true;
    }

    public Player(Vector2D pos, Vector2D dir) {
        this(UUID.randomUUID(), pos, dir);
    }

    public void sendStatsToScoreBoard() {
        if (GameSettings.isServer())
            return;
        // add to scoreboard
        CopyOnWriteArrayList<String> stats = new CopyOnWriteArrayList<>();
        stats.add(this.getObjectId().toString());
        stats.add(this.getName());
        stats.add(this.team.toString());
        stats.add(this.score + "");
        ClientWorld world = (ClientWorld) GameEngine.getSingleton().getWorld();
        world.getGameChat().addToTable(stats);
    }

    @Override
    public void render(PixelBuffer pix, int x, int y, double distanceTraveled) {
        super.render(pix, x, y, distanceTraveled);

        if (currentWeapon != null) {
            currentWeapon.render(pix, x, y, inertiaX, inertiaY, distanceTraveled, hasShot);
        }
    }

    private void setNetworkConsumers() {
        networkConsumers.put("setInputState", this::setInputState);
        networkConsumers.put("messageToTeam", this::messageToTeam);
        networkConsumers.put("messageToAll", this::messageToAll);
    }

    private void setInputState(ByteBuffer buf) {
        this.timeToSend = buf.getDouble();
        synchronized (inputState) {
            while (buf.hasRemaining()) {
                try {
                    ClientController control = ClientProtocol.getByIndex(buf.getInt());
                    Byte value = buf.get();
                    inputState.put(control, value);
                } catch (IOException e) {
                    System.err.println("Index not good... " + e.getMessage());
                }
            }
        }

    }

    public void setInputState(byte[] inputs) {
        synchronized (inputState) {
            for (int i = 0; i < inputs.length; i = i + 2) {
                // take the control
                // using client protocol
                // to fetch the order
                ClientController control = null;
                try {
                    control = ClientProtocol.getByIndex(inputs[i]);
                } catch (IOException e) {
                    System.err.println("Index not good... " + e.getMessage());
                }
                Byte value = inputs[i + 1];
                inputState.put(control, value);
            }
        }
    }

    private void messageToTeam(ByteBuffer buf) {
        String message = NetworkUtils.getStringFromBuf(buf);
        message = "[" + this.team + "] " + this.getName() + ": " + message;
        ByteBuffer bf = ByteBuffer.allocate(NetworkObject.BYTE_BUFFER_DEFAULT_SIZE);
        NetworkUtils.putStringIntoBuf(bf, NetworkObjectManager.MANAGER_UUID.toString());
        NetworkUtils.putStringIntoBuf(bf, "displayMessage");
        NetworkUtils.putStringIntoBuf(bf, message);
        this.teamMessagesToSend.offer(bf);
    }

    public Optional<ByteBuffer> getTeamMessages() {
        if (this.teamMessagesToSend.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(this.teamMessagesToSend.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private void messageToAll(ByteBuffer buf) {
        String message = NetworkUtils.getStringFromBuf(buf);
        message = "[all] " + this.getName() + ": " + message;
        ByteBuffer bf = ByteBuffer.allocate(NetworkObject.BYTE_BUFFER_DEFAULT_SIZE);
        NetworkUtils.putStringIntoBuf(bf, NetworkObjectManager.MANAGER_UUID.toString());
        NetworkUtils.putStringIntoBuf(bf, "displayMessage");
        NetworkUtils.putStringIntoBuf(bf, message);
        this.allMessagesToSend.offer(bf);
    }

    public Optional<ByteBuffer> getAllMessages() {
        if (this.allMessagesToSend.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(this.allMessagesToSend.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Graphic getGraphic(Vector2D playerPos) {
        if (currentHealth <= 0) {
            return DirectionalSprite.GRAVESTONE_DIRECTIONAL_SPRITE.getCurrentGraphic(position, direction, playerPos);
        }
        DirectionalSprite frame = currentAnim.getFrame();
        return frame.getCurrentGraphic(position, direction, playerPos);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.sendStatsToScoreBoard();

        hasShot = false;
        if (shootOnNextUpdate) {
            currentWeapon.fire(this);
            inertiaY += 60;
            hasShot = true;
            shootOnNextUpdate = false;
        }

        synchronized (inputState) {
            inputState = inputModule.updateInput(inputState, this);
            // Check whether each input is triggered - if it is, execute the
            // respective method.
            for (Entry<ClientController, Byte> entry : inputState.entrySet()) {
                // For boolean inputs (i.e. all current inputs), 0 represents
                // false.
                if (entry.getValue() != 0) {
                    ACTION_MAPPINGS.get(entry.getKey()).run();
                }
            }
        }
        if (prevDir != null && prevPos != null) {
            // The difference between our previous and new positions.
            Vector2D displacement = position.subtract(prevPos);
            updateInertia(displacement);

            double dis = displacement.magnitude();
            moveAnim.update(dis);
        }

        currentAnim = moveAnim.expired() ? standAnim : moveAnim;

        currentWeapon.update();
        prevPos = position;
        prevDir = direction;
        checkDeath();
    }

    private void updateInertia(Vector2D displacement) {
        if (!GameSettings.MOTION_BOB) {
            inertiaX = 0;
            inertiaY = 0;
            return;
        }
        final double p = 0.07D;
        inertiaX += (int) (p * -inertiaX);
        inertiaY += (int) (p * -inertiaY);
        Vector2D temp = new Vector2D(plane.getX() / plane.magnitude(), plane.getY() / plane.magnitude());
        double orthProjection = displacement.dot(temp);
        inertiaX -= orthProjection * currentWeapon.getInertiaCoeffX();

        temp = new Vector2D(direction.getX() / direction.magnitude(), direction.getY() / direction.magnitude());
        orthProjection = displacement.dot(temp);
        inertiaY += orthProjection * currentWeapon.getInertiaCoeffY();

        double prevDirTheta = Math.atan2(prevDir.getY(), prevDir.getX());
        double directionTheta = Math.atan2(direction.getY(), direction.getX());
        double diff = directionTheta - prevDirTheta;
        if (diff > Math.PI) {
            diff -= 2 * Math.PI;
        } else if (diff < -Math.PI) {
            diff += 2 * Math.PI;
        }

        inertiaX += currentWeapon.getInertiaCoeffX() * diff;
    }

    private void shoot() {
        if (currentWeapon == null) {
            return;
        }

        if (currentWeapon.canFire()) {
            shootOnNextUpdate = true;
        }
    }

    @Override
    public void onCollide(Player player) {
    }

    // TODO: serialize weapon etc.
    @Override
    public ByteBuffer serialize() {
        ByteBuffer bb = super.serialize();
        bb.putInt(shootOnNextUpdate ? 1 : 0);
        bb.putDouble(this.timeToSend);
        bb.putInt(this.respawn ? 1 : 0);
        this.respawn = false;
        bb.putInt(currentHealth);
        bb.putInt(score);
        bb.putInt(this.currentWeaponType.ordinal());
        return bb;
    }

    @Override
    public synchronized void deserialize(ByteBuffer buf) {
        super.deserialize(buf);
        shootOnNextUpdate = buf.getInt() == 1;
        this.timeToSend = buf.getDouble();
        this.respawn = buf.getInt() == 1;
        currentHealth = buf.getInt();
        previousHealth = currentHealth;
        setScore(buf.getInt());

        WeaponType newWeaponType = WeaponType.VALUES[buf.getInt()];
        this.setCurrentWeaponType(newWeaponType);
    }

    @Override
    public int getMinimapColor() {
        return 0xFFFFFF;
    }

    @Override
    public DirectionalSprite getDirectionalSprite() {
        return DirectionalSprite.PLAYER_DIRECTIONAL_SPRITE;
    }

    @Override
    public String getClientClassName() {
        // One class for both client and server.
        return this.getClass().getName();
    }

    @Override
    public void takeDamage(int damage, Entity inflictor) {
        // Only play if the player is us.
        if (GameSettings.isClient()) {
            return;
        } else {
            damage = (int) (damage * damageTakenModifier);
            int newHealth = currentHealth - damage;
            currentHealth = Math.max(0, Math.min(MAX_HEALTH, newHealth));
            if (inflictor != null) {
                lastInflictor = inflictor;
            }
        }
    }

    private void checkDeath() {
        if (GameSettings.isClient()) {
            return;
        }

        GameManager gameManager = GameEngine.getSingleton().getWorld().getGameManager();
        if (currentHealth <= 0) {
            if (lastInflictor == null) {
                System.out.println(this.getName() + " was killed by natural causes");
                gameManager.onEntityDeath(this);
            } else {
                System.out.println(this.getName() + " was killed by " + lastInflictor.getName());
                if (lastInflictor instanceof Player) {
                    gameManager.onEntityDeath(this, (Player) lastInflictor);
                } else {
                    gameManager.onEntityDeath(this);
                }
            }
            respawn = true;
            removeAllBuffs();
            this.resetBuff(new Immune(this));
            this.sendSystemMessage(this.getName(), lastInflictor);
        }
    }

    public void applyHeal(int heal) {
        takeDamage(-heal, null);
    }

    public int getScore() {
        return score;
    }

    @Override
    public void respawn(Vector2D spawnPos) {
        this.position = spawnPos;
        currentHealth = MAX_HEALTH;
        inputModule.onRespawn(this);
        System.out.println(this.getName() + " respawned.");
    }

    public void setInputModule(InputModule inp) {
        this.inputModule = inp;
    }

    public void setOnNextShot(boolean b) {
        this.shootOnNextUpdate = b;
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    /**
     * Make the player hold a weapon of the given type. Instantiates a new
     * weapon of this type.
     * 
     * @param wt
     *            The type of weapon for the player to be holding.
     */
    public synchronized void setCurrentWeaponType(WeaponType wt) {
        if (wt == this.currentWeaponType) {
            return;
        }

        inertiaY += 500;
        this.currentWeaponType = wt;
        this.currentWeapon = wt.getNewWeapon();
        System.out.println("Player " + this.getName() + " is now holding a " + wt + ".");

        PlayerGraphicMatrix.Weapon w;
        switch (currentWeaponType) {
        case SHOTGUN:
            w = PlayerGraphicMatrix.Weapon.SHOTGUN;
            break;
        case PISTOL:
            w = PlayerGraphicMatrix.Weapon.PISTOL;
            break;
        default:
            w = PlayerGraphicMatrix.Weapon.PISTOL;
        }
        
        System.out.println(w);

        this.moveAnim.setFrames(PlayerGraphicMatrix.getGraphic(Color.BLUE, w, Stance.MOVE));
        this.standAnim.setFrames(PlayerGraphicMatrix.getGraphic(Color.BLUE, w, Stance.STAND));
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setScore(int score) {
        if (score < 0) {
            this.score = 0;
        } else {
            this.score = score;
        }
    }

    public void addScore(int amount) {
        setScore(score + amount);
    }

    public void setRespawn(boolean b) {
        this.respawn = b;
    }

    public void setHealth(int h) {
        this.previousHealth = this.currentHealth;
        this.currentHealth = h;

        // Only play if the player is us.
        if (this == GameEngine.getSingleton().getCamera().getSubject()) {
            playGrunts();
        }
    }

    private void playGrunts() {
        SoundManager sm = GameEngine.getSingleton().getSoundManager();
        if (this.currentHealth < this.previousHealth) {
            sm.playIfNotAlreadyPlaying(Grunt.getRandomGrunt(), sm.defaultVolume);
        }
    }

    @Override
    public boolean renderName() {
        return true;
    }

    public void onClientRespawn() {
        this.resetBuff(new SpawnVision(this));
        this.resetBuff(new Immune(this)); // just for health bar cosmetic
    }

}
