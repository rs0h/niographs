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

import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;

public class UndirectedSimpleCyclesTest
{
    private static int   MAX_SIZE = 10;
    private static int[] RESULTS  = { 0, 0, 0, 1, 3, 6, 10, 15, 21, 28, 36 };

    @Test
    public void test()
    {
        PatonSimpleCycles<Integer, DefaultEdge> patonFinder =
            new PatonSimpleCycles<Integer, DefaultEdge>();

        testAlgorithm(patonFinder);
    }

    private void testAlgorithm(
                               UndirectedSimpleCycles<Integer, DefaultEdge>
                               finder)
    {
        SimpleGraph<Integer, DefaultEdge> graph = new SimpleGraph<Integer, DefaultEdge>
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
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        checkResult(finder, 1);
        graph.addEdge(2, 3);
        graph.addEdge(3, 0);
        checkResult(finder, 2);
        graph.addEdge(3, 1);
        checkResult(finder, 3);
        graph.addEdge(3, 4);
        graph.addEdge(4, 2);
        checkResult(finder, 4);
        graph.addEdge(4, 5);
        checkResult(finder, 4);
        graph.addEdge(5, 2);
        checkResult(finder, 5);
        graph.addEdge(5, 6);
        graph.addEdge(6, 4);
        checkResult(finder, 6);

        for (int size = 1; size <= MAX_SIZE; size++) {
            graph = new SimpleGraph<Integer, DefaultEdge>
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
                    if (i != j) {
                        graph.addEdge(i, j);
                    }
                }
            }
            finder.setGraph(graph);
            checkResult(finder, RESULTS[size]);
        }
    }

    private void checkResult(UndirectedSimpleCycles
                             <Integer, DefaultEdge> finder,
                             int size)
    {
        assertTrue(finder.findSimpleCycles().size() == size);
    }
}
