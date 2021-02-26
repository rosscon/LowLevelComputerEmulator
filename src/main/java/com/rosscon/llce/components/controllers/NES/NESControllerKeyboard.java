package com.rosscon.llce.components.controllers.NES;

import com.rosscon.llce.components.busses.IntegerBus;
import com.rosscon.llce.components.flags.Flag;

import java.util.HashMap;
import java.util.Map;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class NESControllerKeyboard extends NESController {

    private int currentState;

    private final EventHandler buttonPressedHandler;

    private final EventHandler buttonReleasedHandler;

    private final Map<Integer, Integer> keyMap = new HashMap<Integer, Integer>() {{
        put(KeyCode.UP.getCode(), BUTTON_INDEX_UP);
        put(KeyCode.DOWN.getCode(), BUTTON_INDEX_DOWN);
        put(KeyCode.LEFT.getCode(), BUTTON_INDEX_LEFT);
        put(KeyCode.RIGHT.getCode(), BUTTON_INDEX_RIGHT);
        put(KeyCode.Z.getCode(), BUTTON_INDEX_A);
        put(KeyCode.X.getCode(), BUTTON_INDEX_B);
        put(KeyCode.C.getCode(), BUTTON_INDEX_START);
        put(KeyCode.V.getCode(), BUTTON_INDEX_SELECT);
    }};

    public EventHandler getKeyPressHandler(){
        return buttonPressedHandler;
    }

    public EventHandler getKeyReleaseHandler(){
        return buttonReleasedHandler;
    }

    public NESControllerKeyboard(IntegerBus addressBus, IntegerBus dataBus, Flag rwFlag, int player) {
        super(addressBus, dataBus, rwFlag, player);
        currentState = 0x00;

        buttonPressedHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                keyPressed(event.getCode());
            }
        };

        buttonReleasedHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                keyReleased(event.getCode());
            }
        };
    }

    @Override
    protected void snapshotInput() {
        this.switchState = currentState;
    }

    public void keyPressed(KeyCode kc) {
        if (!keyMap.containsKey(kc.getCode()))
            return;

        int mask = 0x01 << keyMap.get(kc.getCode());
        currentState |= mask;
    }

    public void keyReleased(KeyCode kc) {
        if (!keyMap.containsKey(kc.getCode()))
            return;

        int mask = 0x01 << keyMap.get(kc.getCode());
        mask = ~mask;
        currentState &= mask;
    }
}
