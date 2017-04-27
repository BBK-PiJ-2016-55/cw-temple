package student;

import game.*;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Explorer {
  // todo - do I even need CaveNodes? I sticking with current approach, simplify
  private Stack<CaveNode> currentRoute = new Stack<>();
  private Map<Long, CaveNode> caveMap = new ConcurrentHashMap<>();
  // Data structure to store unexplored neighbour nodes
  private Map<Node, EscapeNode> openNodes = new ConcurrentHashMap<>();
  // Data structure to store explored nodes
  private Map<Node, EscapeNode> closedNodes = new ConcurrentHashMap<>();
  private LinkedList<EscapeNode> queue = new LinkedList<>();
  private Set<Node> checked = new HashSet<>();

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
      // todo - get rid of the distance field in caveMap unless used later
      // Create new node with useful env info of current position
      CaveNode currentNode = new CaveNodeImpl(state.getCurrentLocation(),
              state.getDistanceToTarget());
      currentRoute.add(currentNode);
      caveMap.put(currentNode.getId(), currentNode);

      // Find unvisited neighbours and go to the one closest to the orb
      List<NodeStatus> tempNeighbours = newNeighbours(state.getNeighbours());
      if (!tempNeighbours.isEmpty()) {
        tempNeighbours.sort(Comparator.comparing(NodeStatus::getDistanceToTarget));
        state.moveTo(tempNeighbours.get(0).getId());

      // If you're at a dead end, go back...
      // todo - any better way of dealing with this? e.g., reverse when moving away from orb for some time...
      } else {
        currentRoute.pop();
        state.moveTo(currentRoute.pop().getId());
      }
    }
  }

  private List<NodeStatus> newNeighbours(Collection<NodeStatus> neighbours) {
    List<NodeStatus> tempNeighbours = new ArrayList<>();
    for (NodeStatus n : neighbours) {
      if ((!caveMap.containsKey(n.getId()))) {
        tempNeighbours.add(n);
      }
    }
    return tempNeighbours;
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

    // Get end node of best route
    EscapeNode out = getRoute(state);

    // Create stack to read route into
    Stack<EscapeNode> bestRouteStack = new Stack<>();

    // Start on the exit node
    EscapeNode current = out;

    while (current.getParent() != null) {
      System.out.println("line 123");
      bestRouteStack.push(current);
      current = current.getParent();
    }


    while (!bestRouteStack.isEmpty()) {
      state.moveTo(bestRouteStack.pop().getNode());
      System.out.println("line 133");

    }
    return;
  }


  public EscapeNode getRoute(EscapeState state) {
    System.out.println("line 139");

    // Get target node
    Node exit = state.getExit();

    // Wrap + store current node
    EscapeNode root = new EscapeNode(state.getCurrentNode(), null);

    openNodes.put(state.getCurrentNode(), root);
    queue.add(root);

    EscapeNode lastTail = null;

    boolean finished = false;

    // Go through each Node until we find the exit
    while (!finished) {

      // Pop the current node from head of queue
      EscapeNode current = queue.remove();

      // Get its neighbour Nodes
      Set<Node> neighbours = current.getNode().getNeighbours();
      Set<Node> newNeighbours = new HashSet<>();
      System.out.println("line 156");

      // Go through neighbours and filter out any unvisited ones
      for (Node n : neighbours) {
        if (!checked.contains(n)) {
          newNeighbours.add(n);
          System.out.println("line 160");
        }
      }

      for (Node nn : newNeighbours) {
        if (nn.equals(exit)) {
          lastTail = new EscapeNode(nn,current);
          finished = true;
        } else {
          EscapeNode en = new EscapeNode(nn, current);
          queue.add(en);
          checked.add(nn);
        }
      }
    }
    return lastTail;
  }
}

