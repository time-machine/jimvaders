package com.example.jimvader;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;

public class ResultScene extends CameraScene implements IOnSceneTouchListener {
  boolean done;
  BaseActivity activity;

  public ResultScene(Camera pCamera) {
    super(pCamera);
    activity = BaseActivity.getSharedInstance();
    setBackgroundEnabled(false);
    GameScene scene = (GameScene)activity.mCurrentScene;

    float accuracy = 1 - (float) scene.missCount / scene.bulletCount;

    if (Float.isNaN(accuracy)) {
      accuracy = 0;
    }
    accuracy *= 100;

    Text result = new Text(0, 0, activity.mFont,
        activity.getString(R.string.accuracy) + ": "
        + String.format("%.2f", accuracy) + "%",
        activity.getVertexBufferObjectManager());

    final int x = (int)((mCamera.getWidth() - result.getWidth()) / 2);
    final int y = (int)((mCamera.getHeight() - result.getHeight()) / 2);

    done = false;
    result.setPosition(x, mCamera.getHeight() + result.getHeight());
    MoveYModifier mod = new MoveYModifier(3, result.getY(), y) {
      @Override
      protected void onModifierFinished(IEntity pItem) {
        done = true;
      }
    };
    attachChild(result);
    result.registerEntityModifier(mod);
    setOnSceneTouchListener(this);
  }

  @Override
  public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
    if (!done) {
      return true;
    }
    ((GameScene)activity.mCurrentScene).resetValues();
    return false;
  }
}
