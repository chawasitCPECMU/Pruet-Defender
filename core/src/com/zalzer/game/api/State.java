package com.zalzer.game.api;

import java.util.Stack;

/**
 * Created by zalzer on 11/1/2016 AD.
 */
public abstract class State {

    private StateManager stateManager;

    public State(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public abstract void update(float deltaTime);
    public abstract void render();
    public void dispose() {

    }
}
