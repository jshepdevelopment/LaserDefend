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

    public void setList(ArrayList<Enemy> enemyList) {
        this.enemyList = enemyList;
    }

    public void addEnemy(Vector2 position, EnemyState state) {
        enemyList.add(new Enemy(position, state));
    }

    public void animate(float delta) {

        for (int i=0;i<enemyList.size();i++) {
            Enemy enemy = enemyList.get(i);
            if (enemy.getState() == EnemyState.GOOD || enemy.getState() == EnemyState.COVERED){
                enemy.setBounds(new Rectangle(
                                enemy.getBounds().x,
                                enemy.getBounds().y - delta * enemy.getSpeed(),
                                enemy.getBounds().width,
                                enemy.getBounds().height
                        )
                );
            }
            else if (enemy.getState() == EnemyState.BAD){
                enemy.setBounds(new Rectangle(
                                enemy.getBounds().x + delta * enemy.getSpeed(),
                                enemy.getBounds().y,
                                enemy.getBounds().width,
                                enemy.getBounds().height
                        )
                );
            }
            else {
               enemyList.remove(i);
                i--;
            }
        }

/*        for (int i=0;i<enemyList.size();i++) {
            Enemy enemy = enemyList.get(i);
            if (enemy.getBounds().width < 1.2f*Gdx.graphics.getPpcX()){

                enemy.setBounds(new Rectangle(
                                enemy.getBounds().x,
                                enemy.getBounds().y,
                                enemy.getBounds().width + delta,
                                enemy.getBounds().height + delta
                        )
                );
            }
            else {
                enemyList.remove(i);
                i--;
            }
        }
*/
    }
    public void clearList() {
        enemyList.clear();
    }
}
