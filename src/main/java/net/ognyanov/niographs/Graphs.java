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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Provides implementation of graph-related algorithms.<p>
 * <b>Performance Notes:</b><p>
 * &nbsp;&nbsp;&nbsp;&nbsp;
 * Worst case performance of the implemented algorithm for
 * enumeration of the strongly connected components of a graph
 * is O(V+E) where V is the number of vertices in the graph and
 * E is the number of edges in the graph.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;
 * Worst case time complexity of the algorithms for finding 
 * cycles in graphs is: O(VEC) for the Tarjan's algorithm, 
 * O(((V+E)C) for the Johnson's algorithm and O(V+EC) for 
 * the Szwarcfiter and Lauer's algorithm where C is the number
 * of the simple cycles in the graph. Worst case performance 
 * however is achieved only for graphs with special structure, 
 * so on practical workloads an algorithm with higher worst case
 * complexity may well outperform an algorithm with lower 
 * worst case complexity. Note also that "administrative costs"
 * of algorithms with better worst case performance are higher.
 * Also higher is their memory cost (which is in all 3
 * cases O(V+E)). In my experience so far Johnson's algorithm
 * seems to have best overall performance but this observation 
 * is workload-dependent and possibly implementation-dependent.
 * <p>
 * <b>Literature:</b><br>
 * <ol>
 * <li>
 * R.Tarjan, Depth-first search and linear graph algorithms.,
 * SIAM J. Comput. 1 (1972), pp. 146-160. 
 * </li>
 * <li>
 * R. Tarjan, Enumeration of the elementary circuits of 
 * a directed graph, SIAM J. Comput., 2 (1973), pp. 211-216.
 * </li>
 * <li>
 * D.B.Johnson, Finding all the elementary circuits of 
 * a directed graph, SIAM J. Comput., 4 (1975), pp. 77-84.
 * </li>
 * <li>
 * J.L.Szwarcfiter and P.E.Lauer, Finding the elementary 
 * cycles of a directed graph in O(n + m) per cycle, no. 60, 
 * Univ. of Newcastle upon Tyne, Newcastle upon Tyne, England, 
 * May 1974.
 * <li>
 * P.Mateti and N.Deo, On algorithms for enumerating all
 * circuits of a graph., SIAM J. Comput., 5 (1978), pp. 90-99.
 * </li>
 * <li>
 * L.G.Bezem and J.van Leeuwen, Enumeration in graphs., 
 * Technical report RUU-CS-87-7, University of Utrecht,
 * The Netherlands, 1987.
 * </li>
 * </li>
 * </ol>
 */
public final class Graphs {
    private static final class Numerator<T>
        implements GraphVisitor<T> {
        private Map<Integer, T> iToV  = new HashMap<Integer, T>();
        private Map<T, Integer> vToI  = new HashMap<T, Integer>();
        private int             index = 0;

        public Numerator(Map<Integer, T> iToV, Map<T, Integer> vToI) {
            this.iToV = iToV;
            this.vToI = vToI;
        }

        public void startVisit() {
            index = 0;
        }

        public void preVisit(T vertex) {
            if (iToV != null) {
                iToV.put(index, vertex);
            }
            if (vToI != null) {
                vToI.put(vertex, index);
            }
            index++;
        }

        public void postVisit(T vertex) {
        }

        public boolean isDone() {
            return false;
        }

        public void endVisit() {
        }

    }

    private static final class InDegreeCalculator<T>
        implements GraphVisitor<T> {
        private Graph<T>        graph;
        private Map<T, Integer> inDegrees;

        public InDegreeCalculator(Graph<T> graph) {
            this.graph = graph;
        }

        public Map<T, Integer> getInDegrees() {
            return inDegrees;
        }

        public void startVisit() {
            inDegrees = new HashMap<T, Integer>();
            for (T t : graph.keySet()) {
                inDegrees.put(t, 0);
            }
        }

        public void preVisit(T vertex) {
            for (T t : graph.get(vertex)) {
                inDegrees.put(t, inDegrees.get(t) + 1);
            }
        }

        public void postVisit(T vertex) {
        }

        public boolean isDone() {
            return false;
        }

        public void endVisit() {
        }
    }

    // Data context for the Tarjan's strongly connected components algorithm.
    private static final class SCCsCtxT<T> {
        int                    numSCCs    = 0;
        List<AdjacencyList<T>> SCCs       = new LinkedList<AdjacencyList<T>>();
        int                    index      = 0;
        Map<T, Integer>        indexMap   = new HashMap<T, Integer>();
        Map<T, Integer>        lowlinkMap = new HashMap<T, Integer>();
        Stack<T>               stack      = new Stack<T>();
    }

    // Data context for the Tarjahn's simple cycles algorithm.
    private static final class CyclesCtxT<T> {
        int                    numCycles   = 0;
        List<AdjacencyList<T>> cycles      = new LinkedList<AdjacencyList<T>>();
        Set<T>                 marked      = new HashSet<T>();
        Stack<T>               markedStack = new Stack<T>();
        Stack<T>               pointStack  = new Stack<T>();
    }

    // Data context for the Johnson's simple cycles algorithm.
    private static final class CyclesCtxJ<T> {
        int                    numCycles = 0;
        List<AdjacencyList<T>> cycles    = new LinkedList<AdjacencyList<T>>();
        Map<Integer, T>        iToV      = new HashMap<Integer, T>();
        Map<T, Integer>        vToI      = new HashMap<T, Integer>();
        Set<T>                 blocked   = new HashSet<T>();
        Map<T, Set<T>>         bSets     = new HashMap<T, Set<T>>();
        Stack<T>               stack     = new Stack<T>();

        CyclesCtxJ(Graph<T> graph) {
            GraphVisitor<T> numerator = new Numerator<T>(iToV, vToI);
            graph.visitDepthFirst(numerator);
            for (T t : graph.keySet()) {
                bSets.put(t, new HashSet<T>());
            }
        }
    }

    // Data context for the Schwarcfiter and Lauer's simple cycles algorithm.
    private static final class CyclesCtxSL<T> {
        int                          numCycles     = 0;
        List<AdjacencyList<T>>       cycles        =
                                                       new LinkedList<AdjacencyList<T>>();
        Map<Integer, T>              iToV          = new HashMap<Integer, T>();
        Map<T, Integer>              vToI          = new HashMap<T, Integer>();
        Map<T, Set<T>>               bSets         = new HashMap<T, Set<T>>();
        Stack<T>                     stack         = new Stack<T>();
        Set<T>                       marked        = new HashSet<T>();
        HashMap<T, AdjacencyList<T>> removed       = null;
        int[]                        position      = null;
        boolean[]                    reach         = null;
        List<T>                      startVertices = new ArrayList<T>();

        CyclesCtxSL(Graph<T> graph) {
            // prepare the S&L context
            removed = new HashMap<T, AdjacencyList<T>>();
            for (T t : graph.keySet()) {
                removed.put(t, new AdjacencyList<T>());
            }
            int size = graph.size();
            position = new int[size];
            reach = new boolean[size];
            GraphVisitor<T> numerator = new Numerator<T>(iToV, vToI);
            graph.visitDepthFirst(numerator);
            for (T t : graph.keySet()) {
                bSets.put(t, new HashSet<T>());
            }

            InDegreeCalculator<T> inDegreeCalculator =
                new InDegreeCalculator<T>(graph);
            graph.visitDepthFirst(inDegreeCalculator);
            Map<T, Integer> inDegrees = inDegreeCalculator.getInDegrees();
            List<AdjacencyList<T>> sccs = findSCCs(graph);
            for (AdjacencyList<T> scc : sccs) {
                int maxIngrade = -1;
                T startVertex = null;
                for (T t : scc) {
                    int inDegree = inDegrees.get(t);
                    if (inDegree > maxIngrade) {
                        maxIngrade = inDegree;
                        startVertex = t;
                    }
                }
                startVertices.add(startVertex);
            }
        }
    }

    /**
     * FInd all strongly connected components of a graph.
     * 
     * @param <V> The vertex type.
     * @param graph The graph.
     * @return A list of strongly connected components. May be empty
     * but not null.
     */
    public static <V> List<AdjacencyList<V>> findSCCs(Graph<V> graph) {
        if (graph == null) {
            throw new IllegalArgumentException(Graph.NULL_ARGUMENT);
        }
        SCCsCtxT<V> ctx = new SCCsCtxT<V>();
        for (V vertex : graph.keySet()) {
            if (!ctx.indexMap.containsKey(vertex)) {
                doStronglyConnect(ctx, graph, vertex, false);
            }
        }
        return ctx.SCCs;
    }

    /**
     * Count the number of strongly connected components in a graph.
     * 
     * @param <V> The vertex type.
     * @param graph The graph.
     * @return The number of strongly connected components in the graph.
     */
    public static <V> int countSCCs(Graph<V> graph) {
        if (graph == null) {
            throw new IllegalArgumentException(Graph.NULL_ARGUMENT);
        }
        SCCsCtxT<V> ctx = new SCCsCtxT<V>();
        for (V vertex : graph.keySet()) {
            if (!ctx.indexMap.containsKey(vertex)) {
                doStronglyConnect(ctx, graph, vertex, true);
            }
        }
        return ctx.numSCCs;
    }

    private static <V> void doStronglyConnect(SCCsCtxT<V> ctx, Graph<V> graph,
                                              V vertex, boolean countOnly) {
        AdjacencyList<V> result = null;

        ctx.indexMap.put(vertex, ctx.index);
        ctx.lowlinkMap.put(vertex, ctx.index);
        ctx.index++;
        ctx.stack.push(vertex);

        Iterator<V> it = graph.get(vertex).iterator();
        boolean gotSuccessors = it.hasNext();
        while (it.hasNext()) {
            V successor = it.next();
            if (!ctx.indexMap.containsKey(successor)) {
                doStronglyConnect(ctx, graph, successor, false);
                ctx.lowlinkMap.put(
                    vertex,
                    Math.min(ctx.lowlinkMap.get(vertex),
                        ctx.lowlinkMap.get(successor)));
            }
            else if (ctx.stack.contains(successor)) {
                ctx.lowlinkMap.put(
                    vertex,
                    Math.min(ctx.lowlinkMap.get(vertex),
                        ctx.indexMap.get(successor)));
            }
        }
        if (gotSuccessors
                && ctx.lowlinkMap.get(vertex) == ctx.indexMap.get(vertex)) {
            ctx.numSCCs++;
            if (!countOnly) {
                result = new AdjacencyList<V>();
                V temp = null;
                do {
                    temp = ctx.stack.pop();
                    result.add(temp);
                } while (!vertex.equals(temp));
                ctx.SCCs.add(result);
            }
        }
    }

    /**
     * Find all simple cycles in a graph by the Tarjan's algorithm.<p>
     * <b>Implementation Note:</b><br>
     * The vertex type must implement Comparable&lt;V&gt;.<br>
     * 
     * @param <V> The vertex type.
     * @param graph The graph.
     * @return A list of cycles.
     */
    public static <V> List<AdjacencyList<V>> findSimpleCyclesT(Graph<V> graph) {
        CyclesCtxT<V> ctx = new CyclesCtxT<V>();
        return doFindSimpleCyclesT(ctx, graph, false);
    }

    /**
     * Count the simple cycles in a graph by the Tarjan's algorithm.<p>
     * <b>Implementation Note:</b><br>
     * The vertex type must implement Comparable&lt;V&gt;.<br>
     * 
     * @param <V> The vertex type.
     * @param graph The graph.
     * @return The number of simple cycles in the graph.
     */
    public static <V> int countSimpleCyclesT(Graph<V> graph) {
        CyclesCtxT<V> ctx = new CyclesCtxT<V>();
        if (graph == null) {
            throw new IllegalArgumentException(Graph.NULL_ARGUMENT);
        }
        doFindSimpleCyclesT(ctx, graph, true);
        return ctx.numCycles;
    }

    private static <V> List<AdjacencyList<V>> doFindSimpleCyclesT(CyclesCtxT<V> ctx,
                                                                  Graph<V> graph,
                                                                  boolean countOnly) {
        if (graph == null) {
            throw new IllegalArgumentException(Graph.NULL_ARGUMENT);
        }

        for (V start : (Set<V>) graph.keySet()) {
            backtrackT(ctx, graph, start, start, countOnly);
            while (!ctx.markedStack.isEmpty()) {
                ctx.marked.remove(ctx.markedStack.pop());
            }
        }
        return ctx.cycles;
    }

    private static <V> boolean backtrackT(CyclesCtxT<V> ctx, Graph<V> graph,
                                          V start, V vertex, boolean countOnly) {
        boolean foundCycle = false;
        ctx.pointStack.push(vertex);
        ctx.marked.add(vertex);
        ctx.markedStack.push(vertex);

        for (V currentVertex : graph.get(vertex)) {
            @SuppressWarnings("unchecked")
            int comparison = ((Comparable<V>) currentVertex).compareTo(start);
            if (comparison < 0) {
            }
            else if (comparison == 0) {
                foundCycle = true;
                if (!countOnly) {
                    AdjacencyList<V> cycle = new AdjacencyList<V>();
                    int cycleStart = ctx.pointStack.indexOf(start);
                    int cycleEnd = ctx.pointStack.size() - 1; //pointStack.indexOf(vertexIndex);
                    for (int i = cycleStart; i <= cycleEnd; i++) {
                        cycle.add(ctx.pointStack.get(i));
                    }
                    ctx.cycles.add(cycle);
                }
                ctx.numCycles++;
            }
            else if (!ctx.marked.contains(currentVertex)) {
                boolean gotCycle =
                    backtrackT(ctx, graph, start, (V) currentVertex, countOnly);
                foundCycle = foundCycle || gotCycle;
            }
        }
        if (foundCycle) {
            while (!ctx.markedStack.peek().equals(vertex)) {
                ctx.marked.remove(ctx.markedStack.pop());
            }
            ctx.marked.remove(ctx.markedStack.pop()); // vertexIndex
        }
        ctx.pointStack.pop(); // vertexIndex
        return foundCycle;
    }

    /**
     * Find all simple cycles in a graph 
     * by the Johnson's algorithm.
     * 
     * @param <V> The vertex type.
     * @param graph The graph.
     * @return A list of cycles.
     */
    public static <V> List<AdjacencyList<V>> findSimpleCyclesJ(Graph<V> graph) {
        if (graph == null) {
            throw new IllegalArgumentException(Graph.NULL_ARGUMENT);
        }
        CyclesCtxJ<V> ctx = new CyclesCtxJ<V>(graph);
        return doFindSimpleCyclesJ(ctx, graph, false);
    }

    /**
     * Count the simple cycles in a graph 
     * by the Johnson's algorithm.
     * 
     * @param <V> The vertex type.
     * @param graph The graph.
     * @return The number of simple cycles in the graph.
     */
    public static <V> int countSimpleCyclesJ(Graph<V> graph) {
        if (graph == null) {
            throw new IllegalArgumentException(Graph.NULL_ARGUMENT);
        }
        CyclesCtxJ<V> ctx = new CyclesCtxJ<V>(graph);
        doFindSimpleCyclesJ(ctx, graph, true);
        return ctx.numCycles;
    }

    private static <V> List<AdjacencyList<V>> doFindSimpleCyclesJ(CyclesCtxJ<V> ctx,
                                                                  Graph<V> graph,
                                                                  boolean countOnly) {
        int startIndex = 0;
        int size = graph.size();
        while (startIndex < size) {
            Pair<Integer, Graph<V>> minSCCResult =
                minSCGraphJ(ctx, graph, startIndex, false);
            if (minSCCResult != null) {
                startIndex = minSCCResult.getFirst();
                Graph<V> scg = minSCCResult.getSecond();
                for (V v : scg.get(ctx.iToV.get(startIndex))) {
                    ctx.blocked.remove(v);
                    ctx.bSets.get(v).clear();
                }
                findCyclesInSCGJ(ctx, startIndex, startIndex, scg, countOnly);
                startIndex++;
            }
            else {
                break;
            }
        }

        return ctx.cycles;
    }

    private static <V> Pair<Integer, Graph<V>> minSCGraphJ(CyclesCtxJ<V> ctxJ,
                                                           Graph<V> graph,
                                                           int startIndex,
                                                           boolean countOnly) {
        SCCsCtxT<V> ctxT = new SCCsCtxT<V>();
        for (V v : graph.keySet()) {
            int vIndex = ctxJ.vToI.get(v);
            if (vIndex < startIndex) {
                continue;
            }
            if (!ctxT.indexMap.containsKey(v)) {
                doStronglyConnectJ(ctxJ, ctxT, graph, startIndex, vIndex, false);
            }
        }
        // find the SCC with minimum index
        int minIndexFound = Integer.MAX_VALUE;
        AdjacencyList<V> minSCC = null;
        for (AdjacencyList<V> scc : ctxT.SCCs) {
            for (V v : scc) {
                int t = ctxJ.vToI.get(v);
                if (t < minIndexFound) {
                    minIndexFound = t;
                    minSCC = scc;
                }
            }
        }
        if (minSCC == null) {
            return null;
        }
        // build a graph for the SCC found
        Graph<V> resultGraph = new Graph<V>();
        for (V v : minSCC) {
            AdjacencyList<V> aList = new AdjacencyList<V>();
            resultGraph.put(v, aList);
            AdjacencyList<V> origAList = graph.get(v);
            for (V w : minSCC) {
                if (origAList.contains(w)) {
                    aList.add(w);
                }
            }
        }
        return new Pair<Integer, Graph<V>>(minIndexFound, resultGraph);
    }

    private static <V> void doStronglyConnectJ(CyclesCtxJ<V> ctxJ,
                                               SCCsCtxT<V> ctx, Graph<V> graph,
                                               int startIndex, int vertexIndex,
                                               boolean countOnly) {
        AdjacencyList<V> result = null;

        V vertex = ctxJ.iToV.get(vertexIndex);
        ctx.indexMap.put(vertex, ctx.index);
        ctx.lowlinkMap.put(vertex, ctx.index);
        ctx.index++;
        ctx.stack.push(vertex);

        Iterator<V> it = graph.get(vertex).iterator();
        boolean gotSuccessors = it.hasNext();
        while (it.hasNext()) {
            V successor = it.next();
            int successorIndex = ctxJ.vToI.get(successor);
            if (successorIndex < startIndex) {
                continue;
            }
            if (!ctx.indexMap.containsKey(successor)) {
                doStronglyConnectJ(ctxJ, ctx, graph, startIndex,
                    successorIndex, countOnly);
                ctx.lowlinkMap.put(
                    vertex,
                    Math.min(ctx.lowlinkMap.get(vertex),
                        ctx.lowlinkMap.get(successor)));
            }
            else if (ctx.stack.contains(successor)) {
                ctx.lowlinkMap.put(
                    vertex,
                    Math.min(ctx.lowlinkMap.get(vertex),
                        ctx.indexMap.get(successor)));
            }
        }
        if (gotSuccessors
                && ctx.lowlinkMap.get(vertex) == ctx.indexMap.get(vertex)) {
            ctx.numSCCs++;
            if (!countOnly) {
                result = new AdjacencyList<V>();
                V temp = null;
                do {
                    temp = ctx.stack.pop();
                    result.add(temp);
                } while (!vertex.equals(temp));
                ctx.SCCs.add(result);
            }
        }
    }

    private static <V> boolean findCyclesInSCGJ(CyclesCtxJ<V> ctx,
                                                int startIndex,
                                                int vertexIndex, Graph<V> scg,
                                                boolean countOnly) {
        boolean foundCycle = false;
        V vertex = ctx.iToV.get(vertexIndex);
        ctx.stack.push(vertex);
        ctx.blocked.add(vertex);

        for (V successor : scg.get(vertex)) {
            int successorIndex = ctx.vToI.get(successor);
            if (successorIndex == startIndex) {
                if (!countOnly) {
                    AdjacencyList<V> cycle = new AdjacencyList<V>();
                    cycle.addAll(ctx.stack);
                    ctx.cycles.add(cycle);
                }
                foundCycle = true;
                ctx.numCycles++;
            }
            else if (!ctx.blocked.contains(successor)) {
                boolean gotCycle =
                    findCyclesInSCGJ(ctx, startIndex, successorIndex, scg,
                        countOnly);
                foundCycle = foundCycle || gotCycle;
            }
        }
        if (foundCycle) {
            unblockJ(ctx, vertex);
        }
        else {
            for (V w : scg.get(vertex)) {
                Set<V> bList = ctx.bSets.get(w);
                if (!bList.contains(vertex)) {
                    bList.add(vertex);
                }
            }
        }
        ctx.stack.pop();
        return foundCycle;
    }

    private static <V> void unblockJ(CyclesCtxJ<V> ctx, V vertex) {
        ctx.blocked.remove(vertex);
        Set<V> bSet = ctx.bSets.get(vertex);
        while (bSet.size() > 0) {
            V w = bSet.iterator().next();
            bSet.remove(w);
            if (ctx.blocked.contains(w)) {
                unblockJ(ctx, w);
            }
        }
    }

    /**
     * Find all simple cycles in a graph 
     * by the Szwarcfiter and Lauer's algorithm.
     * 
     * @param <V> The vertex type.
     * @param graph The graph.
     * @return A list of cycles.
     */
    public static <V> List<AdjacencyList<V>> findSimpleCyclesSL(Graph<V> graph) {
        if (graph == null) {
            throw new IllegalArgumentException(Graph.NULL_ARGUMENT);
        }
        CyclesCtxSL<V> ctx = new CyclesCtxSL<V>(graph);
        return doFindSimpleCyclesSL(ctx, graph, false);
    }

    /**
     * Count the simple cycles in a graph 
     * by the Szwarcfiter and Lauer's algorithm.
     * 
     * @param <V> The vertex type.
     * @param graph The graph.
     * @return The number of simple cycles in the graph.
     */
    public static <V> int countSimpleCyclesSL(Graph<V> graph) {
        if (graph == null) {
            throw new IllegalArgumentException(Graph.NULL_ARGUMENT);
        }
        CyclesCtxSL<V> ctx = new CyclesCtxSL<V>(graph);
        doFindSimpleCyclesSL(ctx, graph, true);
        return ctx.numCycles;
    }

    private static <V> List<AdjacencyList<V>> doFindSimpleCyclesSL(CyclesCtxSL<V> ctx,
                                                                   Graph<V> graph,
                                                                   boolean countOnly) {
        for (V vertex : ctx.startVertices) {
            cycleSL(ctx, graph, ctx.vToI.get(vertex), 0, countOnly);
        }
        return ctx.cycles;
    }

    private static <V> boolean cycleSL(CyclesCtxSL<V> ctx, Graph<V> graph,
                                       int v, int q, boolean CountOnly) {
        boolean foundCycle = false;
        V vV = ctx.iToV.get(v);
        ctx.marked.add(vV);
        ctx.stack.push(vV);
        int t = ctx.stack.size();
        ctx.position[v] = t;
        if (!ctx.reach[v]) {
            q = t;
        }
        AdjacencyList<V> avRemoved = ctx.removed.get(vV);
        AdjacencyList<V> avList = graph.get(vV);
        Iterator<V> avIt = avList.iterator();
        while (avIt.hasNext()) {
            V wV = avIt.next();
            if (avRemoved.contains(wV)) {
                continue;
            }
            int w = ctx.vToI.get(wV);
            if (!ctx.marked.contains(wV)) {
                boolean gotCycle = cycleSL(ctx, graph, w, q, CountOnly);
                if (gotCycle) {
                    foundCycle = gotCycle;
                }
                else {
                    noCycleSL(ctx, graph, v, w);
                }
            }
            else if (ctx.position[w] <= q) {
                foundCycle = true;
                ctx.numCycles++;
                if (!CountOnly) {
                    int vIndex = ctx.stack.indexOf(vV);
                    int wIndex = ctx.stack.indexOf(wV);
                    AdjacencyList<V> cycle = new AdjacencyList<V>();
                    for (int i = vIndex; i <= wIndex; i++) {
                        cycle.add(ctx.stack.elementAt(i));
                    }
                    ctx.cycles.add(cycle);
                }
            }
            else {
                noCycleSL(ctx, graph, v, w);
            }
        }
        ctx.stack.pop();
        if (foundCycle) {
            unmarkSL(ctx, graph, v);
        }
        ctx.reach[v] = true;
        ctx.position[v] = graph.size();
        return foundCycle;
    }

    private static <V> void noCycleSL(CyclesCtxSL<V> ctx, Graph<V> graph,
                                      int x, int y) {
        V xV = ctx.iToV.get(x);
        V yV = ctx.iToV.get(y);

        Set<V> by = ctx.bSets.get(yV);
        AdjacencyList<V> axRemoved = ctx.removed.get(xV);

        by.add(xV);
        axRemoved.add(yV);
    }

    private static <V> void unmarkSL(CyclesCtxSL<V> ctx, Graph<V> graph, int x) {
        V xV = ctx.iToV.get(x);
        ctx.marked.remove(xV);
        Set<V> bx = ctx.bSets.get(xV);
        for (V yV : bx) {
            Set<V> ayRemoved = ctx.removed.get(yV);
            ayRemoved.remove(xV);
            if (ctx.marked.contains(yV)) {
                unmarkSL(ctx, graph, ctx.vToI.get(yV));
            }
        }
        bx.clear();
    }
}
