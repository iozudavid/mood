package com.knightlore.game.entity.pickup;

public class PickupPlacement implements Comparable<PickupPlacement>{

    private double placementTime;
    private PickupItem item;
    
    @SuppressWarnings("unused")
    private PickupPlacement() {
    }
    
    public PickupPlacement(double placementTime, PickupItem item) {
        this.placementTime = placementTime;
        this.item = item;
    }

    public double getPlacementTime() {
        return placementTime;
    }
    
    public PickupItem getItem(){
        return item;
    }
    
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
