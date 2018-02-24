package com.knightlore.network.protocol;

import java.awt.event.KeyEvent;
import java.io.IOException;

public enum ClientControl {
    FORWARD, LEFT, BACKWARD, RIGHT, ROTATE_CLOCKWISE, ROTATE_ANTI_CLOCKWISE, SHOOT;

    public static int getKeyCode(ClientControl k) throws IOException {
        switch (k) {
        // at this moment only this keys are provided
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
        default:
            throw new IOException();
        }

    }

}
