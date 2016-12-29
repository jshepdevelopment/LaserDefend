package com.jshepdevelopment.laserdefend.view;

/**
 * Created by Jason Shepherd on 12/19/2016.
 */

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.jshepdevelopment.laserdefend.model.Enemy;
import com.jshepdevelopment.laserdefend.model.EnemyHandler;
import com.jshepdevelopment.laserdefend.model.Laser;
import com.jshepdevelopment.laserdefend.screens.EndScreen;

import java.util.ArrayList;
import java.util.Random;

public class GameRenderer {

    private Game game;

    private Laser playerLaser;
    private int enemyLaserCounter = 0;
    private Laser enemyLaser;
    private ArrayList<Laser> enemyLasers = new ArrayList<Laser>();

    Random generator = new Random();
    double rval = generator.nextDouble();
    double gval = generator.nextDouble();
    double bval = generator.nextDouble();
    double aval = generator.nextDouble();

    private Sprite spriteLaserS1;
    private Sprite spriteLaserS2;
    private Sprite spriteLaserM1;
    private Sprite spriteLaserM2;
    private Sprite spriteLaserE1;
    private Sprite spriteLaserE2;

    private Sprite playerSprite;
    private Sprite playerArmSprite;

    private TextureRegion backgroundTexture;
    private TextureRegion[] goodTexture = new TextureRegion[5];
    private TextureRegion[] badTexture = new TextureRegion[5];
    private TextureRegion coveredTexture;
    private TextureRegion penguinTexture;
    private TextureRegion santaTexture;
    private TextureRegion playerArmTexture;


    public SpriteBatch batch;
    private Vector2 touchedArea = new Vector2();
    private Vector2 playerLaserDest = new Vector2();

    private BitmapFont gameFont;
    private Sound eatSound;
    private Sound ewSound;
    private ParticleEffect effect;
    private ArrayList<ParticleEffect> effects = new ArrayList<ParticleEffect>();

    //private ParticleEffect enemyLaserEffect;
    //private ArrayList<ParticleEffect> enemyLaserEffects = new ArrayList<ParticleEffect>();

    private ShapeRenderer shape;
    private EnemyHandler enemyHandler;
    private static GameRenderer instance = null;

    public GameRenderer(Game game) {
        instance = this;
        this.game = game;
        loadItems();
    }

    // Loading all the necessary items
    private void loadItems() {

        Texture texLaserS1;
        Texture texLaserS2;
        Texture texLaserM1;
        Texture texLaserM2;
        Texture texLaserE1;
        Texture texLaserE2;


        backgroundTexture = new TextureRegion(new Texture(Gdx.files.internal("background/background.png")));
        santaTexture = new TextureRegion(new Texture(Gdx.files.internal("models/santa.png")));
        playerArmTexture = new TextureRegion(new Texture(Gdx.files.internal("effects/santa_arm.png")));

        texLaserS1 = new Texture(Gdx.files.internal("effects/beamstart1.png"));
        texLaserS2 = new Texture(Gdx.files.internal("effects/beamstart2.png"));
        texLaserM1 = new Texture(Gdx.files.internal("effects/beammid1.png"));
        texLaserM2 = new Texture(Gdx.files.internal("effects/beammid2.png"));
        texLaserE1 = new Texture(Gdx.files.internal("effects/beamend1.png"));
        texLaserE2 = new Texture(Gdx.files.internal("effects/beamend2.png"));

        playerSprite = new Sprite(santaTexture);
        playerSprite.setPosition(Gdx.graphics.getWidth()/2, 0);
        playerArmSprite = new Sprite(playerArmTexture);
        playerArmSprite.setPosition(Gdx.graphics.getWidth()/2, 0);

        santaPos = playerSprite.getX();

        spriteLaserS1 = new Sprite(texLaserS1);
        spriteLaserS2 = new Sprite(texLaserS2);
        spriteLaserM1 = new Sprite(texLaserM1);
        spriteLaserM2 = new Sprite(texLaserM2);
        spriteLaserE1 = new Sprite(texLaserE1);
        spriteLaserE2 = new Sprite(texLaserE2);


        playerLaser = new Laser();
        playerLaser.begin1 = spriteLaserS1;
        playerLaser.begin2 = spriteLaserS2;
        playerLaser.mid1 = spriteLaserM1;
        playerLaser.mid2 = spriteLaserM2;
        playerLaser.end1 = spriteLaserE1;
        playerLaser.end2 = spriteLaserE2;
        playerLaser.setColor(Color.RED);
        playerLaser.position.set(Gdx.graphics.getWidth()/2, 0);

        batch = new SpriteBatch();

        // Loading the atlas which contains the spritesheet
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("models/laserdefend.pack"));
        // getting the sprites with for good food
        for (int i = 0; i < 5; i++) {
            goodTexture[i] = atlas.findRegion("good"+i);
        }
        // getting the sprites for bad food
        for (int i = 0; i < 5; i++) {
            badTexture[i] = atlas.findRegion("bad"+i);
        }
        // getting the sprite for covered food
        coveredTexture = atlas.findRegion("cover");
        penguinTexture = atlas.findRegion("penguin");

        enemyHandler = EnemyHandler.getInstance();

        gameFont = new BitmapFont(Gdx.files.internal("fonts/title.fnt"));

        eatSound = Gdx.audio.newSound(Gdx.files.internal("sounds/eat.wav"));
        ewSound = Gdx.audio.newSound(Gdx.files.internal("sounds/eww.wav"));

        shape = new ShapeRenderer();

        // Loading the particle effect used when snatching the food
        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("effects/mist.p"), Gdx.files.internal("effects"));
        effect.setPosition(0, 0);
        effect.start();

        playerArmSprite.setPosition(playerSprite.getX(), playerSprite.getY()
                + playerSprite.getHeight() / 2);

        // Loading the enemy laser particle effect
        //enemyLaserEffect = new ParticleEffect();
        //enemyLaserEffect.load(Gdx.files.internal("effects/enemylaser.p"), Gdx.files.internal("effects"));
        //enemyLaserEffect.setPosition(0, 0);
        //enemyLaserEffect.start();

    }

	/*
	 * Render fields
	 */

    //private float timePassed = 0.0f;

    private float timeGood = 0.0f;
    private float timeBad = 0.0f;
    private float timeCoveredAppearance = 0.0f;
    private int score = 0;
    private int ending = 3;
    private boolean flash = false;
    private boolean laserOn = false;
    private boolean laserFired = false;
    private float flashDuration = 0.0f;
    private float laserDuration = 0.0f;
    private float enemyLaserDuration = 0.0f;
    private float santaPos = 0.0f;
    private boolean santaFlipped = false;

    // Render the game
    public void render(float delta)
    {
        // Starting the timers used to place enemies on the screen at certain intervals of time
        //timePassed += delta;
        timeGood += delta;
        timeBad+=delta;
        timeCoveredAppearance+=delta;

        // Placing the enemy on-screen
        if (timeGood > .8f) {
            enemyHandler.addEnemy(
                    new Vector2(
                            (int)(Math.random()*Gdx.graphics.getWidth()),
                            (int)(Math.random()*Gdx.graphics.getHeight()) + Gdx.graphics.getHeight()/2),
                    Enemy.EnemyState.GOOD
            );
            timeGood = 0.0f;
        }

        if (timeBad > 2.0f) {
            enemyHandler.addEnemy(
                    new Vector2(
                            (int)(Math.random()*Gdx.graphics.getWidth()),
                            (int)(Math.random()*Gdx.graphics.getHeight())+ Gdx.graphics.getHeight()/2),
                    Enemy.EnemyState.BAD

            );

            timeBad = 0.0f;
        }

        if (timeCoveredAppearance > 3.5f) {

            enemyHandler.addEnemy(
                    new Vector2(
                            (int)(Math.random()*Gdx.graphics.getWidth()),
                            (int)(Math.random()*Gdx.graphics.getHeight())+ Gdx.graphics.getHeight()/2),
                    Enemy.EnemyState.COVERED);
            timeCoveredAppearance = 0.0f;

            //enemyLaserEffect.setPosition(tempx,tempy);
            //double degreesA = Math.atan2(
            //        0 - tempy,
            //        Gdx.graphics.getWidth()/2 - tempx
            //) * 180.0d / Math.PI;

            //rotateBy((float) degreesA);
            //enemyLaserEffect.start();
            //enemyLaserEffects.add(enemyLaserEffect);
        }

        // Animating the enemy
        enemyHandler.animate(delta*100);

        // Checking if the covered enemy should show the contents
        for (Enemy enemy : enemyHandler.getList()){
             if (enemy.getState() == Enemy.EnemyState.COVERED) {
                 //if (enemy.getBounds().getY() < Gdx.graphics.getHeight()/2 * Gdx.graphics.getPpcX()){
                 if (enemy.getBounds().getY() < Gdx.graphics.getHeight() / 2) {
                     int rand = (int) (Math.random() * 2);
                     if (rand % 2 == 0) {
                         enemy.setState(Enemy.EnemyState.BAD);
                         enemy.setSpeed(2);

                     } else {
                         enemy.setState(Enemy.EnemyState.BAD);
                         enemy.setSpeed(3);
                     }
                 }
             }
            if (enemy.getState() == Enemy.EnemyState.BAD) {
                int rand = (int) (Math.random() * 2);
                if (rand % 2 == 0) {
                    enemy.setSpeed(2);
                } else {
                    enemy.setSpeed(3);
                }

            }
        }

        // Load lasers and checking the collision between the touched area and the enemy
        ArrayList<Enemy> tempList = enemyHandler.getList();
        for (int i = 0; i < tempList.size(); i++) {
            Enemy tempEnemy = tempList.get(i);

            if(tempEnemy.getState() == Enemy.EnemyState.BAD &&
                    tempEnemy.getBounds().getY() < Gdx.graphics.getHeight()/2 &&
                    tempEnemy.getBounds().getY() > 0 && !tempEnemy.getIsFiring()){

                enemyLaser = new Laser();
                enemyLaser.begin1 = spriteLaserS1;
                enemyLaser.begin2 = spriteLaserS2;
                enemyLaser.mid1 = spriteLaserM1;
                enemyLaser.mid2 = spriteLaserM2;
                enemyLaser.end1 = spriteLaserE1;
                enemyLaser.end2 = spriteLaserE2;

                rval = generator.nextDouble();
                gval = generator.nextDouble();
                bval = generator.nextDouble();
                aval = generator.nextDouble();

                //what a hack
                //if(tempEnemy.getBounds().getY() == Gdx.graphics.getHeight()/2 + 99)
                this.enemyLaser.setColor(new Color((float)rval, (float)gval, (float)bval, (float)aval));

                enemyLaser.position.set(tempEnemy.getBounds().getX()
                        + tempEnemy.getBounds().getWidth(), tempEnemy.getBounds().getY());

                double enemyLaserDistance = Math.sqrt((tempEnemy.getBounds().getX()-playerSprite.getX())*
                        (tempEnemy.getBounds().getX()-playerSprite.getX()) +
                        (tempEnemy.getBounds().getY()-0)*
                                (tempEnemy.getBounds().getY()-0));
                enemyLaser.distance = (float)enemyLaserDistance;

                double enemyLaserDegrees = Math.atan2(
                        0 - tempEnemy.getBounds().getY(),
                        playerSprite.getX() - tempEnemy.getBounds().getX()
                ) * 180.0d / Math.PI;

                enemyLaser.degrees = ((float)enemyLaserDegrees)-90;

                enemyLasers.add(this.enemyLaser);
                enemyLaserCounter++;

                tempEnemy.setIsFiring(true);

            }

            // Input from phone pitch to move santa along x axis
            santaPos = playerSprite.getX() - Gdx.input.getPitch() / 32;
            playerSprite.setPosition(santaPos, playerSprite.getY());
            playerArmSprite.setPosition(playerSprite.getX() + playerArmSprite.getWidth(),
                    playerSprite.getHeight() / 2);
            playerLaser.position.set(playerSprite.getX(), playerSprite.getY()
                    + playerSprite.getHeight() / 2);


            //if (laserFired) {
            //   playerSprite.rotate(-playerLaser.degrees + 90);
            //    laserFired = false;
            //}

            if (playerSprite.getX() > Gdx.graphics.getWidth()) {
                playerSprite.setPosition(Gdx.graphics.getWidth(), playerSprite.getY());
            }

            if (playerSprite.getX() < 0) {
                playerSprite.setPosition(0, playerSprite.getY());
            }

            if (touchedArea.x >= tempEnemy.getBounds().x &&
                    touchedArea.x <= tempEnemy.getBounds().x+tempEnemy.getBounds().width &&
                    touchedArea.y >= tempEnemy.getBounds().y &&
                    touchedArea.y <= tempEnemy.getBounds().y+tempEnemy.getBounds().height) {

                if (tempEnemy.getBounds().x > playerSprite.getX() && !santaFlipped ) {
                    playerSprite.flip(true, false);
                    playerArmSprite.flip(true, false);
                    playerArmSprite.setPosition(playerSprite.getX() + playerArmSprite.getWidth()*3, 24 +
                            playerSprite.getHeight() / 2);
                    santaFlipped = true;

                } else if (tempEnemy.getBounds().x < playerSprite.getX() && santaFlipped){
                    playerSprite.flip(true, false);
                    playerArmSprite.flip(true, false);
                    playerArmSprite.setPosition(playerSprite.getX() + playerArmSprite.getWidth(),
                            playerSprite.getHeight() / 2);

                    santaFlipped = false;
                }


                // set the laser destination
                playerLaserDest.x = touchedArea.x;
                playerLaserDest.y = touchedArea.y;

                //playerLaser.distance = (150*100%300);
                double laserDistance = Math.sqrt((playerSprite.getX()-playerLaserDest.x)*
                        (playerSprite.getX()-playerLaserDest.x) +
                        (0-playerLaserDest.y)*
                                (0-playerLaserDest.y));
                playerLaser.distance = (float)laserDistance;

                double laserDegrees = Math.atan2(
                        playerLaserDest.y - 0,
                        playerLaserDest.x - playerSprite.getX()
                ) * 180.0d / Math.PI;

                playerLaser.degrees = ((float)laserDegrees)-90; //(float)laserDegrees;
                playerArmSprite.setRotation(((float)laserDegrees)-90); //(float)laserDegrees;


                laserFired = true;

                switch (tempEnemy.getState()) {
                    case GOOD:
                        effect.setPosition(tempEnemy.getBounds().x, tempEnemy.getBounds().y);
                        effect.start();
                        effects.add(effect);
                        laserOn = true;
                        // tagging the food to be removed
                        score++;
                        // playing the sound
                        eatSound.play();
                        // Remove the food from the list
                        tempList.remove(i);
                        i--;
                        break;

                    case BAD:
                        effect.setPosition(tempEnemy.getBounds().x, tempEnemy.getBounds().y);
                        effect.start();
                        effects.add(effect);
                        laserOn = true;
                        // the device will vibrate for x milliseconds
//                        Gdx.input.vibrate(300);

                        tempList.remove(i);
                        i--;
                        break;

                    case COVERED:

                        // cant fire on this one
                        ending--;
                        flash = true;
                        ewSound.play(0.5f);

                        break;

                    case GOOD_UNCOVERED:
                        effect.setPosition(tempEnemy.getBounds().x, tempEnemy.getBounds().y);
                        effect.start();
                        effects.add(effect);
                        laserOn = true;
                        // tagging the enemy to be removed
                        score+=10;
                        // playing the sound
                        eatSound.play();
                        // Remove the enemy from the list
                        tempList.remove(i);
                        i--;
                        break;

                    default:
                        break;
                }
            }
        }

        enemyHandler.setList(tempList);

        enemyLaserDuration+=delta;
        Gdx.app.log("JSD", "enemyLaserDuration " + enemyLaserDuration);

        if (enemyLaserDuration > 1.0f) {
            //for (int i = 1; i < enemyLaserCounter; i++) {
            enemyLasers.clear();
            enemyLaserCounter = 0;
            enemyLaserDuration = 0;

        }

        // cleaning the effects list
        for (int i=0; i< effects.size(); i++) {
            if (effects.get(i).isComplete()) {
                effects.remove(i);
                i--;
            }
        }

        //for (int i=0; i< enemyLaserEffects.size(); i++) {
        //    if (enemyLaserEffects.get(i).isComplete()) {
        //        enemyLaserEffects.remove(i);
        //        i--;
        //    }
        //}

        // if ending reaches 0 the lives are gone and it's gameOver
        // the Ending Screen will be called
        if (ending == 0) {
            dispose();
            game.setScreen(new EndScreen(score, game));
        }

        // Drawing everything on the screen
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        playerArmSprite.draw(batch);//batch.draw(santaTexture, Gdx.graphics.getWidth()/2, 0, 256, 256);
        playerSprite.draw(batch);//batch.draw(santaTexture, Gdx.graphics.getWidth()/2, 0, 256, 256);

        //batch.draw(coveredTexture, touchedArea.x, touchedArea.y, 100.0f, 100.0f);
        drawEnemy();

        // Displaying the particle effects
        for (ParticleEffect eff : effects) {
            eff.draw(batch, delta/2);
        }

        //for (ParticleEffect eff : enemyLaserEffects) {
        //    eff.draw(batch, delta/2);
        //}

        //Start a player laser to enemy
        if (laserOn) {
            if(!santaFlipped){
                playerLaser.render();
            }
            if(santaFlipped){
                playerLaser.render();
            }
            laserDuration += delta;
            if (laserDuration > 0.50f) {
                laserDuration = 0.0f;
                laserOn = false;
                //playerSprite.rotate(-playerLaser.degrees + 90);
                //playerSprite.rotate(-playerLaser.degrees+90);

            }
        }

        for (Laser eLaser : enemyLasers) {
            //Start an enemy laser to player
            eLaser.render();
        }

         // HUD
        gameFont.draw(batch, String.valueOf(score), 20.0f, Gdx.graphics.getHeight()-20.0f);
        // Drawing the lives on screen
        for (int i = ending;i>0;i--){
            if (i == 3)
                batch.draw(penguinTexture, Gdx.graphics.getWidth()/1.3f, Gdx.graphics.getHeight()/1.1f, 0.5f*Gdx.graphics.getPpcX(), 0.5f*Gdx.graphics.getPpcY());
            if (i == 2)
                batch.draw(penguinTexture, Gdx.graphics.getWidth()/1.2f, Gdx.graphics.getHeight()/1.1f, 0.5f*Gdx.graphics.getPpcX(), 0.5f*Gdx.graphics.getPpcY());
            if (i == 1)
                batch.draw(penguinTexture, Gdx.graphics.getWidth()/1.11f, Gdx.graphics.getHeight()/1.1f, 0.5f*Gdx.graphics.getPpcX(), 0.5f*Gdx.graphics.getPpcY());

        }

        // Displaying a flash when the user snatched bad enemy
        if (flash) {
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(Color.WHITE);
            shape.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shape.end();
            flashDuration += delta;
            if (flashDuration > 0.50f)
                flashDuration = 0.0f;
                flash = false;
        }

        batch.end();

    }

    // Draw the enemy
    private void drawEnemy() {
        for (Enemy f : enemyHandler.getList()) {

            // If the index is -1 (texture not set), assign a number between 0 and 4
            if (f.getIndex() == -1) {
                int index = (int)(Math.random()*goodTexture.length);
                f.setIndex(index);
            }
            // draw the enemy according to its state
            switch (f.getState()) {
                case GOOD: case GOOD_UNCOVERED:
                    batch.draw(goodTexture[f.getIndex()],
                            f.getBounds().x,
                            f.getBounds().y,
                            f.getBounds().width,
                            f.getBounds().height
                    );
                    break;

                case BAD:
                    batch.draw(badTexture[f.getIndex()],
                            f.getBounds().x,
                            f.getBounds().y,
                            f.getBounds().width,
                            f.getBounds().height
                    );
                    break;

                case COVERED:
                    batch.draw(coveredTexture,
                            f.getBounds().x,
                            f.getBounds().y,
                            f.getBounds().width,
                            f.getBounds().height
                    );
                    break;

                default:
                    break;
            }
        }
    }

    public void setTouchedArea(Vector2 area) {
        this.touchedArea = area;
    }

    //public void rotateBy(float amountInDegrees) {
    //    Array<ParticleEmitter> emitters = enemyLaserEffect.getEmitters();
    //    for (int i = 0; i < emitters.size; i++) {
    //        ParticleEmitter.ScaledNumericValue val = emitters.get(i).getAngle();
    //        float amplitude = (val.getHighMax() - val.getHighMin()) / 2f;
    //        float h1 = amountInDegrees + amplitude;
    //        float h2 = amountInDegrees - amplitude;
    //        val.setHigh(h1, h2);
    //        val.setLow(amountInDegrees);
    //    }
    //}

    public void dispose() {
        batch.dispose();
        eatSound.dispose();
        ewSound.dispose();
        effect.dispose();
        //enemyLaserEffect.dispose();
        gameFont.dispose();
        enemyHandler.clearList();
    }
    /**
     * Accessor
     * @return
     */
    @SuppressWarnings("unchecked")
    public static final <T extends GameRenderer> T get() {
        return (T) instance;
    }
}