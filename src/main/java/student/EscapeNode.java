package student;

import game.Node;

import java.util.Objects;

/**
 * Created by svince04 on 26/04/2017 for cw-temple.
 */
public class EscapeNode {
  private Node node;
  private EscapeNode parent;
  private double cost;
  private double routeGold;
  private double routeGoldPerStep;

  EscapeNode(Node node, EscapeNode parent) {
    this.node = node;
    setParent(parent);
  }

  void setParent(EscapeNode parent) {
    if (parent == null) {
      cost = 0;
      routeGold = getNode().getTile().getGold();
    } else {
      this.parent = parent;
      setCost();
      setRouteGold();
    }
  }

  private void setCost() {
    this.cost = parent.getCost() + node.getEdge(parent.getNode()).length();
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

  @Override
  public boolean equals(Object obj) {
    return obj instanceof EscapeNode && node.equals(((EscapeNode) obj).getNode());
  }

  @Override
  public int hashCode() {
    return Objects.hash(node);
  }

  private double getCumulativeGold() {
    return routeGold;
  }

  private void setRouteGold() {
    this.routeGold = getParent().getCumulativeGold() + getNode().getTile().getGold();
    setRouteGoldPerStep();
  }

  private void setRouteGoldPerStep() {
    this.routeGoldPerStep = routeGold / cost;
  }

  double getRouteGoldPerStep() {
    return routeGoldPerStep;
  }

}
