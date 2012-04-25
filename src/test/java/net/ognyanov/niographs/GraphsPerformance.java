package net.ognyanov.niographs;

public class GraphsPerformance {

    public static void main(String[] a) {
        Graph<Integer> graph = new Graph<Integer>();
        int dimension = 9;

        for (int i = 0; i < dimension; i++) {
            AdjacencyList<Integer> adjSet = new AdjacencyList<Integer>();
            graph.put(i, adjSet);
        }

        for (int i = 0; i < dimension; i++) {
            AdjacencyList<Integer> adjSet = graph.get(i);
            for (int j = 0; j < dimension; j++) {
                //if (j % 10 == 0)
                adjSet.add(j);
            }
        }

        /*
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
        */

        System.out.println("Nodes:\t" + graph.size());
        int ec = 0;
        for (Integer k : graph.keySet()) {
            for (@SuppressWarnings("unused")
            Integer v : graph.get(k)) {
                ec++;
            }
        }
        long startTime = 0;
        long endTime = 0;

        System.out.println("Edges:\t" + ec);
        startTime = System.currentTimeMillis();
        int numSCCs = Graphs.countSCCs(graph);
        endTime = System.currentTimeMillis();
        System.out.println("SCCs      :\t" + numSCCs + "\t in "
                + (endTime - startTime) + " ms");

        int numCycles = 0;
        startTime = System.currentTimeMillis();
        numCycles = Graphs.countSimpleCyclesT(graph);
        endTime = System.currentTimeMillis();
        System.out.println("Cycles - T:\t" + numCycles + "\t in "
                + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        numCycles = Graphs.countSimpleCyclesJ(graph);
        endTime = System.currentTimeMillis();
        System.out.println("Cycles - J:\t" + numCycles + "\t in "
                + (endTime - startTime) + " ms");

        startTime = System.currentTimeMillis();
        numCycles = Graphs.countSimpleCyclesSL(graph);
        endTime = System.currentTimeMillis();
        System.out.println("Cycles - SL:\t" + numCycles + "\t in "
                + (endTime - startTime) + " ms");
    }
}
