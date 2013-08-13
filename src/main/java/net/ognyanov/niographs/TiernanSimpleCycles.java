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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;

/**
 * Find all simple cycles of a directed graph using the Tiernan's
 * algorithm.
 * <p/>
 * See:<br/>
 * J.C.Tiernan An Efficient Search Algorithm Find the Elementary
 * Circuits of a Graph., Communications of the ACM, vol.13, 12,
 * (1970), pp. 722 - 726.

 * @author Nikolay Ognyanov
 *
 * @param <V> - the vertex type.
 * @param <E> - the edge type.
 */
public class TiernanSimpleCycles<V, E>
    implements DirectedSimpleCycles<V, E>
{
    private DirectedGraph<V, E> graph;

    /**
     * Create a simple cycle finder with an unspecified graph.
     */
    public TiernanSimpleCycles()
    {
    }

    /**
     * Create a simple cycle finder for the specified graph.
     * 
     * @param graph - the DirectedGraph in which to find cycles.
     * @throws IllegalArgumentException if the graph argument is
     *         <code>null</code>.
     */
    public TiernanSimpleCycles(DirectedGraph<V, E> graph)
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
        Map<V, Integer> indices = new HashMap<V, Integer>();
        List<V> path = new ArrayList<V>();
        Set<V> pathSet = new HashSet<V>();
        Map<V, Set<V>> blocked = new HashMap<V, Set<V>>();
        List<List<V>> cycles = new LinkedList<List<V>>();

        int index = 0;
        for (V v : graph.vertexSet()) {
            blocked.put(v, new HashSet<V>());
            indices.put(v, index++);
        }

        Iterator<V> vertexIterator = graph.vertexSet().iterator();
        if (!vertexIterator.hasNext()) {
            return cycles;
        }

        V startOfPath = null;
        V endOfPath = null;
        V temp = null;
        int endIndex = 0;
        boolean extensionFound = false;

        endOfPath = vertexIterator.next();
        path.add(endOfPath);
        pathSet.add(endOfPath);

        // A mostly straightforward implementation
        // of the algorithm. Except that there is
        // no real need for the state machine from
        // the original paper.
        while (true) {
            // path extension
            do {
                extensionFound = false;
                for (E e : graph.outgoingEdgesOf(endOfPath)) {
                    V n = graph.getEdgeTarget(e);
                    int cmp = indices.get(n).
                        compareTo(indices.get(path.get(0)));
                    if ((cmp > 0) &&
                            !pathSet.contains(n) &&
                            !blocked.get(endOfPath).contains(n))
                    {
                        path.add(n);
                        pathSet.add(n);
                        endOfPath = n;
                        extensionFound = true;
                        break;
                    }
                }
            } while (extensionFound);
            // circuit confirmation
            startOfPath = path.get(0);
            if (graph.containsEdge(endOfPath, startOfPath)) {
                List<V> cycle = new ArrayList<V>();
                cycle.addAll(path);
                cycles.add(cycle);
            }
            // vertex closure
            if (path.size() > 1) {
                blocked.get(endOfPath).clear();
                endIndex = path.size() - 1;
                path.remove(endIndex);
                pathSet.remove(endOfPath);
                --endIndex;
                temp = endOfPath;
                endOfPath = path.get(endIndex);
                blocked.get(endOfPath).add(temp);
                continue;
            }
            // advance initial index
            if (vertexIterator.hasNext()) {
                path.clear();
                pathSet.clear();
                endOfPath = vertexIterator.next();
                path.add(endOfPath);
                pathSet.add(endOfPath);
                for (V vt : blocked.keySet()) {
                    blocked.get(vt).clear();
                }
                continue;
            }
            // terminate
            break;
        }

        return cycles;
    }
}
