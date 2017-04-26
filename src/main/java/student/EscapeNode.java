package student;

import game.Node;
import game.Tile;

/**
 * Created by svince04 on 26/04/2017 for cw-temple.
 */
public class EscapeNode {
  private Node node;
  private Tile tile;
  private EscapeNode parent;
  private int stepsFromStart;
  private int stepsToEnd;
  private int fCost;

  public EscapeNode(Node node) {
    this.node = node;
    this.tile = node.getTile();
  }

  public void setParent(EscapeNode parent) {
    this.parent = parent;
    // Calculate the distance by adding weight of edge with parent node to parent's distance
    this.stepsFromStart = (parent.getStepsFromStart() + node.getEdge(parent.getNode()).length);
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

  public EscapeNode getParent() {
    return parent;
  }

  public Node getNode() {
      return node;
  }
}
