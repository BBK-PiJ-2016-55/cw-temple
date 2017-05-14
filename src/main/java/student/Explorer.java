package student;

import game.EscapeState;
import game.ExplorationState;
import game.NodeStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class Explorer {
  private Stack<Long> currentRoute = new Stack<>();
  private Set<Long> visitedNodes = new HashSet<>();
  private List<EscapeNode> goldQueue = new ArrayList<>();
  private EscapeState state;
  private EscapeNode current;

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

  /**
   * Evaluates all neighbours of the current node and disregards any nodes that have already been
   * visited before returning the one closest to the orb.
   * @param neighbours set of {@link NodeStatus} objects adjacent to current position.
   * @return long ID of the neighbour node closest to the orb.
   */
  private Long findNewNeighbours(Collection<NodeStatus> neighbours) {
    List<NodeStatus> tempNeighbours = neighbours.stream()
        .filter(nodeStatus -> !(visitedNodes.contains(nodeStatus.getId())))
        .sorted(NodeStatus::compareTo)
        .collect(Collectors.toList());

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
    createGoldQueue();
    pickUpGold();

    // Calculate a route to the most profitable node reachable in the time remaining
    while (!goldQueue.isEmpty()) {
      RouteFinder routeFinder = new RouteFinder(current);
      EscapeNode target = routeFinder.getRoute(goldQueue.get(0).getNode());
      if ((target.getCost() + routeFinder.getRoute(state.getExit())
          .getCost()) > state.getTimeRemaining()) {
        goldQueue.remove(0);
      } else {
        traverseRoute(target);
        createGoldQueue();
      }
    }
    // Go to the exit
    traverseRoute(new RouteFinder(current).getRoute(state.getExit()));
  }

  /**
   * Checks to see if the gold value of the current tile is > 0 and picks it up if so.
   */
  private void pickUpGold() {
    if (state.getCurrentNode().getTile().getGold() != 0) {
      state.pickUpGold();
    }
  }

  /**
   * Finds all the gold left on the map and builds a list of
   * {@link EscapeNode} objects sorted in ascending order according to
   * their route's cost-to-gold value.
   */
  private void createGoldQueue() {
    current = new EscapeNode(state.getCurrentNode(), null);
    goldQueue = state.getVertices().stream()
        .filter(n -> n.getTile().getGold() != 0)
        .map(n -> new RouteFinder(current).getRoute(n))
        .sorted(Comparator.comparing(EscapeNode::getCumulativeGoldPerStep).reversed())
        .collect(Collectors.toList());
  }

  /**
   * Moves the avatar to a new destination, picking up any gold found
   * along the way.
   * @param target {@link EscapeNode} we want to move to.
   */
  private void traverseRoute(EscapeNode target) {
    Stack<EscapeNode> bestRouteStack = new Stack<>();
    EscapeNode nextStep = target;
    // Work backwards from target, adding each parent to route stack
    while (!nextStep.equals(current)) {
      bestRouteStack.push(nextStep);
      nextStep = nextStep.getParent();
    }

    // Traverse route
    while (!bestRouteStack.isEmpty()) {
      // todo - do I need the first one of these? Find an example where it matters, delete if not
      pickUpGold();
      state.moveTo(bestRouteStack.pop().getNode());
      pickUpGold();
    }
  }
}