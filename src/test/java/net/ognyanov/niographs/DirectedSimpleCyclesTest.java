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

import static org.junit.Assert.assertTrue;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;

public class DirectedSimpleCyclesTest
{
    private static int   MAX_SIZE = 9;
    private static int[] RESULTS  =
                                  { 0, 1, 3, 8, 24, 89, 415, 2372, 16072,
                                  125673 };

    @Test
    public void test()
    {
        TiernanSimpleCycles<Integer, DefaultEdge> tiernanFinder =
            new TiernanSimpleCycles<Integer, DefaultEdge>();
        TarjanSimpleCycles<Integer, DefaultEdge> tarjanFinder =
            new TarjanSimpleCycles<Integer, DefaultEdge>();
        JohnsonSimpleCycles<Integer, DefaultEdge> johnsonFinder =
            new JohnsonSimpleCycles<Integer, DefaultEdge>();
        SzwarcfiterLauerSimpleCycles<Integer, DefaultEdge> szwarcfiterLauerFinder =
            new SzwarcfiterLauerSimpleCycles<Integer, DefaultEdge>();

        testAlgorithm(tiernanFinder);
        testAlgorithm(tarjanFinder);
        testAlgorithm(johnsonFinder);
        testAlgorithm(szwarcfiterLauerFinder);
    }

    private void testAlgorithm(
                               DirectedSimpleCycles<Integer, DefaultEdge>
                               finder)
    {
        DirectedGraph<Integer, DefaultEdge> graph = new DefaultDirectedGraph<Integer, DefaultEdge>
            (
             new ClassBasedEdgeFactory<Integer, DefaultEdge>
             (
              DefaultEdge.class
             )
            );
        for (int i = 0; i < 7; i++) {
            graph.addVertex(i);
        }
        finder.setGraph(graph);
        graph.addEdge(0, 0);
        checkResult(finder, 1);
        graph.addEdge(1, 1);
        checkResult(finder, 2);
        graph.addEdge(0, 1);
        graph.addEdge(1, 0);
        checkResult(finder, 3);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 0);
        checkResult(finder, 4);
        graph.addEdge(6, 6);
        checkResult(finder, 5);

        for (int size = 1; size <= MAX_SIZE; size++) {
            graph = new DefaultDirectedGraph<Integer, DefaultEdge>
                (
                 new ClassBasedEdgeFactory<Integer, DefaultEdge>
                 (
                  DefaultEdge.class
                 )
                );
            for (int i = 0; i < size; i++) {
                graph.addVertex(i);
            }
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    graph.addEdge(i, j);
                }
            }
            finder.setGraph(graph);
            checkResult(finder, RESULTS[size]);
        }
    }

    private void checkResult(DirectedSimpleCycles
                             <Integer, DefaultEdge> finder,
                             int size)
    {
        assertTrue(finder.findSimpleCycles().size() == size);
    }
}
