package com.example.jimvader;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSCounter;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.ui.activity.BaseGameActivity;

import android.graphics.Typeface;

public class BaseActivity extends BaseGameActivity {
  static final int CAMERA_WIDTH = 480;
  static final int CAMERA_HEIGHT = 800;

  public Font mFont;
  public Camera mCamera;

  // a reference to the current scene
  public Scene mCurrentScene;
  public static BaseActivity instance;

  FPSCounter fpsCounter;

  @Override
  public EngineOptions onCreateEngineOptions() {
    instance = this;
    mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
    return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
        new FillResolutionPolicy(), mCamera);
  }

  @Override
  public void onCreateResources(
      OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
    mFont = FontFactory.create(getFontManager(), getTextureManager(),
        256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
    mFont.load();
    pOnCreateResourcesCallback.onCreateResourcesFinished();
  }

  @Override
  public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
      throws Exception {
    mCurrentScene = new SplashScene();
    pOnCreateSceneCallback.onCreateSceneFinished(mCurrentScene);
  }

  @Override
  public void onPopulateScene(Scene pScene,
      OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
    fpsCounter = new FPSCounter();
    mEngine.registerUpdateHandler(fpsCounter);
    pOnPopulateSceneCallback.onPopulateSceneFinished();
  }

  @Override
  public void onBackPressed() {
    if (mCurrentScene instanceof GameScene) {
      ((GameScene)mCurrentScene).detach();
    }
    mCurrentScene = null;
    SensorListener.instance = null;
    super.onBackPressed();
  }

  public static BaseActivity getSharedInstance() {
    return instance;
  }

  // to change the current main scene
  public void setCurrentScene(Scene scene) {
    mCurrentScene = scene;
    getEngine().setScene(mCurrentScene);
  }
}
