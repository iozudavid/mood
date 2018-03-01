package com.knightlore.network.protocol;

import java.awt.event.KeyEvent;
import java.io.IOException;

public enum ClientController {
    FORWARD, LEFT, BACKWARD, RIGHT, ROTATE_CLOCKWISE, ROTATE_ANTI_CLOCKWISE, SHOOT;

    public static int getKeyCode(ClientController k) throws IOException {
        switch (k) {
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
