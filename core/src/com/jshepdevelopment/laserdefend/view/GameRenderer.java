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
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.jshepdevelopment.laserdefend.model.Enemy;
import com.jshepdevelopment.laserdefend.model.EnemyHandler;
import com.jshepdevelopment.laserdefend.model.Laser;
import com.jshepdevelopment.laserdefend.screens.EndScreen;

import java.util.ArrayList;

public class GameRenderer {

    private Game game;

    private Texture texLaserS1;
    private Texture texLaserS2;
    private Texture texLaserM1;
    private Texture texLaserM2;
    private Texture texLaserE1;
    private Texture texLaserE2;

    private Sprite spriteLaserS1;
    private Sprite spriteLaserS2;
    private Sprite spriteLaserM1;
    private Sprite spriteLaserM2;
    private Sprite spriteLaserE1;
    private Sprite spriteLaserE2;

    private Laser laser1;

    private TextureRegion backgroundTexture;
    private TextureRegion[] goodTexture = new TextureRegion[5];
    private TextureRegion[] badTexture = new TextureRegion[5];
    private TextureRegion coveredTexture;
    private TextureRegion penguinTexture;

    public SpriteBatch batch;
    private Vector2 touchedArea = new Vector2();
    private Vector2 playerLaserDest = new Vector2();

    private BitmapFont gameFont;
    private Sound eatSound;
    private Sound ewSound;
    private ParticleEffect effect;
    private ArrayList<ParticleEffect> effects = new ArrayList<ParticleEffect>();

    private ParticleEffect enemyLaserEffect;
    private ArrayList<ParticleEffect> enemyLaserEffects = new ArrayList<ParticleEffect>();

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
        backgroundTexture = new TextureRegion(new Texture(Gdx.files.internal("background/background.png")));

        batch = new SpriteBatch();

        this.texLaserS1 = new Texture(Gdx.files.internal("effects/beamstart1.png"));
        this.texLaserS2 = new Texture(Gdx.files.internal("effects/beamstart2.png"));
        this.texLaserM1 = new Texture(Gdx.files.internal("effects/beammid1.png"));
        this.texLaserM2 = new Texture(Gdx.files.internal("effects/beammid2.png"));
        this.texLaserE1 = new Texture(Gdx.files.internal("effects/beamend1.png"));
        this.texLaserE2 = new Texture(Gdx.files.internal("effects/beamend2.png"));

        this.spriteLaserS1 = new Sprite(this.texLaserS1);
        this.spriteLaserS2 = new Sprite(this.texLaserS2);
        this.spriteLaserM1 = new Sprite(this.texLaserM1);
        this.spriteLaserM2 = new Sprite(this.texLaserM2);
        this.spriteLaserE1 = new Sprite(this.texLaserE1);
        this.spriteLaserE2 = new Sprite(this.texLaserE2);

        this.laser1 = new Laser();
        laser1.begin1 = this.spriteLaserS1;
        laser1.begin2 = this.spriteLaserS2;
        laser1.mid1 = this.spriteLaserM1;
        laser1.mid2 = this.spriteLaserM2;
        laser1.end1 = this.spriteLaserE1;
        laser1.end2 = this.spriteLaserE2;
        laser1.position.set(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);

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

        // Loading the enemy laser particle effect
        enemyLaserEffect = new ParticleEffect();
        enemyLaserEffect.load(Gdx.files.internal("effects/enemylaser.p"), Gdx.files.internal("effects"));
        enemyLaserEffect.setPosition(0, 0);
        enemyLaserEffect.start();

    }

	/*
	 * Render fields
	 */

    private float timePassed = 0.0f;
    private float timeGood = 0.0f;
    private float timeBad = 0.0f;
    private float timeCoveredAppearance = 0.0f;
    private int score = 0;
    private int ending = 3;
    private boolean flash = false;
    private boolean laserOn = false;
    private float flashDuration = 0.0f;
    private float laserDuration = 0.0f;

    // Render the game
    public void render(float delta)
    {
        // Starting the timers used to place food on the screen at certain intervals of time
        timePassed += delta;
        timeGood += delta;
        timeBad+=delta;
        timeCoveredAppearance+=delta;

        // Placing the enemy on-screen
        if (timeGood > .8f) {
            enemyHandler.addEnemy(
                    new Vector2(
                            (int)(Math.random()*Gdx.graphics.getWidth()),
                            (int)(Math.random()*Gdx.graphics.getHeight())),
                    Enemy.EnemyState.GOOD
            );
            timeGood = 0.0f;
        }

        if (timeBad > 2.0f) {
            enemyHandler.addEnemy(
                    new Vector2(
                            (int)(Math.random()*Gdx.graphics.getWidth()),
                            (int)(Math.random()*Gdx.graphics.getHeight())),
                    Enemy.EnemyState.BAD
            );

            timeBad = 0.0f;
        }

        if (timeCoveredAppearance > 3.5f) {

            int tempx = (int)(Math.random()*Gdx.graphics.getWidth());
            int tempy = (int)(Math.random()*Gdx.graphics.getHeight());
            enemyHandler.addEnemy(new Vector2(tempx, tempy), Enemy.EnemyState.COVERED);
            timeCoveredAppearance = 0.0f;

            enemyLaserEffect.setPosition(tempx,tempy);
            double degreesA = Math.atan2(
                    Gdx.graphics.getHeight()/2 - tempy,
                    Gdx.graphics.getWidth()/2 - tempx
            ) * 180.0d / Math.PI;

            rotateBy((float) degreesA);
            enemyLaserEffect.start();
            enemyLaserEffects.add(enemyLaserEffect);
        }

        // Animating the enemy
        enemyHandler.animate(delta*100);

        // Checking if the covered enemy should show the contents
        for (Enemy enemy : enemyHandler.getList()){
            if (enemy.getState() == Enemy.EnemyState.COVERED)
                if (enemy.getBounds().width > .8f * Gdx.graphics.getPpcX()){
                    int rand = (int)(Math.random()*2);
                    if (rand % 2 == 0)
                        enemy.setState(Enemy.EnemyState.GOOD_UNCOVERED);
                    else
                        enemy.setState(Enemy.EnemyState.BAD);

                }
        }



        // Checking the collision between the touched area and the enemy
        ArrayList<Enemy> tempList = enemyHandler.getList();
        for (int i = 0; i < tempList.size(); i++) {
            Enemy tempEnemy = tempList.get(i);
            if (touchedArea.x >= tempEnemy.getBounds().x &&
                    touchedArea.x <= tempEnemy.getBounds().x+tempEnemy.getBounds().width &&
                    touchedArea.y >= tempEnemy.getBounds().y &&
                    touchedArea.y <= tempEnemy.getBounds().y+tempEnemy.getBounds().height) {
                // set the laser destination
                playerLaserDest.x = touchedArea.x;
                playerLaserDest.y = touchedArea.y;
                Gdx.app.log("JSLOG", "playerLaserDest.x " + playerLaserDest.x + " playerLaserDest.y " + playerLaserDest.y);
                //Gdx.app.log("JSLOG", "touchedArea.x " + touchedArea.x + " touchedArea.y " + touchedArea.y);

                //laser1.distance = (150*100%300);
                double laserDistance = Math.sqrt((Gdx.graphics.getWidth()/2-playerLaserDest.x)*
                        (Gdx.graphics.getWidth()/2-playerLaserDest.x) +
                        (Gdx.graphics.getHeight()/2-playerLaserDest.y)*
                                (Gdx.graphics.getHeight()/2-playerLaserDest.y));
                laser1.distance = (float)laserDistance;

                double laserDegrees = Math.atan2(
                        playerLaserDest.y - Gdx.graphics.getHeight()/2,
                        playerLaserDest.x - Gdx.graphics.getWidth()/2
                ) * 180.0d / Math.PI;
                
                laser1.degrees = ((float)laserDegrees)-90; //(float)laserDegrees;
                Gdx.app.log("JSLOG", "laser1.degrees: " + laserDegrees);


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
                        // tagging the food to be removed
                        ending--;
                        flash = true;
                        ewSound.play(0.5f);
                        // the device will vibrate for x milliseconds
//                        Gdx.input.vibrate(300);

                        tempList.remove(i);
                        i--;
                        break;

                    case COVERED:
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

        // cleaning the effects list
        for (int i=0; i< effects.size(); i++) {
            if (effects.get(i).isComplete()) {
                effects.remove(i);
                i--;
            }
        }
        for (int i=0; i< enemyLaserEffects.size(); i++) {
            if (enemyLaserEffects.get(i).isComplete()) {
                enemyLaserEffects.remove(i);
                i--;
            }
        }

        // if ending reaches 0 the lives are gone and it's gameOver
        // the Ending Screen will be called
        if (ending == 0) {
            dispose();
            game.setScreen(new EndScreen(score, game));
        }


        // Drawing everything on the screen
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        //batch.draw(coveredTexture, touchedArea.x, touchedArea.y, 100.0f, 100.0f);
        drawEnemy();
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
        // Displaying the particle effects
        for (ParticleEffect eff : effects) {
            eff.draw(batch, delta/2);
        }
        for (ParticleEffect eff : enemyLaserEffects) {
            eff.draw(batch, delta/2);
        }

        //Start a player laser to enemy
        if (laserOn) {

            laser1.render();

            if (laserDuration > 0.50f) {
                laserDuration = 0.0f;
                laserOn = false;
            }
        }

        // Displaying a flash when the user snatched bad enemy
        if (flash) {

            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(Color.WHITE);
            shape.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shape.end();
            flashDuration +=delta;
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

    public void rotateBy(float amountInDegrees) {
        Array<ParticleEmitter> emitters = enemyLaserEffect.getEmitters();
        for (int i = 0; i < emitters.size; i++) {
            ParticleEmitter.ScaledNumericValue val = emitters.get(i).getAngle();
            float amplitude = (val.getHighMax() - val.getHighMin()) / 2f;
            float h1 = amountInDegrees + amplitude;
            float h2 = amountInDegrees - amplitude;
            val.setHigh(h1, h2);
            val.setLow(amountInDegrees);
        }
    }

    public void dispose() {
        batch.dispose();
        eatSound.dispose();
        ewSound.dispose();
        effect.dispose();
        enemyLaserEffect.dispose();
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