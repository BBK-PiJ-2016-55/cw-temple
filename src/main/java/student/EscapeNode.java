package student;

import game.Node;

/**
 * Created by svince04 on 26/04/2017 for cw-temple.
 */
public class EscapeNode implements Comparable<EscapeNode> {
  private Node node;
  private EscapeNode parent;
  private int cost;
  private int gold;


  EscapeNode(Node node, EscapeNode parent) {
    this.node = node;
    setParent(parent);
    this.gold = node.getTile().getGold();
  }

  int getGold() {
    return this.gold;
  }

  void clearGold() {
    this.gold = 0;
  }
  void setParent(EscapeNode parent) {

    if (parent == null) {
      cost = 0;
    } else {
      this.parent = parent;
      // Calculate the distance by adding weight of edge with parent node to parent's distance
      setCost(parent.getCost() + node.getEdge(parent.getNode()).length());
    }
  }

  void setCost(int steps) {
    cost = steps;
  }

  int getCost() {
    return cost;
  }

  EscapeNode getParent() {
    return parent;
  }

  public Node getNode() {
    return node;
  }

  // Implements custom sort to use gold value to break tie in cost value
  @Override
  public int compareTo(EscapeNode other) {
    int result = Integer.compare(this.cost, other.cost);
    if (result == 0) {
      result = ((Integer.compare(this.gold, other.gold) > 0) ? this.gold : other.gold);
    }
    return result;
  }

}
