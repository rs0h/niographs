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

public class TestGraphVisitor<V>
    implements GraphVisitor<V> {
    private boolean done = false;

    public void startVisit() {
        System.out.println("startVisit()");
    }

    public void preVisit(V vertex) {
        System.out.println("preVisit(" + vertex.toString() + ")");
    }

    public void postVisit(V vertex) {
        System.out.println("postVisit(" + vertex.toString() + ")");
    }

    public final boolean isDone() {
        return done;
    }

    /**
     * Sets the value of the visitor's done flag.
     * 
     * @param done The flag.
     */
    public final void setDone(boolean done) {
        this.done = done;
    }

    public void endVisit() {
        System.out.println("endVisit()");
    }

    public static void main(String[] a) {
        Graph<String> graph = new Graph<String>();
        AdjacencyList<String> sa = new AdjacencyList<String>();
        AdjacencyList<String> sb = new AdjacencyList<String>();
        AdjacencyList<String> sc = new AdjacencyList<String>();
        AdjacencyList<String> sd = new AdjacencyList<String>();
        sa.add("b");
        sa.add("c");
        sb.add("d");
        sc.add("d");
        graph.put("a", sa);
        graph.put("b", sb);
        graph.put("c", sc);
        graph.put("d", sd);
        System.out.println("Graph: " + graph);
        System.out.println("Depth-First:");
        graph.visitDepthFirst(new TestGraphVisitor<String>());
        System.out.println("\nDepth-First from b:");
        graph.visitDepthFirst(new TestGraphVisitor<String>(), "b");
        System.out.println("\nDepth-First from d:");
        graph.visitDepthFirst(new TestGraphVisitor<String>(), "d");
        System.out.println("\nBreadth-First:");
        graph.visitBreadthFirst(new TestGraphVisitor<String>());
        System.out.println("\nBreadth-First from b:");
        graph.visitBreadthFirst(new TestGraphVisitor<String>(), "b");
        System.out.println("\nBreadth-First from d:");
        graph.visitBreadthFirst(new TestGraphVisitor<String>(), "d");
    }
}
