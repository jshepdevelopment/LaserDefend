package com.jshepdevelopment.laserdefend.model;

/**
 * Created by Jason Shepherd on 12/19/2016.
 */

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {

    public enum EnemyState {
        GOOD,
        BAD,
        COVERED,
        GOOD_UNCOVERED,
    }
    private static final float SIZE = 1.0f;
    private boolean isFiring = false;
    private float speed = 1;
    private float ySpeed = 1;

    private EnemyState state;
    private int textureIndex;

    private Rectangle bounds = new Rectangle();

    public Enemy(Vector2 position, EnemyState state) {

        textureIndex = -1;
        bounds.x = position.x;
        bounds.y = position.y;
        bounds.width = SIZE * Gdx.graphics.getPpcX();
        bounds.height = SIZE * Gdx.graphics.getPpcY();

        this.state = state;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public EnemyState getState() {
        return state;
    }

    public void setState(EnemyState state) {
        this.state = state;
    }

    public int getIndex() {
        return textureIndex;
    }

    public void setIndex(int index) {
        textureIndex = index;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float eSpeed) {
        this.speed = eSpeed;
    }

    public float getYSpeed() { return ySpeed; }

    public void setYSpeed(float eYSpeed) {
        this.ySpeed = eYSpeed;
    }

    public boolean getIsFiring() { return isFiring; }

    public void setIsFiring(boolean isFiring) {
        this.isFiring = isFiring;
    }
}