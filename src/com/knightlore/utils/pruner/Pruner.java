package com.knightlore.utils.pruner;

import java.util.Iterator;
import java.util.List;

public class Pruner {

    public static void prune(List<? extends Prunable> prunables) {
        for (Iterator<? extends Prunable> i = prunables.iterator(); i.hasNext();) {
            Prunable p = i.next();
            if (!p.exists()) {
                p.destroy();
                i.remove();
            }
        }
    }
}
