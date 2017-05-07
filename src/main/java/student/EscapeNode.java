package student;

import game.Node;

/**
 * Created by svince04 on 26/04/2017 for cw-temple.
 */
public class EscapeNode {
  private Node node;
  private EscapeNode parent;
  private double gold;
  private double cost;
  private double goldPerStep;

  EscapeNode(Node node, EscapeNode parent) {
    this.node = node;
    setParent(parent);
  }

  void setParent(EscapeNode parent) {
    if (parent == null) {
      cost = 0;
    } else {
      this.parent = parent;
      // Calculate the cost by adding weight of edge with parent node to parent's distance
      setCost(parent.getCost() + node.getEdge(parent.getNode()).length());
      setGold();
      setGoldPerStep();
    }
  }

  void setGold() {
    this.gold = node.getTile().getGold();
  }

  double getGold() {
    return gold;
  }

  double getGoldPerStep() {
    return goldPerStep;
  }

  void setGoldPerStep() {
    this.goldPerStep = (getGold() / getCost());
  }

  void setCost(double cost) {
    this.cost = cost;
  }

  double getCost() {
    return cost;
  }

  EscapeNode getParent() {
    return parent;
  }

  public Node getNode() {
    return node;
  }
}
