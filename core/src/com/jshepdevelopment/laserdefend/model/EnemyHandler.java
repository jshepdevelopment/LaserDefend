package com.jshepdevelopment.laserdefend.model;

/**
 * Created by Jason Shepherd on 12/19/2016.
 */

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.jshepdevelopment.laserdefend.model.Enemy.EnemyState;

public class EnemyHandler {

    private static EnemyHandler handler = new EnemyHandler();
    private ArrayList<Enemy> enemyList = new ArrayList<Enemy>();

    private EnemyHandler() {

    }

    public static EnemyHandler getInstance() {
        return handler;
    }

    public ArrayList<Enemy> getList() {
        return enemyList;
    }

    public void setList(ArrayList<Enemy> foodList) {
        this.enemyList = foodList;
    }

    public void addFood(Vector2 position, EnemyState state) {
        enemyList.add(new Enemy(position, state));
    }

    public void animate(float delta) {
        for (int i=0;i<enemyList.size();i++) {
            Enemy food = enemyList.get(i);
            if (food.getBounds().width < 1.2f*Gdx.graphics.getPpcX()){

                food.setBounds(new Rectangle(
                                food.getBounds().x,
                                food.getBounds().y,
                                food.getBounds().width + delta,
                                food.getBounds().height + delta
                        )
                );
            }
            else {
                enemyList.remove(i);
                i--;
            }
        }
    }

    public void clearList() {
        enemyList.clear();
    }
}
