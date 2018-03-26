package com.knightlore.network.protocol;

import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * Controllers performed by user
 * @author David Iozu, Will Miller
 */
public enum ClientController {
    FORWARD, LEFT, BACKWARD, RIGHT, ROTATE_CLOCKWISE, ROTATE_ANTI_CLOCKWISE, SHOOT;

    /**
     * Transition from Controller to actual keyboard inputs
     * 
     * @param cControl
     *            - control to be converted
     * @return actual keyboard key pressed
     * @throws IOException
     *             when control not recognized
     */
    public static int getKeyCode(ClientController cControl) throws IOException {
        switch (cControl) {
        // at this moment only these keys are provided
        case FORWARD:
            return KeyEvent.VK_W;
        case LEFT:
            return KeyEvent.VK_A;
        case BACKWARD:
            return KeyEvent.VK_S;
        case RIGHT:
            return KeyEvent.VK_D;
        case ROTATE_ANTI_CLOCKWISE:
            return KeyEvent.VK_LEFT;
        case ROTATE_CLOCKWISE:
            return KeyEvent.VK_RIGHT;
        case SHOOT:
            return KeyEvent.VK_SPACE;
        default:
            throw new IOException();
        }

    }

}
