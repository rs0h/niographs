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

import org.junit.Test;

public class GraphsTest {
    @Test
    public void test() {
        int numCycles = 0;
        int numSCCs = 0;
        Graph<Integer> graph = new Graph<Integer>();
        int dimension = 9;

        for (int i = 0; i < dimension; i++) {
            AdjacencyList<Integer> adjSet = new AdjacencyList<Integer>();
            graph.put(i, adjSet);
        }
        graph.get(0).add(1);
        graph.get(1).add(0);
        graph.get(1).add(2);
        graph.get(2).add(3);
        graph.get(3).add(2);
        numSCCs = Graphs.countSCCs(graph);
        assertTrue(numSCCs == 2);
        numCycles = Graphs.countSimpleCyclesT(graph);
        assertTrue(numCycles == 2);
        numCycles = Graphs.countSimpleCyclesJ(graph);
        assertTrue(numCycles == 2);
        numCycles = Graphs.countSimpleCyclesSL(graph);
        assertTrue(numCycles == 2);

        graph.get(4).add(5);
        graph.get(5).add(4);
        graph.get(5).add(6);
        graph.get(6).add(7);
        graph.get(7).add(6);
        numSCCs = Graphs.countSCCs(graph);
        assertTrue(numSCCs == 4);
        numCycles = Graphs.countSimpleCyclesT(graph);
        assertTrue(numCycles == 4);
        numCycles = Graphs.countSimpleCyclesJ(graph);
        assertTrue(numCycles == 4);
        numCycles = Graphs.countSimpleCyclesSL(graph);
        assertTrue(numCycles == 4);

        graph.get(2).add(1);
        graph.get(7).add(0);
        numSCCs = Graphs.countSCCs(graph);
        assertTrue(numSCCs == 1);

        for (int i = 0; i < dimension; i++) {
            AdjacencyList<Integer> adjSet = graph.get(i);
            for (int j = 0; j < dimension; j++) {
                adjSet.add(j);
            }
        }
        numCycles = Graphs.countSimpleCyclesT(graph);
        assertTrue(numCycles == 125673);
        numCycles = Graphs.countSimpleCyclesJ(graph);
        assertTrue(numCycles == 125673);
        numCycles = Graphs.countSimpleCyclesSL(graph);
        assertTrue(numCycles == 125673);

        graph = new Graph<Integer>();
        dimension = 30;
        for (int i = 0; i < dimension; i++) {
            AdjacencyList<Integer> adjSet = new AdjacencyList<Integer>();
            for (int j = 0; j < dimension; j++) {
                if (j % 4 == 0)
                    adjSet.add(j);
            }
            graph.put(i, adjSet);
        }
        graph.get(0).add(1);
        graph.get(1).add(0);
        graph.get(1).add(2);
        graph.get(2).add(3);
        graph.get(3).add(2);

        graph.get(4).add(5);
        graph.get(5).add(4);
        graph.get(5).add(6);
        graph.get(6).add(7);
        graph.get(7).add(6);

        numCycles = Graphs.countSimpleCyclesT(graph);
        assertTrue(numCycles == 203961);
        numCycles = Graphs.countSimpleCyclesJ(graph);
        assertTrue(numCycles == 203961);
        numCycles = Graphs.countSimpleCyclesSL(graph);
        assertTrue(numCycles == 203961);
    }
}
