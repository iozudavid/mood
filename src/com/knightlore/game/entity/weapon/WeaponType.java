package com.knightlore.game.entity.weapon;

/**
 * An enum to store the different types of weapon a Player can hold.
 *
 * @author Will
 */
public enum WeaponType {
    PISTOL(Pistol.class), SHOTGUN(Shotgun.class);
    // Cache the values to avoid recreating the array each time.
    public static final WeaponType VALUES[] = values();
    
    private final Class<? extends Weapon> cls;
    
    WeaponType(Class<? extends Weapon> cls) {
        this.cls = cls;
    }
    
    /**
     * Returns a new instance of the weapon of the given type.
     *
     * @return a new weapon object.
     */
    public Weapon getNewWeapon() {
        try {
            return this.cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            System.err.println("Error while instantiating a weapon of type "
                    + this.name() + ":");
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public String toString() {
        return this.cls.getSimpleName();
    }
}
