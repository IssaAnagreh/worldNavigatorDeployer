package com.worldNavigator;
import java.util.Timer;
import java.util.TimerTask;

public final class GameTimer extends PlayerController {
  Timer timer;
  private int time = 0;
  public final int SECONDS;
  public int remainingTime;
  private static final int MILLI_IN_SEC = 1000;
  private final PlayerModel playerModel;

  public GameTimer(int seconds, PlayerModel playerModel) {
    super(playerModel);
    timer = new Timer();
    timer.schedule(new FinishTask(), MILLI_IN_SEC * seconds);
    timer.schedule(new RemindTask(), 0, MILLI_IN_SEC);
    this.SECONDS = seconds;
    this.playerModel = playerModel;
    this.playerModel.notify_player("This game will end in " + seconds + " seconds");
  }

  public void getRemaining_time() {
    this.playerModel.notify_player("Remaining time: " + remainingTime + " minutes");
  }

  class RemindTask extends TimerTask {
    public void run() {
      int SEC_IN_MIN = 60;
      int new_remaining_time = (SECONDS / SEC_IN_MIN) - (++time / SEC_IN_MIN);
      if (remainingTime != new_remaining_time) {
        remainingTime = new_remaining_time;
      }
    }
  }

  class FinishTask extends TimerTask {
    public void run() {
      System.out.println("Time's up!");
      timer.cancel(); // Terminate the timer thread
      System.exit(1);
      quit();
    }
  }

  @Override
  public String toString() {
    return "Game Timer";
  }
}
