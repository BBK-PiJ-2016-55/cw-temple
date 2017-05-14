package student;

import game.Node;

import java.util.Objects;

/**
 * Wrapper class for {@link Node} objects. Contains useful information about the costs and
 * benefits of traversing the path to this position and enables the Explorer class to
 * select paths that provide a good return in gold for time taken/effort expended.
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

  /**
   * Sets a pointer to the EscapeNode's parent (i.e, the previous step in the current route)
   * and prompts updating of any other class variables that would change if the parent were
   * redefined: cost, routeGolg, routeGoldPerStep.
   * @param parent {@link EscapeNode} to be set as parent
   */
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

  /**
   * Sets the cost of the current route - how many steps it took to get here.
   */
  private void setCost() {
    this.cost = parent.getCost() + node.getEdge(parent.getNode()).length();
  }

  /**
   * Returns the number of steps/amount of time it would take to reach this node.
   * @return the cost of the current route.
   */
  double getCost() {
    return cost;
  }

  /**
   * Returns the {@link EscapeNode} this object has a pointer set at, which can be
   * used to build a route between the current position and the target destination.
   * @return the {@link EscapeNode} define as this EscapeNode's parent.
   */
  EscapeNode getParent() {
    return parent;
  }

  /**
   * Returns the {@link Node} object wrapped in this EscapeNode.
   * @return {@link Node} object.
   */
  public Node getNode() {
    return node;
  }

  /**
   * Local implementation of equals().
   * @param obj the object to be compared with this EscapeNode.
   * @return true if obj is an {@link EscapeNode} containing the same {@link Node} as
   *     the current EscapeNode and false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof EscapeNode && node.equals(((EscapeNode) obj).getNode());
  }

  /**
   * Local implementation of hashCode().
   * @return a hashcode for this, based solely on its node.
   */
  @Override
  public int hashCode() {
    return Objects.hash(node);
  }

  /**
   * Returns the amount of gold present along the route to this EscapeNode.
   * @return the amount of gold present along the optimal route to this EscapeNode
   */
  private double getRouteGold() {
    return routeGold;
  }

  /**
   * Calculates and sets the total gold found along the route so far.
   */
  private void setRouteGold() {
    this.routeGold = getParent().getRouteGold() + getNode().getTile().getGold();
    setRouteGoldPerStep();
  }

  /**
   * Calculates and sets the cost per step for this route to be used when selecting
   * the best route to take.
   */
  private void setRouteGoldPerStep() {
    this.routeGoldPerStep = routeGold / cost;
  }

  /**
   * Returns the cost per step for this route to be used when selecting
   * the best route to take.
   * @return the routeGoldPerStep.
   */
  double getRouteGoldPerStep() {
    return routeGoldPerStep;
  }

}
