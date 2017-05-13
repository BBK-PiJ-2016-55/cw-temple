package student;

import game.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that calculates the most efficient route between two points using
 * a version of Dijkstra's algorithm.
 * @author svince04
 */
class RouteFinder {
  private List<EscapeNode> openList = new ArrayList<>();
  private Map<Node, EscapeNode> closedList = new HashMap<>();
  private EscapeNode current;

  RouteFinder(EscapeNode current) {
    this.current = current;
  }

  /**
  * @param target node we want to reach/find the best route to.
  * @return {@link EscapeNode} wrapping the target Node with a pointer that will enable
  *     us to construct a route between the target and the start position of our route.
  */
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

  /**
   * Checks the current position's neighbours to either add them to the list of tiles to
   * be considered while looking for a route, or if they've already been considered,
   * check that the stored cost isn't greater than the cost of the current route.
   * @param neighbours Set of {@link Node} objects representing the current position's
   *                   non-wall neighbours.
   */
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

  /**
   * Checks the list of {@link EscapeNode} objects to see if one of them
   * contains the node of interest.
   * @param node Node to be checked.
   * @return {@link EscapeNode} containing the node passed as parameter,
   *     or null if no matching EscapeNode is found.
   */
  private EscapeNode checkOpenList(Node node) {
    return openList.stream()
        .filter(escNode -> escNode.getNode().equals(node))
        .findAny()
        .orElse(null);
  }

  /**
   * Assesses the neighbouring tiles to see if their current cost (i.e.,
   * the cost of a previously-calculated route ending at the tile) is less than
   * the cost of the current route.
   * @param neighbour neighbouring {@link EscapeNode} to be re-evaluated.
   * @return true if current route + neighbour is quicker than one
   *     already found.
   */
  private boolean checkCost(EscapeNode neighbour) {
    return (neighbour.getCost() > (current.getCost() + current
        .getNode()
        .getEdge(neighbour.getNode())
        .length()));
  }
}