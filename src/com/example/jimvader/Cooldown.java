package com.example.jimvader;

import java.util.Timer;
import java.util.TimerTask;

public class Cooldown {
  private boolean valid;
  private final Timer timer;
  private final long delay = 4;
  private static Cooldown instance;

  public static Cooldown getSharedInstance() {
    if (instance == null) {
      instance = new Cooldown();
    }
    return instance;
  }

  private Cooldown() {
    timer = new Timer();
    valid = true;
  }

  public boolean checkValidity() {
    if (valid) {
      valid = false;
      timer.schedule(new Task(), delay);
      return true;
    }
    return false;
  }

  class Task extends TimerTask {
    @Override
    public void run() {
      valid = true;
    }
  }
}
