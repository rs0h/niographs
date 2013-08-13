/*=============================================================================

    Copyright(©) 2013 Nikolay Ognyanov

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jgrapht.DirectedGraph;

/**
 * Find all simple cycles of a directed graph using the Tarjan's
 * algorithm.
 * <p/>
 * See:<br/>
 * R. Tarjan, Enumeration of the elementary circuits of 
 * a directed graph, SIAM J. Comput., 2 (1973), pp. 211-216.
 *
 * @author Nikolay Ognyanov
 *
 * @param <V> - the vertex type.
 * @param <E> - the edge type.
 */
public class TarjanSimpleCycles<V, E>
    implements DirectedSimpleCycles<V, E>
{
    private DirectedGraph<V, E> graph;

    private List<List<V>>       cycles;
    private Set<V>              marked;
    private Stack<V>            markedStack;
    private Stack<V>            pointStack;
    private Map<V, Integer>     vToI;
    private Map<V, Set<V>>      removed;

    /**
     * Create a simple cycle finder with an unspecified graph.
     */
    public TarjanSimpleCycles()
    {
    }

    /**
     * Create a simple cycle finder for the specified graph.
     * 
     * @param graph - the DirectedGraph in which to find cycles.
     * @throws IllegalArgumentException if the graph argument is
     *         <code>null</code>.
     */
    public TarjanSimpleCycles(DirectedGraph<V, E> graph)
    {
        if (graph == null) {
            throw new IllegalArgumentException("Null graph argument.");
        }
        this.graph = graph;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectedGraph<V, E> getGraph()
    {
        return graph;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGraph(DirectedGraph<V, E> graph)
    {
        if (graph == null) {
            throw new IllegalArgumentException("Null graph argument.");
        }
        this.graph = graph;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<List<V>> findSimpleCycles()
    {
        if (graph == null) {
            throw new IllegalArgumentException("Null graph.");
        }
        initState();

        for (V start : graph.vertexSet()) {
            backtrack(start, start);
            while (!markedStack.isEmpty()) {
                marked.remove(markedStack.pop());
            }
        }

        List<List<V>> result = cycles;
        clearState();
        return result;
    }

    private boolean backtrack(V start, V vertex)
    {
        boolean foundCycle = false;
        pointStack.push(vertex);
        marked.add(vertex);
        markedStack.push(vertex);

        for (E currentEdge : graph.outgoingEdgesOf(vertex)) {
            V currentVertex = graph.getEdgeTarget(currentEdge);
            if (getRemoved(vertex).contains(currentVertex)) {
                continue;
            }
            int comparison = toI(currentVertex).
                compareTo(toI(start));
            if (comparison < 0) {
                getRemoved(vertex).add(currentVertex);
            }
            else if (comparison == 0) {
                foundCycle = true;
                List<V> cycle = new ArrayList<V>();
                int cycleStart = pointStack.indexOf(start);
                int cycleEnd = pointStack.size() - 1;
                for (int i = cycleStart; i <= cycleEnd; i++) {
                    cycle.add(pointStack.get(i));
                }
                cycles.add(cycle);
            }
            else if (!marked.contains(currentVertex)) {
                boolean gotCycle =
                    backtrack(start, currentVertex);
                foundCycle = foundCycle || gotCycle;
            }
        }

        if (foundCycle) {
            while (!markedStack.peek().equals(vertex)) {
                marked.remove(markedStack.pop());
            }
            marked.remove(markedStack.pop());
        }

        pointStack.pop();
        return foundCycle;
    }

    private void initState()
    {
        cycles = new ArrayList<List<V>>();
        marked = new HashSet<V>();
        markedStack = new Stack<V>();
        pointStack = new Stack<V>();
        vToI = new HashMap<V, Integer>();
        removed = new HashMap<V, Set<V>>();
        int index = 0;
        for (V v : graph.vertexSet()) {
            vToI.put(v, index++);
        }
    }

    private void clearState()
    {
        cycles = null;
        marked = null;
        markedStack = null;
        pointStack = null;
        vToI = null;
    }

    private Integer toI(V v)
    {
        return vToI.get(v);
    }

    private Set<V> getRemoved(V v)
    {
        // Removed sets typically not all
        // needed, so instantiate lazily.
        Set<V> result = removed.get(v);
        if (result == null) {
            result = new HashSet<V>();
            removed.put(v, result);
        }
        return result;
    }
}
