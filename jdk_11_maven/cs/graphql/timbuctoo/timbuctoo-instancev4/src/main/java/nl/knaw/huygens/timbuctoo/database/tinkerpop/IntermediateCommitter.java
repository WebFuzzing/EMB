package nl.knaw.huygens.timbuctoo.database.tinkerpop;

public class IntermediateCommitter {
  private final long maxTicks;
  private final Runnable run;
  private long curTick;

  public IntermediateCommitter(long maxTicks, Runnable run) {
    this.maxTicks = maxTicks;
    this.run = run;
    curTick = 0;
  }

  public void tick() {
    curTick++;
    if (maxTicks == curTick) {
      curTick = 0;
      run.run();
    }
  }
}
