package student;

import game.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by svince04 on 07/05/2017 for cw-temple.
 */
class RouteFinder {
  private List<EscapeNode> openList = new ArrayList<>();
  private Map<Node, EscapeNode> closedList = new HashMap<>();
  private EscapeNode current;

  RouteFinder(EscapeNode current) {
    this.current = current;
  }

  EscapeNode getRoute(Node target) {
    closedList.clear();
    openList.clear();
    openList.add(current);

    while (current.getNode() != target) {
      // Get closest EscapeNode + move from openList to closedList
      openList.sort(Comparator.comparing(EscapeNode::getCost));
      current = openList.remove(0);
      closedList.put(current.getNode(), current);
      // Get neighbour Nodes + evaluate
      evaluateNeighbours(current.getNode().getNeighbours());
    }
    return current;
  }

  private void evaluateNeighbours(Set<Node> neighbours) {
    for (Node n : neighbours) {
      // If the new route is quicker than previous, update + move to openList if required
      if (closedList.containsKey(n)) {
        if (checkCost(closedList.get(n))) {
          closedList.get(n).setParent(current);
          openList.add(closedList.remove(n));
        }
      } else if (checkOpenList(n) != null) {
        checkCost(checkOpenList(n));
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
  private boolean checkCost(EscapeNode child) {
    return (child.getCost() > (current.getCost() + current
        .getNode()
        .getEdge(child.getNode())
        .length()));
  }
}