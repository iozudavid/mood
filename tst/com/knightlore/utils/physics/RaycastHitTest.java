package com.knightlore.utils.physics;

import com.knightlore.game.entity.Entity;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class RaycastHitTest {
    
    @Test
    public void raycastHit_nonnull() {
        // Given
        Entity entity = mock(Entity.class);
        RaycastHit raycastHit = new RaycastHit(entity);
        
        // Then
        assertThat(raycastHit.didHitEntity(), is(true));
        assertThat(raycastHit.getEntity(), sameInstance(entity));
    }
    
    @Test
    public void raycastHit_null() {
        // Given
        RaycastHit raycastHit = new RaycastHit(null);
        
        // Then
        assertThat(raycastHit.didHitEntity(), is(false));
        assertNull(raycastHit.getEntity());
    }
}