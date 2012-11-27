package com.example.jimvader;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.andengine.entity.Entity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;

public class EnemyLayer extends Entity {
  private final LinkedList<Enemy> enemies;

  public static EnemyLayer instance;
  public int enemyCount;

  public static EnemyLayer getSharedInstance() {
    return instance;
  }

  public static boolean isEmpty() {
    return (instance.enemies.size() == 0);
  }

  public static Iterator<Enemy> getIterator() {
    return instance.enemies.iterator();
  }

  public EnemyLayer(int x) {
    enemies = new LinkedList<Enemy>();
    instance = this;
    enemyCount = x;
  }

  public void restart() {
    enemies.clear();
    clearEntityModifiers();
    clearUpdateHandlers();

    for (int i = 0; i < enemyCount; i++) {
      Enemy e = EnemyPool.sharedEnemyPool().obtainPoolItem();
      float finalPosX = (i % 9) * 1.5f * e.sprite.getWidth();
      float finalPosY = (i / 9) * e.sprite.getHeight() * 2;

      Random r = new Random();
      e.sprite.setPosition(r.nextInt(2) == 0 ? -e.sprite.getWidth() * 3
          : BaseActivity.CAMERA_WIDTH + e.sprite.getWidth() * 3,
          (r.nextInt(5) + 1) * e.sprite.getHeight());
      e.sprite.setVisible(true);

      attachChild(e.sprite);
      e.sprite.registerEntityModifier(new MoveModifier(2,
          e.sprite.getX(), finalPosX, e.sprite.getY(), finalPosY));

      enemies.add(e);

      setVisible(true);
      setPosition(50, 30);

      MoveXModifier moveRight = new MoveXModifier(1, 30, 70);
      MoveXModifier moveLeft = new MoveXModifier(1, 70, 30);
      MoveYModifier moveDown = new MoveYModifier(1, 20, 60);
      MoveYModifier moveUp = new MoveYModifier(1, 60, 20);

      registerEntityModifier(new LoopEntityModifier(
          new SequenceEntityModifier(moveRight, moveDown, moveLeft, moveUp)));
    }
  }

  public void purge() {
    detachChildren();
    for (Enemy e : enemies) {
      EnemyPool.sharedEnemyPool().recyclePoolItem(e);
    }
    enemies.clear();
  }

  public static void purgeAndRestart() {
    instance.purge();
    instance.restart();
  }

  @Override
  public void onDetached() {
    purge();
    clearUpdateHandlers();
    super.onDetached();
  }
}
