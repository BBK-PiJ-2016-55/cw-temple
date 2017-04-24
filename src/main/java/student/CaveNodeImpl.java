package student;

/**
 * Created by svince04 on 24/04/2017 for cw-temple.
 */
public class CaveNodeImpl implements CaveNode {
  private long id;
  private boolean visited = true;
  private int distanceToOrb;

  CaveNodeImpl(long id, int distancetoOrb) {
    this.id = id;
    this.distanceToOrb = distanceToOrb;
  }

  @Override
  public long getId() {
    return id;
  }

  @Override
  public int getDistance() {
    return distanceToOrb;
  }

  @Override
  public boolean visited() {
    return visited;
  }

  @Override
  public boolean setVisited(boolean visited) {
    return false;
  }
}
