package student;

import game.*;

import java.util.*;

public class Explorer {
  private Stack<Long> currentRoute = new Stack<>();
  private Set<Long> visitedNodes = new HashSet<>();
  private List<EscapeNode> goldQueue = new ArrayList<>();
  private EscapeState state;
  private EscapeNode current;
  private RouteFinder routeFinder;

  /**
   * Explore the cavern, trying to find the orb in as few steps as possible.
   * Once you find the orb, you must return from the function in order to pick
   * it up. If you continue to move after finding the orb rather
   * than returning, it will not count.
   * If you return from this function while not standing on top of the orb,
   * it will count as a failure.
   *
   * <p>There is no limit to how many steps you can take, but you will receive
   * a score bonus multiplier for finding the orb in fewer steps.</p>
   *
   * <p>At every step, you only know your current tile's ID and the ID of all
   * open neighbor tiles, as well as the distance to the orb at each of these tiles
   * (ignoring walls and obstacles).</p>
   *
   * <p>To get information about the current state, use functions
   * getCurrentLocation(),
   * getNeighbours(), and
   * getDistanceToTarget()
   * in ExplorationState.
   * You know you are standing on the orb when getDistanceToTarget() is 0.</p>
   *
   * <p>Use function moveTo(long id) in ExplorationState to move to a neighboring
   * tile by its ID. Doing this will change state to reflect your new position.</p>
   *
   * <p>A suggested first implementation that will always find the orb, but likely won't
   * receive a large bonus multiplier, is a depth-first search.</p>
   *
   * @param state the information available at the current state
   */
  public void explore(ExplorationState state) {
    while (state.getDistanceToTarget() != 0) {

      // Add current position to visited list, if new.
      if (!visitedNodes.contains(state.getCurrentLocation())) {
        visitedNodes.add(state.getCurrentLocation());
      }

      // Move to closest, unvisited neighbour.
      Long closestNeighbour = findNewNeighbours(state.getNeighbours());
      if (closestNeighbour != null) {
        state.moveTo(closestNeighbour);
        currentRoute.push(state.getCurrentLocation());
        continue;
      }

      // If there are no unvisited neighbours (i.e., dead end), move back one step.
      currentRoute.pop();
      state.moveTo(currentRoute.peek());
    }
  }

  private Long findNewNeighbours(Collection<NodeStatus> neighbours) {
    List<NodeStatus> tempNeighbours = new ArrayList<>();

    // Filter out any previously-visited neighbours.
    for (NodeStatus n : neighbours) {
      if (!visitedNodes.contains(n.getId())) {
        tempNeighbours.add(n);
      }
    }

    // Sort according to distance from orb, then return ID/null if in a dead end.
    tempNeighbours.sort(Comparator.comparing(NodeStatus::getDistanceToTarget));
    return (tempNeighbours.isEmpty() ? null : tempNeighbours.get(0).getId());
  }

  /**
   * Escape from the cavern before the ceiling collapses, trying to collect as much
   * gold as possible along the way. Your solution must ALWAYS escape before time runs
   * out, and this should be prioritized above collecting gold.
   *
   * <p>You now have access to the entire underlying graph, which can be accessed
   * through EscapeState.
   * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
   * will return a collection of all nodes on the graph.</p>
   *
   * <p>Note that time is measured entirely in the number of steps taken, and for each step
   * the time remaining is decremented by the weight of the edge taken. You can use
   * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
   * on your current tile (this will fail if no such gold exists), and moveTo() to move
   * to a destination node adjacent to your current node.</p>
   *
   * <p>You must return from this function while standing at the exit. Failing to do so before time
   * runs out or returning from the wrong location will be considered a failed run.</p>
   *
   * <p>You will always have enough time to escape using the shortest path from the starting
   * position to the exit, although this will not collect much gold.</p>
   *
   * @param state the information available at the current state
   */
  public void escape(EscapeState state) {
    this.state = state;
    this.routeFinder = new RouteFinder();

    // Create list of the richest nodes
    createGoldQueue();

    // While there's still something in goldQueue, see if you can get to the closest rich node
    while (!goldQueue.isEmpty()) {

      setCurrent();

      // Retrieve the nearest EscapeNode + plot route
      EscapeNode target = routeFinder.getRoute(current, goldQueue.get(0).getNode());
      EscapeNode exitRoute = routeFinder.getRoute(
          new EscapeNode(goldQueue.get(0).getNode(), null), state.getExit());

      // Check the total journey is possible in time remaining
      if ((target.getCost() + exitRoute.getCost()) > state.getTimeRemaining()) {
        goldQueue.remove(0);
      } else {
        traverseRoute(target);
        createGoldQueue();
      }
    }

    // Once we run out of reachable gold nodes, head for the exit
    current = setCurrent();
    traverseRoute(routeFinder.getRoute(current, state.getExit()));
  }

  private EscapeNode setCurrent() {
    return (new EscapeNode(state.getCurrentNode(), null));
  }

  private void pickUpGold() {
    if (state.getCurrentNode().getTile().getGold() != 0) {
      state.pickUpGold();
    }
  }

  private void createGoldQueue() {
    current = setCurrent();
    pickUpGold();
    goldQueue.clear();

    Collection<Node> allNodes = state.getVertices();
    for (Node n : allNodes) {
      if (n.getTile().getGold() != 0) {
        EscapeNode tempNode = routeFinder.getRoute(current, n);
        goldQueue.add(tempNode);
      }
    }
    goldQueue.sort(Comparator.comparing(EscapeNode::getGoldPerStep).reversed());
  }

  private void traverseRoute(EscapeNode target) {
    Stack<EscapeNode> bestRouteStack = new Stack<>();

    // Work backwards from target, adding each parent to route stack
    while (!target.getNode().equals(current.getNode())) {
      bestRouteStack.push(target);
      target = target.getParent();
    }

    // Traverse route
    while (!bestRouteStack.isEmpty()) {
      pickUpGold();
      EscapeNode currentStep = bestRouteStack.pop();
      state.moveTo(currentStep.getNode());
    }
  }


}

