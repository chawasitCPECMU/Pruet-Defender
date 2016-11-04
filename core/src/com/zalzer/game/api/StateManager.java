package com.zalzer.game.api;

import java.util.Stack;

/**
 * Created by zalzer on 11/1/2016 AD.
 */
public class StateManager {

    private Stack<State> states;

    public StateManager() {
        states = new Stack<State>();
    }

    /**
     * Pop State from stack
     */
    public void pop() {
        // pop state and dispose it
        states.pop().dispose();
    }

    /**
     * Push State
     * @param state
     */
    public void push(State state) {
        states.push(state);
    }

    /**
     * Peek Top State in stack
     */
    public State peek() {
        return states.peek();
    }

    /**
     * Pop and Dispose all states
     */
    public void dispose() {
        while(!states.empty()) pop();
    }

}
