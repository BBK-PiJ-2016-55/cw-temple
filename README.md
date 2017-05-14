# cw-temple
PiJ Coursework 4: Philip Hammond and the Temple of Gloom

Please note that Phillip Hammond has been renamed 'Sidney' for this project, as I didn't feel much enthusiasm for helping him escape the cave under the previous name.

### Explore

The Explore phase uses a depth-first search approach which prioritises more 'promising' moves when a choice is available. This is usually at a junction: given a choice of two or three unvisited tiles, Sidney will choose the one which has the closest proximity to the orb. When a dead-end is reached (whether due to a lack of open neighbouring tiles or a lack of unvisited neighbouring tiles), Sidney retraces his steps until he finds an open, unvisited neighbour - usually at the most recently-visited junction.

This works reasonably well, but in situations where two potential next moves are the same distance from the orb one is chosen at random. If this choice turns out to move Sidney further away from the orb, the search algorithm doesn't self-correct. Sometimes this means he wanders around the whole map before finding the exit, leading to a low bonus rating.

This could potentially be fixed by adding a record of the paths seen-but-not-taken. If this was used in conjunction with Sidney's awareness of his distance from the node, it could enable him to backtrack when he's moved too far away from the orb and try a more promising route. I had a go at this a couple of times, but ran out of time without getting it working.

### Escape

The Escape phase uses a version of Dijkstra's algorithm to get Sidney out of the cave on time with as much gold as his robbing little hands can carry. When an optimal route is found, the pointer of the target EscapeNode (node wrapper class detailed below) is pointed at the 'parent' EscapeNode preceding it, which in turn has a pointer to its parent and so on. This way, by returning a single EscapeNode we can rebuild and traverse a path from the end destination back to Sidney's current location.

At the start of this phase, a list of all the available gold in the cavern is generated and the RouteFinder class (using Dijkstra's algorithm) is used to plot the least expensive/time-consuming route to each. The 'best' choice out of all these route/node combinations is decided by:

a) removing any routes that wouldn't leave Sidney enough time to reach the exit in the time remaining.

b) sorting the list so that nodes which have the greatest payoff for the least cost will be selected.

The sorting takes into account the cost of the route as well as the total gold available to be picked up enroute to the target node. I tried a few different sorting criteria: proximity to the current position, richness of the target node (excluding route gold) and richness of the target node vs. effort to reach it, but while testing found the current approach to give a higher average score, while still ensuring that Sidney makes it to the exit every time.

### Additional classes 

##### EscapeNode 
This is a wrapper class for a Node object. A wrapper was needed because I wanted to be able to store information about the route leading to this class: total cost, total gold, etc. 

##### Routefinder 
This is where I've tried to implement a version of Dijkstra's algorithm to find the best route between two points. The constructor takes the route's start position as a parameter and the getRoute() method accepts the desired target, so this approach requires a new RouteFinder object for each starting point.

To generate the route, each potential next-step EscapeNode is visited and each neighbouring node is checked to see if:

a) they're the target (at which point the evaluation is terminated and the current node is set as the target's parent) or

b) a node we've seen before. If a neighbour has been seen before, it is re-evaluated and if the cost to reach it via the current node is less than the route cost currently stored, the neighbour's parent is set to the current node and route cost/route gold info is updated accordingly.

#### Other notes + improvements
As already mentioned, the Explore phase could be improved to stop Sidney from haring off in the wrong direction half the time. If I was to implement this again, I might not use a pointer/parent relationship to define the route - the traverseRoute() method seems a bit inefficient and there's probably a simpler way of achieving the same result.

I'd also write some tests to make sure that my code kept passing edge case maps (all the gold can be collected, maps where Sidney is initially stood on top of the only reachable gold, etc.).
