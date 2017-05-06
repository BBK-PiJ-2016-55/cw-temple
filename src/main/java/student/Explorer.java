package student;

import game.*;

import java.util.*;

public class Explorer {
  private Stack<Long> currentRoute = new Stack<>();
  private Set<Long> visitedNodes = new HashSet<>();
  private EscapeNode start;
  private List<Node> goldQueue = new ArrayList<>();
  private EscapeNode exitNode;
  private Map<Node, EscapeNode> allNodesMap = new HashMap<>();

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

    // Create list of the richest nodes
    createGoldQueue(state);

//    // If Sid spawns on a gold tile, pick it up before doing anything else
//    if (start.getNode().getTile().getGold() != 0) {
//      state.pickUpGold();
//      goldQueue.remove(start.getNode());
//    }

    // Create escapeGraph using current location
    createEscapeGraph(state);

    // While there's still something in goldQueue, see if you can get to the closest rich node
    while (!goldQueue.isEmpty()) {

      // retrieve the richest EscapeNode from the graph
      EscapeNode rich = allNodesMap.get(goldQueue.get(0));

      // Calculate how many steps to gold + exit
      int costOfRoute = rich.getCost() + (getRoute(rich, state.getExit().getId()).getCost()) ;

      // Check the richest node + exit are reachable
      if (costOfRoute > state.getTimeRemaining()) {
        // If not, remove richest node from the list and go round the while loop again
        goldQueue.remove(0);
      } else {
        // If it is, visit node, create a new gold queue and then go round the loop again
        traverseRoute(state, rich);
        // Refresh escape graph using new location
        createEscapeGraph(state);
        // Refresh gold queue
        createGoldQueue(state);
      }
    }
    createEscapeGraph(state);
    traverseRoute(state, exitNode);

  }

  private void createGoldQueue(EscapeState state) {

    // If Sid spawns on a gold tile, pick it up before doing anything else
    if (state.getCurrentNode().getTile().getGold() != 0) {
      state.pickUpGold();
    }

    goldQueue.clear();

    Collection<Node> allNodes = state.getVertices();
    for (Node n : allNodes) {
      if (n.getTile().getGold() != 0) {
        goldQueue.add(n);
      }
    }

    // Sorts according to gold content (ascending, hopefully...)
    Comparator<Node> goldNodeComparator = Comparator.comparing(node -> node.getTile().getGold());
    Collections.sort(goldQueue, goldNodeComparator.reversed());
  }


  private void traverseRoute(EscapeState state, EscapeNode current) {
    // Create stack to read route into
    Stack<EscapeNode> bestRouteStack = new Stack<>();

    // Work backwards from exit, adding each parent to route stack
    while (current.getParent() != null) {
      bestRouteStack.push(current);
      current = current.getParent();
    }

    // Traverse route
    while (!bestRouteStack.isEmpty()) {

      EscapeNode currentStep = bestRouteStack.pop();
      state.moveTo(currentStep.getNode());

      if (state.getCurrentNode().getTile().getGold() != 0) {
        state.pickUpGold();
      }
    }
  }

  // Converts each node into an EscapeNode object, so we know the quickest route to each one
  private void createEscapeGraph(EscapeState state) {

    // Gives us most nodes of interest
    createGoldQueue(state);

    // Clear allNodesMap
    allNodesMap.clear();

    // Create node for current position
    start = new EscapeNode(state.getCurrentNode(), null);

    // Get all Nodes
    Collection<Node> allNodes = state.getVertices();

    // Find best route for each node from current position
    for (Node n : allNodes) {
      EscapeNode temp = getRoute(start, n.getId());
      allNodesMap.put(n, temp);
    }

    // Update exit node variable
    exitNode = allNodesMap.get(state.getExit());
  }


  private EscapeNode getRoute(EscapeNode start, Long dest) {
    Set<Node> checked = new HashSet<>();
    List<EscapeNode> queue = new ArrayList<>();


    // Add start node to queue
    queue.add(start);

    // Custom comparator to compare on cost and gold
    Comparator<EscapeNode> comp = (en1, en2) -> {
      int result = Integer.compare(en1.getCost(), en2.getCost());
      if (result == 0) {
        result = ((Integer.compare(en1.getGold(), en2.getGold()) > 0) ? en1.getGold() : en2.getGold());
      }
      return result;
    };

    // Go through each Node until we find the exit
    while (true) {

      // Sort based on edge weight (asc) and gold (desc)
      queue.sort(comp);

      // Pop the lowest-weighted node from queue
      EscapeNode current = queue.remove(0);

      checked.add(current.getNode());

      // Get current's neighbour Nodes
      Set<Node> neighbours = current.getNode().getNeighbours();

      // todo - this is fucking horrible, try and make it less fucking horrible
      for (Node n : neighbours) {
        // Return if we find the destination we're looking for
        if (n.getId() == dest) {
          queue.clear();
          return new EscapeNode(n, current);
          // Todo: what does checked do again? I can't remember but taking out breaks Sid!
        } else if (checked.contains(n)) {
          continue;
          // If we've already see this neighbour, recalculate cost and change parent if needed
        } else if (queue.contains(allNodesMap.get(n))) {
          checkCost(allNodesMap.get(n), current);
        } else {
          // Add totally new neighbours to queue
          queue.add(new EscapeNode(n, current));
        }
      }
    }
  }

  // todo - will there ever be a downstream impact? ie., will child node ever be a parent
  private void checkCost(EscapeNode child, EscapeNode current) {

    System.out.println("CheckCost() is working...");
    // Get edge connecting current to neighbour being re-analysed
    Edge edge = current.getNode().getEdge(child.getNode());

    // Check if current distance + calculate distance is greater than child's distance
    if (child.getCost() > (current.getCost() + edge.length())) {
      child.setCost(current.getCost() + edge.length());
      child.setParent(current);
    }
  }
}

