package com.knightlore.game.entity.pickup;

public class PickupPlacement implements Comparable<PickupPlacement>{

    private double placementTime;
    private PickupItem item;
    
    @SuppressWarnings("unused")
    private PickupPlacement() {
    }
    
    /**
     * Used by the pickup manager. The given times determines when
     * the item is placed by the manager, and the item object provided
     * determines what is placed.
     * @param placementTime
     * @param item
     */
    public PickupPlacement(double placementTime, PickupItem item) {
        this.placementTime = placementTime;
        this.item = item;
    }

    /**
     * The time at which the item is placed.
     * @return placementTime
     */
    public double getPlacementTime() {
        return placementTime;
    }
    
    /**
     * The item to be placed.
     * @return item
     */
    public PickupItem getItem(){
        return item;
    }
    
    /**
     * Used to compare other PickupPlacements by time.
     * This allows the PickupManager's queue to be sorted
     * appropriately.
     */
    @Override
    public int compareTo(PickupPlacement arg) {
        if(placementTime < arg.placementTime) {
            return -1;
        }
        if(placementTime > arg.placementTime) {
            return 1;
        }
        return 0;
    }
    
}
