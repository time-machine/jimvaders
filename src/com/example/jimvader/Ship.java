package com.example.jimvader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.primitive.Rectangle;

public class Ship {
  private static final float DEFAULT_SPEED = 1.5f;

  public Rectangle sprite;
  public static Ship instance;

  Camera mCamera;
  boolean moveable;

  public static Ship getSharedInstance() {
    if (instance == null) {
      instance = new Ship();
    }
    return instance;
  }

  public void moveShip(float accelerometerSpeedX) {
    if (!moveable) {
      return;
    }

    if (accelerometerSpeedX != 0) {
      int lL = 0;
      int rL = (int) (mCamera.getWidth() - (int) sprite.getWidth());
      float newX;

      // calculate new X, Y coordinates within limits
      if (sprite.getX() >= lL) {
        newX = sprite.getX() + accelerometerSpeedX * DEFAULT_SPEED;
      }
      else {
        newX = lL;
      }

      if (newX <= rL) {
        newX = sprite.getX() + accelerometerSpeedX * DEFAULT_SPEED;
      }
      else {
        newX = rL;
      }

      // double check that new X, Y coordinates are within limits
      if (newX < lL) {
        newX = lL;
      }
      else if (newX > rL) {
        newX = rL;
      }

      sprite.setPosition(newX, sprite.getY());
    }
  }

  public void shoot() {
    if (!moveable) {
      return;
    }

    GameScene scene = (GameScene)BaseActivity.getSharedInstance().mCurrentScene;

    Bullet b = BulletPool.sharedBulletPool().obtainPoolItem();
    b.sprite.setPosition(sprite.getX() + ((sprite.getWidth() - b.sprite.getWidth()) / 2), sprite.getY());
    MoveYModifier mod = new MoveYModifier(1.5f, b.sprite.getY(),
        -b.sprite.getHeight());

    b.sprite.setVisible(true);
    b.sprite.detachSelf();
    scene.attachChild(b.sprite);
    scene.bulletList.add(b);
    b.sprite.registerEntityModifier(mod);
    scene.bulletCount++;
  }

  public void restart() {
    moveable = false;
    MoveXModifier mod = new MoveXModifier(0.2f, sprite.getX(),
        (mCamera.getWidth() - sprite.getWidth()) / 2) {
      @Override
      protected void onModifierFinished(IEntity pItem) {
        super.onModifierFinished(pItem);
        moveable = true;
      }
    };
    sprite.registerEntityModifier(mod);
  }

  private Ship() {
    sprite = new Rectangle(0, 0, 70, 30,
        BaseActivity.getSharedInstance().getVertexBufferObjectManager());
    mCamera = BaseActivity.getSharedInstance().mCamera;
    sprite.setPosition((mCamera.getWidth() - sprite.getWidth()) / 2,
        mCamera.getHeight() - sprite.getHeight() - 10);
    moveable = true;
  }
}
