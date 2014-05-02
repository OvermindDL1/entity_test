package com.overminddl1.entity_test;

import com.artemis.Component;

/**
 * Created by overminddl1 on 5/1/14.
 */
// All Components must inherit from Component, though Component has/does nothing, just a tag
public class Position extends Component {

    private float x;
    private float y;

    public Position() {
    }

    public Position(float x, float y) {
        this.setX(x);
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void deltaX(float x) {
        this.x += x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void deltaY(float y) {
        this.y += y;
    }
}
