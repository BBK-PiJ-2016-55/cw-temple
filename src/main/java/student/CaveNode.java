package student;

/**
 * Created by svince04 on 24/04/2017 for cw-temple.
 */
public interface CaveNode {

  long getId();

  int getDistance();

  boolean visited();

  boolean setVisited(boolean visited);

}