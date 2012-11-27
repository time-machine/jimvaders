package com.example.jimvader;

import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.primitive.Rectangle;

public class Enemy {
  public Rectangle sprite;
  public int hp;
  protected final int MAX_HEALTH = 2; // max health for each enemy

  public Enemy() {
    sprite = new Rectangle(0, 0, 30, 30,
        BaseActivity.getSharedInstance().getVertexBufferObjectManager());
    sprite.setColor(0.09904f, 0.8574f, 0.1786f);
    init();
  }

  // initializing Enemy object, used by the constructor and the EnemyPool class
  public void init() {
    hp = MAX_HEALTH;
    sprite.registerEntityModifier(new LoopEntityModifier(
        new RotationModifier(5, 0, 360)));
  }

  public void clean() {
    sprite.clearEntityModifiers();
    sprite.clearUpdateHandlers();
  }

  // applying hit and checking if enemy died or not
  // return false if enemy died
  public boolean gotHit() {
    synchronized (this) {
      return (--hp > 0);
    }
  }
}
