package student;

import game.Edge;
import game.Node;

import java.util.*;

/**
 * Created by svince04 on 07/05/2017 for cw-temple.
 */
public class RouteFinder {

  EscapeNode getRoute(EscapeNode start, Node target) {
    Map<Node, EscapeNode> closedList = new HashMap<>();
    List<EscapeNode> openList = new ArrayList<>();

    // Add start location to openList for checking
    EscapeNode current = start;
    openList.add(start);

    // Go through each Node until we find the target
    while (current.getNode() != target) {

      // Get most promising EscapeNode
      openList.sort(Comparator.comparing(EscapeNode::getCost));

      // Move the current node from openList to closedList
      current = openList.remove(0);
      closedList.put(current.getNode(), current);

      // Get current's neighbour Nodes
      Set<Node> neighbours = current.getNode().getNeighbours();

      // Evaluate each neighbour
      for (Node n : neighbours) {
        // If already processed, check the new route isn't quicker
        if (closedList.containsKey(n)) {
          // Update and move to openList if so
          if (checkCost(closedList.get(n), current)) {
            openList.add(closedList.get(n));
            closedList.remove(n);
          }
        } else if (checkOpenList(openList, n) != null) {
          // If already in openList, check the new route isn't quicker
          EscapeNode temp = checkOpenList(openList, n);
          // Update and replace in openList if so
          if (checkCost(temp, current)) {
            openList.remove(temp);
            openList.add(temp);
          }
        } else {
          // Add totally new neighbours to openList
          EscapeNode newNode = new EscapeNode(n, current);
          openList.add(newNode);
        }
      }
    }
    return current;
  }

  private EscapeNode checkOpenList(List<EscapeNode> openList, Node node) {
    for (EscapeNode en : openList) {
      if (en.getNode().equals(node)) {
        return en;
      }
    }
    return null;
  }

  // Checks and updates node cost if quicker than already found
  private boolean checkCost(EscapeNode child, EscapeNode current) {
    boolean quicker = false;

    // Get edge connecting current to neighbour being re-analysed
    Edge edge = current.getNode().getEdge(child.getNode());

    // Check if current distance + calculate distance is greater than child's distance
    if (child.getCost() > (current.getCost() + edge.length())) {
      child.setParent(current);
      quicker = true;
    }

    return quicker;
  }
}
