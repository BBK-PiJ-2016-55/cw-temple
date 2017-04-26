package student;

import game.Node;
import game.NodeStatus;
import game.Tile;

/**
 * Created by svince04 on 26/04/2017 for cw-temple.
 */
public class EscapeNode {
  private Node node;
  private Tile tile;
  private Node parent;
  private int stepsFromStart;
  private int stepsToEnd;
  private int fCost;

  public EscapeNode(Node node) {
    this.node = node;
    this.tile = node.getTile();
  }

  public void setParent(Node parent) {
    this.parent = parent;
  }

  public void setStepsFromStart(int steps) {
    stepsFromStart = steps;
    setfCost(stepsFromStart, stepsToEnd);
  }

  public void setStepsToEnd(int steps) {
    stepsToEnd = steps;
    setfCost(stepsFromStart, stepsToEnd);
  }

  private void setfCost(int stepsStart, int stepsEnd) {
    fCost = stepsStart + stepsEnd;
  }

  public int getStepsFromStart() {
    return stepsFromStart;
  }

  public int getStepsToEnd() {
    return stepsToEnd;
  }

  public int getFcost() {
    return fCost;
  }

  public Node getParent() {
    return parent;
  }
}
