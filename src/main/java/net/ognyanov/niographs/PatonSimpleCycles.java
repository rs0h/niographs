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

import org.jgrapht.UndirectedGraph;

/**
 * Find all simple cycles of an undirected graph using the Paton's
 * algorithm.
 * <p/>
 * See:<br/>
 * K. Paton, An algorithm for finding a fundamental set of cycles
 * for an undirected linear graph, Comm. ACM 12 (1969), pp. 514-518.
 * 
 * @author Nikolay Ognyanov
 *
 * @param <V> - the vertex type.
 * @param <E> - the edge type.
 */
public class PatonSimpleCycles<V, E>
    implements UndirectedSimpleCycles<V, E>
{
    private UndirectedGraph<V, E> graph;

    /**
     * Create a simple cycle finder with an unspecified graph.
     */
    public PatonSimpleCycles()
    {
    }

    /**
     * Create a simple cycle finder for the specified graph.
     * 
     * @param graph - the DirectedGraph in which to find cycles.
     * @throws IllegalArgumentException if the graph argument is
     *         <code>null</code>.
     */
    public PatonSimpleCycles(UndirectedGraph<V, E> graph)
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
    public UndirectedGraph<V, E> getGraph()
    {
        return graph;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGraph(UndirectedGraph<V, E> graph)
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
        Map<V, Set<V>> used = new HashMap<V, Set<V>>();
        Map<V, V> parent = new HashMap<V, V>();
        Stack<V> stack = new Stack<V>();
        List<List<V>> cycles = new ArrayList<List<V>>();

        for (V root : graph.vertexSet()) {
            // Loop over the connected
            // components of the graph.
            if (parent.containsKey(root)) {
                continue;
            }
            // Free some memory in case of
            // multiple connected components.
            used.clear();
            // Prepare to walk the spanning tree.
            parent.put(root, root);
            used.put(root, new HashSet<V>());
            stack.push(root);
            // Do the walk. It is a BFS with
            // a FIFO instead of the usual
            // LIFO. Thus it is easier to 
            // find the cycles in the tree.
            while (!stack.isEmpty()) {
                V current = stack.pop();
                Set<V> currentUsed = used.get(current);
                for (E e : graph.edgesOf(current)) {
                    V neighbour = graph.getEdgeTarget(e);
                    if (neighbour.equals(current)) {
                        neighbour = graph.getEdgeSource(e);
                    }
                    if (!used.containsKey(neighbour)) {
                        // found a new node
                        parent.put(neighbour, current);
                        Set<V> neighbourUsed = new HashSet<V>();
                        neighbourUsed.add(current);
                        used.put(neighbour, neighbourUsed);
                        stack.push(neighbour);
                    }
                    else if (neighbour.equals(current)) {
                        // found a self loop
                        List<V> cycle = new ArrayList<V>();
                        cycle.add(current);
                        cycles.add(cycle);
                    }
                    else if (!currentUsed.contains(neighbour)) {
                        // found a cycle
                        Set<V> neighbourUsed = used.get(neighbour);
                        List<V> cycle = new ArrayList<V>();
                        cycle.add(neighbour);
                        cycle.add(current);
                        V p = parent.get(current);
                        while (!neighbourUsed.contains(p)) {
                            cycle.add(p);
                            p = parent.get(p);
                        }
                        cycle.add(p);
                        cycles.add(cycle);
                        neighbourUsed.add(current);
                    }
                }
            }
        }
        return cycles;
    }
}
