package student;

import game.*;
import game.Tile;

import java.util.*;

public class Explorer {
  private Stack<Long> visitedTiles = new Stack<>();
  // todo - what's the best collection for neighbourTiles?
  private List<NodeStatus> neighbourTiles = new ArrayList<>();
  private List<Long> unvisitedTiles = new ArrayList<>();

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
        // get current location and add ID to visited tile stack
        visitedTiles.add(state.getCurrentLocation());
        List<NodeStatus> tempNeighbour = new ArrayList<>();
        // remove from unvisited if it's there...
        if (unvisitedTiles.contains(visitedTiles.peek())) {
            unvisitedTiles.remove(visitedTiles.peek());
            System.out.println("Current tile removed from unvisited");
        }

        neighbourTiles = (List<NodeStatus>) state.getNeighbours();
        System.out.println("Size of neighbourTiles = " + neighbourTiles.size());
        // get list of unvisited, (non-wall?) neighbours and add to temp data structure
        for (NodeStatus n : neighbourTiles) {
            if ((!visitedTiles.contains(n.getId()) && (!unvisitedTiles.contains(n.getId())))) {
                unvisitedTiles.add(n.getId());
                tempNeighbour.add(n);
                System.out.println("Adding node ID " + n.getId() + " to unvisited tiles");
            }
        }

        // find the neighbour with the lowest distance to the orb
        if (!tempNeighbour.isEmpty()) {
            tempNeighbour.sort(Comparator.comparing(node -> node.getDistanceToTarget()));
            System.out.println("Moving to: " + tempNeighbour.get(0).getId());
            state.moveTo(tempNeighbour.get(0).getId());
            neighbourTiles.clear();
        } else {
            state.moveTo(visitedTiles.peek());
            neighbourTiles.clear();
        }
    }
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
    //TODO: Escape from the cavern before time runs out
  }
}
