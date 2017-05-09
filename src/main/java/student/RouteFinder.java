package student;

import game.Edge;
import game.Node;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by svince04 on 07/05/2017 for cw-temple.
 */
public class RouteFinder {
  private List<EscapeNode> openList = new ArrayList<>();
  private Map<Node, EscapeNode> closedList = new HashMap<>();

  EscapeNode getRoute(EscapeNode current, Node target) {
    closedList.clear();
    openList.clear();

    // Add start location to openList for checking
    openList.add(current);

    while (current.getNode() != target) {
      // Get most promising EscapeNode + move from openList to closedList
      openList.sort(Comparator.comparing(EscapeNode::getCost));
      current = openList.remove(0);
      closedList.put(current.getNode(), current);

      // Get neighbour Nodes + evaluate each one
      Set<Node> neighbours = current.getNode().getNeighbours();
      evaluateNeighbours(current, neighbours);

    }
    return current;
  }

  private void evaluateNeighbours(EscapeNode current, Set<Node> neighbours) {
    for (Node n : neighbours) {
      // If the new route is quicker than previous, update + move to openList if required
      if (closedList.containsKey(n)) {
        if (checkCost(closedList.get(n), current)) {
          openList.add(closedList.remove(n));
        }
      } else if (checkOpenList(n) != null) {
        checkCost(checkOpenList(n), current);
      } else {
        // Add totally new neighbours to openList
        EscapeNode newNode = new EscapeNode(n, current);
        openList.add(newNode);
      }
    }
  }

  private EscapeNode checkOpenList(Node node) {
    return openList.stream()
        .filter(escNode -> escNode.getNode().equals(node))
        .findAny()
        .orElse(null);
  }

  // Checks and updates node cost if quicker than already found
  private boolean checkCost(EscapeNode child, EscapeNode current) {

    // Get edge connecting current to neighbour being re-analysed
    Edge edge = current.getNode().getEdge(child.getNode());

    boolean quicker = false;

    // Check if current distance + calculate distance is greater than child's distance
    if (child.getCost() > (current.getCost() + edge.length())) {
      child.setParent(current);
      quicker = true;
    }

    return quicker;
  }
}
