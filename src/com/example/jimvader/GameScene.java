package com.example.jimvader;

import java.util.Iterator;
import java.util.LinkedList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityFactory;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.RotationParticleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

public class GameScene extends Scene implements IOnSceneTouchListener {
  public Ship ship;
  public LinkedList<Bullet> bulletList;
  public int bulletCount;
  public int missCount;
  public float accelerometerSpeedX;

  Camera mCamera;
  SensorManager sensorManager;
  final Text fpsText;

  public GameScene() {
    setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
    attachChild(new EnemyLayer(100));

    mCamera = BaseActivity.getSharedInstance().mCamera;
    ship = Ship.getSharedInstance();
    ship.sprite.detachSelf();
    ship.sprite.setVisible(true);
    attachChild(ship.sprite);

    fpsText = new Text(10, 10,
        BaseActivity.getSharedInstance().mFont, "0123456789",
        BaseActivity.getSharedInstance().getVertexBufferObjectManager());
    attachChild(fpsText);

    bulletList = new LinkedList<Bullet>();

    BaseActivity.getSharedInstance().setCurrentScene(this);
    sensorManager = (SensorManager) BaseActivity.getSharedInstance()
        .getSystemService(BaseGameActivity.SENSOR_SERVICE);
    SensorListener.getSharedInstance();
    sensorManager.registerListener(SensorListener.getSharedInstance(),
        sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0),
        SensorManager.SENSOR_DELAY_GAME);

    resetValues();

    setOnSceneTouchListener(this);
  }

  public void moveShip() {
    ship.moveShip(accelerometerSpeedX);

  }

  public void updateFPS() {
    fpsText.setText("" + BaseActivity.getSharedInstance().fpsCounter.getFPS());
  }

  public void cleaner() {
    synchronized (this) {
      // if all Enemies are killed
      if (EnemyLayer.isEmpty()) {
        Log.v("Jimvaders", "GameScene Cleaner() cleared");
        setChildScene(new ResultScene(mCamera));
        clearUpdateHandlers();
      }
      else {
        Iterator<Enemy> eIt = EnemyLayer.getIterator();
        while (eIt.hasNext()) {
          Enemy e = eIt.next();
          Iterator<Bullet> it = bulletList.iterator();
          while (it.hasNext()) {
            Bullet b = it.next();
            if (b.sprite.getY() <= -b.sprite.getHeight()) {
              BulletPool.sharedBulletPool().recyclePoolItem(b);
              it.remove();
              missCount++;
              continue;
            }

            if (b.sprite.collidesWith(e.sprite)) {
              if (!e.gotHit()) {
                createExplosion(e.sprite.getX(), e.sprite.getY(),
                    e.sprite.getParent(), BaseActivity.getSharedInstance());
                EnemyPool.sharedEnemyPool().recyclePoolItem(e);
                eIt.remove();
              }
              BulletPool.sharedBulletPool().recyclePoolItem(b);
              it.remove();
              break;
            }
          }
        }
      }
    }
  }

  // reset values and restart the game
  public void resetValues() {
    missCount = 0;
    bulletCount = 0;
    ship.restart();
    EnemyLayer.purgeAndRestart();
    clearChildScene();
    registerUpdateHandler(new GameLoopUpdateHandler());
  }

  public void detach() {
    Log.v("Jimvaders", "GameScene onDetached()");
    clearUpdateHandlers();
    for (Bullet b : bulletList) {
      BulletPool.sharedBulletPool().recyclePoolItem(b);
    }
    bulletList.clear();
    detachChildren();
    Ship.instance = null;
    EnemyPool.instance = null;
    BulletPool.instance = null;
  }

  @Override
  public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
    if (!Cooldown.getSharedInstance().checkValidity()) {
      return false;
    }

    synchronized (this) {
      ship.shoot();
    }
    return true;
  }

  private void createExplosion(final float posX, final float posY,
      final IEntity target, final BaseActivity activity) {
    int mNumPart = 15;
    int mTimePart = 2;

    PointParticleEmitter particleEmitter = new PointParticleEmitter(posX, posY);
    IEntityFactory<Rectangle> recFact = new IEntityFactory<Rectangle>() {
      @Override
      public Rectangle create(float pX, float pY) {
        Rectangle rect = new Rectangle(posX, posY, 10, 10,
            activity.getVertexBufferObjectManager());
        rect.setColor(Color.GREEN);
        return rect;
      }
    };
    final ParticleSystem<Rectangle> particleSystem =
        new ParticleSystem<Rectangle>(recFact, particleEmitter, 500, 500,
            mNumPart);
    particleSystem.addParticleInitializer(
        new VelocityParticleInitializer<Rectangle>(-50, 50, -50, 50));
    particleSystem.addParticleModifier(
        new AlphaParticleModifier<Rectangle>(0, 0.6f * mTimePart, 1, 0));
    particleSystem.addParticleModifier(
        new RotationParticleModifier<Rectangle>(0, mTimePart, 0, 360));

    target.attachChild(particleSystem);
    target.registerUpdateHandler(new TimerHandler(mTimePart,
        new ITimerCallback() {
      @Override
          public void onTimePassed(final TimerHandler pTimerHandler) {
        particleSystem.detachSelf();
        target.sortChildren();
        target.unregisterUpdateHandler(pTimerHandler);
      }
    }));
  }
}
