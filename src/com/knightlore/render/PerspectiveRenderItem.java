package com.knightlore.render;

import com.knightlore.render.graphic.Graphic;

/**
 * This class is a data holder. It stores information related to rendering items
 * with the right perspective.
 * 
 * HOW THIS IS USED: when a ray intersects a block, a PerspectiveRenderItem is
 * created to store information about the collision -- e.g. how transparent the
 * block was, where to start and finish drawing, the associated texture, and so
 * on. These render items are pushed to a stack. Once the ray reaches a
 * completely opaque object, the stack is popped and each PerspectiveRenderItem
 * is rendered in turn. This allows for transparency and composition.
 * 
 * @author Joe Ellis
 *
 */
public class PerspectiveRenderItem {

    public final double opacity;
    public final int drawStart;
    public final int drawEnd;
    public final int lineHeight;

    public final Graphic texture;

    public final int texX;
    public final double distanceToWall;
    public final int xx;
    public final boolean side;

    public PerspectiveRenderItem(double opacity, int drawStart, int drawEnd, int lineHeight, Graphic texture, int texX,
            double distanceToWall, int xx, boolean side) {
        this.opacity = opacity;
        this.drawStart = drawStart;
        this.drawEnd = drawEnd;
        this.lineHeight = lineHeight;
        this.texture = texture;
        this.texX = texX;
        this.distanceToWall = distanceToWall;
        this.xx = xx;
        this.side = side;
    }

}
