package com.overminddl1.entity_test;

import com.artemis.Component;

/**
 * Created by overminddl1 on 5/1/14.
 */

public class Physics extends Component {
    private float velocityX;
    private float velocityY;

    private float forceX;
    private float forceY;

    // Could add in more here like a body shape so they cannot intersect, ideally this entire Component would actually
    // be part of a physics world, like JBullet or something.

    public Physics() {
    }

    public Physics(float velocityX, float velocityY, float forceX, float forceY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.forceX = forceX;
        this.forceY = forceY;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public void deltaVelocityX(float velocityX) {
        this.velocityX += velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public void deltaVelocityY(float velocityY) {
        this.velocityY += velocityY;
    }

    public float getForceX() {
        return forceX;
    }

    public void setForceX(float forceX) {
        this.forceX = forceX;
    }

    public float getForceY() {
        return forceY;
    }

    public void setForceY(float forceY) {
        this.forceY = forceY;
    }

    public void moveForcesToVelocity() {
        velocityX += forceX;
        velocityY += forceY;
        forceX = 0.0f;
        forceY = 0.0f;
    }

    public void dampenVelocity(float both) {
        dampenVelocity(both, both);
    }
    public void dampenVelocity(float x, float y) {
        velocityX *= x;
        velocityY *= y;
    }
}
