package com.knightlore.utils.pruner;

import java.util.List;

public class Pruner {
    
    public static void prune(List<Prunable> prunables) {
        prunables.removeIf(p -> !p.exists());
    }

}
