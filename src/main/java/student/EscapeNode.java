package student;

import game.Node;

/**
 * Created by svince04 on 26/04/2017 for cw-temple.
 */
public class EscapeNode {
  private Node node;
  private EscapeNode parent;
  private int cost;


  public EscapeNode(Node node, EscapeNode parent) {
    this.node = node;
    setParent(parent);
  }

  public void setParent(EscapeNode parent) {

    if (parent == null) {
      cost = 0;
      this.parent = null;
    } else {
      this.parent = parent;
      // Calculate the distance by adding weight of edge with parent node to parent's distance
      this.cost = (parent.getCost() + node.getEdge(parent.getNode()).length());
    }
  }

  public void setCost(int steps) {
    cost = steps;
  }

  public int getCost() {
    return cost;
  }


  public EscapeNode getParent() {
    return parent;
  }

  public Node getNode() {
      return node;
  }

}
