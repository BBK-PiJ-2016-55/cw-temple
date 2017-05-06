package student;

import game.EscapeState;
import game.GameState;
import game.Node;

import java.util.*;

/**
 * Created by svince04 on 06/05/2017 for cw-temple.
 */
public class RouteFinder {
  private EscapeState state;
  private Node startNode;
  private EscapeNode sNode;
  private Node destNode;
  private EscapeNode dNode;
  private List<EscapeNode> openList = new LinkedList<>();
  private List<EscapeNode> closedList = new ArrayList<>();
  private Stack<EscapeNode> bestRouteStack = new Stack<>();

  // Constructor
  public RouteFinder(EscapeState state, Node destNode) {
    this.state = state;
    this.startNode = state.getCurrentNode();
    this.destNode = destNode;
  }

  public Stack<EscapeNode> getRoute() {
    // At start, add the spawn location to openList and clear closedList
    sNode = new EscapeNode(startNode, null);
    openList.add(sNode);
    closedList.clear();

    // While there are possible next steps in openlist and we aren't at the destination
    while (!openList.isEmpty()) {
      openList.sort(Comparator.comparing(EscapeNode :: getCost));
      //  Remove the most promising next step from openList and add it to closedList
      EscapeNode nextStep = openList.remove(0);

      closedList.add(nextStep);

      // Assess each neighbor of the step.
      assessNeighbours(nextStep);

    }

    buildPath();
    return bestRouteStack;

  }

  public void buildPath() {
    EscapeNode current = dNode;
    // Work backwards from exit, adding each parent to route stack
    while (current.getParent() != null) {
      bestRouteStack.push(current);
      current = current.getParent();
    }
  }

  public void assessNeighbours(EscapeNode current) {
    Set<Node> neighbours = current.getNode().getNeighbours();

    // For each neighbor:
    for (Node n : neighbours) {

      // Calculate the path cost of reaching the neighbor
      EscapeNode temp = checkLists(n);

      // If the location isn’t in either open or closed list then create new EscNode
      // for the location and add it to openList
      if (temp == null) {
        EscapeNode newNode = new EscapeNode(n, current);
        openList.add(newNode);
        System.out.println("Cost to reach new node is: " + newNode.getCost());
      } else {
        int parentCost = current.getCost();
        System.out.println("Parent cost: " + parentCost);
        EscapeNode newNode = new EscapeNode(n, current);
        // If the cost is less than the cost known for this location then remove it from
        // the open or closed lists (since we’ve now found a better route)
        if (temp.getCost() < newNode.getCost()) {
          if (closedList.contains(temp)) {
            closedList.remove(temp);
          }

          if (openList.contains(temp)) {
            openList.remove(temp);
            openList.add(newNode);
          }
        }
      }
    }
  }


    // Method to check if the node we're evaluating has already been seen & converted to EscapeNode

  public EscapeNode checkLists(Node node) {
    for (EscapeNode eNode : openList) {
      if (eNode.getNode().equals(node)) {
        System.out.println("Node " + node.getId() + " is present in openList");
        return eNode;
      }
    }

    for (EscapeNode eNode : closedList) {
      if (eNode.getNode().equals(node)) {
        System.out.println("Node " + node.getId() + " is present in closedList");
        return eNode;
      }
    }
    return null;
  }

  public EscapeNode closedListContains(Node node) {
    for (EscapeNode eNode : closedList) {
      if (eNode.getNode().equals(node)) {
        System.out.println("Node " + node.getId() + " is present in closedList");
        return eNode;
      }
    }
    return null;
  }


}

