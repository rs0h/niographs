/*=============================================================================

Copyright(©) 2012 Nikolay Ognyanov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

=============================================================================*/

package net.ognyanov.niographs;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

/**
 * A class implementing a graph as a map from vertices to adjacency lists.<p>
 * <b>Implementation Notes:</b><br>
 * <ol>
 * <li>
 * The primary design goal of this implementation is convenience of use
 * rather than maximum performance (which can be achieved with vertices 
 * of type <code>int</code> and use of arrays instead of lists, sets and 
 * maps).
 * </li>
 * <li>
 * All vertices must be unique in terms of their method <code>equals()</code>.
 * </li>
 * <li>
 * <code>null</code> values of adjacency lists are not allowed.
 * Empty list values must be used instead.
 * </li>
 * <li>
 * <code>null</code> values in adjacency lists are not allowed.
 * </li>
 * <li>
 * The target vertex of an  adjacency pair must be present in the graph.
 * </li>
 * <li>
 * If the graph is undirected and contains an adjacency pair (x,y) and 
 * <code>!x.equals(y)</code> is <code>true</code> then the graph must 
 * also contain the reverse pair (y,x).
 * </li>
 * <li>
 * This implementation does not fully enforce or/and automatically 
 * support any of the requirements mentioned above (as it could and p
 * ossibly should).
 * </li>
 * </ol>
 * 
 * @param <V> The vertex type.
 */
public final class Graph<V>
    extends LinkedHashMap<V, AdjacencyList<V>> {
    private static final long serialVersionUID     = 2984563501113161033L;
    static final String       IMPLEMENTATION_ERROR = "Implementation error!";
    static final String       NULL_ARGUMENT        = "Null argument.";

    static final class StackEntry<V> {
        V           vertex;
        Iterator<V> successors;

        StackEntry(V vertex, Iterator<V> successors) {
            this.vertex = vertex;
            this.successors = successors;
        }
    }

    private Set<V>               verticesAlreadyVisited = new HashSet<V>();
    private Stack<StackEntry<V>> stack                  =
                                                            new Stack<StackEntry<V>>();
    private Queue<V>             queue                  = new LinkedList<V>();

    /**
     * Visit depth-first the argument vertex and all vertices reachable from it.
     * Vertices are post-visited after all their successors have been post-visited.<br>
     * Modification of graph structure during the visit is not allowed.<br>
     * Successors of every vertex are traversed in the order in which they were 
     * added to the adjacency set.
     * 
     * @param visitor The visitor to be used.
     * @param vertex The start vertex of the traversal.
     */
    public void visitDepthFirst(GraphVisitor<V> visitor, V vertex) {
        if (visitor == null || vertex == null) {
            throw new IllegalArgumentException(NULL_ARGUMENT);
        }
        try {
            visitor.startVisit();
            doVisitDepthFirst(visitor, vertex);
            visitor.endVisit();
        }
        finally {
            // Do this in a finally clause because
            // the visitor may throw an exception.
            verticesAlreadyVisited.clear();
            stack.clear();
        }
    }

    /**
     * Visit depth-first all vertices in the graph.<br>
     * Modification of graph structure during the visit is not allowed.
     * Vertices are post-visited after all their successors have been post-visited.<br>
     * Vertices are traversed in the order in which they were added to the graph.<br>
     * Successor of every vertex are traversed in the order in which they were 
     * added to the adjacency set.
     * 
     * @param visitor The visitor to be used.
     */
    public void visitDepthFirst(GraphVisitor<V> visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException(NULL_ARGUMENT);
        }
        try {
            visitor.startVisit();
            for (Map.Entry<V, AdjacencyList<V>> e : entrySet()) {
                V vertex = e.getKey();
                if (verticesAlreadyVisited.contains(vertex)) {
                    continue;
                }
                if (doVisitDepthFirst(visitor, vertex)) {
                    // the visitor is done
                    break;
                }
            }
            visitor.endVisit();
        }
        finally {
            // Do this in a finally clause because
            // the visitor may throw an exception.
            verticesAlreadyVisited.clear();
            stack.clear();
        }

    }

    private boolean doVisitDepthFirst(GraphVisitor<V> visitor, V vertex) {
        boolean visitorIsDone = false;
        visitor.preVisit(vertex);
        visitorIsDone = visitor.isDone();
        if (!visitorIsDone) {
            Set<V> successors = get(vertex);
            Iterator<V> successorsIterator = successors.iterator();
            stack.push(new StackEntry<V>(vertex, successorsIterator));
            verticesAlreadyVisited.add(vertex);
            do {
                vertex = stack.peek().vertex;
                successorsIterator = stack.peek().successors;
                V nextVertex = null;
                while (successorsIterator.hasNext()) {
                    V v = successorsIterator.next();
                    if (!verticesAlreadyVisited.contains(v)) {
                        nextVertex = v;
                        break;
                    }
                }
                if (nextVertex != null) {
                    successors = get(nextVertex);
                    successorsIterator = successors.iterator();
                    visitor.preVisit(nextVertex);
                    visitorIsDone = visitor.isDone();
                    if (visitorIsDone) {
                        break;
                    }
                    stack
                        .push(new StackEntry<V>(nextVertex, successorsIterator));
                    verticesAlreadyVisited.add(nextVertex);
                }
                else {
                    stack.pop();
                    visitor.postVisit(vertex);
                    visitorIsDone = visitor.isDone();
                    if (visitorIsDone) {
                        break;
                    }
                }
            } while (!stack.isEmpty());
        }
        return visitorIsDone;
    }

    /**
     * Visit breadth-first the argument vertex and all vertices reachable from it.
     * Vertices are post-visited immediately after their direct successors have 
     * been pre-visited<br>
     * Modification of graph structure during the visit is not allowed.<br>
     * Successors of every vertex are traversed in the order in which they were 
     * added to the adjacency set.
     * 
     * @param visitor The visitor to be used.
     * @param vertex The start vertex of the traversal.
     */
    public void visitBreadthFirst(GraphVisitor<V> visitor, V vertex) {
        if (visitor == null || vertex == null) {
            throw new IllegalArgumentException(NULL_ARGUMENT);
        }
        try {
            visitor.startVisit();
            visitor.preVisit(vertex);
            if (!visitor.isDone()) {
                verticesAlreadyVisited.add(vertex);
                queue.add(vertex);
                doVisitBreadthFirst(visitor);
            }
            visitor.endVisit();
        }
        finally {
            // Do this in a finally clause because
            // the visitor may throw an exception.
            verticesAlreadyVisited.clear();
            queue.clear();
        }
    }

    /**
     * Visit breadth-first all vertices in the graph. Vertices are post-visited 
     * immediately after their direct successors have been pre-visited<br>
     * Modification of graph structure during the visit is not allowed.<br>
     * Vertices are traversed in the order in which they were added to the graph.<br>
     * Successor of every vertex are traversed in the order in which they were 
     * added to the adjacency set.
     * 
     * @param visitor The visitor to be used.
     */
    public void visitBreadthFirst(GraphVisitor<V> visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException(NULL_ARGUMENT);
        }
        try {
            visitor.startVisit();
            for (V vertex : keySet()) {
                if (verticesAlreadyVisited.contains(vertex)) {
                    continue;
                }
                visitor.preVisit(vertex);
                if (visitor.isDone()) {
                    break;
                }
                verticesAlreadyVisited.add(vertex);
                queue.add(vertex);
                if (doVisitBreadthFirst(visitor)) {
                    // the visitor is done
                    break;
                }
            }
            visitor.endVisit();
        }
        finally {
            // Do this in a finally clause because
            // the visitor may throw an exception.
            verticesAlreadyVisited.clear();
            queue.clear();
        }
    }

    private boolean doVisitBreadthFirst(GraphVisitor<V> visitor) {
        boolean visitorIsDone = false;
        do {
            V vertex = queue.remove();
            Iterator<V> successorsIterator = get(vertex).iterator();
            while (successorsIterator.hasNext()) {
                V successor = successorsIterator.next();
                if (verticesAlreadyVisited.contains(successor)) {
                    continue;
                }
                else {
                    visitor.preVisit(successor);
                    if (visitor.isDone()) {
                        visitorIsDone = true;
                        break;
                    }
                    verticesAlreadyVisited.add(successor);
                    queue.add(successor);
                }
            }
            visitor.postVisit(vertex);
            if (visitor.isDone()) {
                visitorIsDone = true;
                break;
            }
        } while (!queue.isEmpty());
        return visitorIsDone;
    }
}
