# NioGraphs
Fork from project at [NioGraphs](https://code.google.com/p/niographs)



##A Library of Graph Algorithms

Surprisingly, algorithms for enumeration of simple cycles in a graph are not readily available in textbooks and on the web. This inspired me to develop a small Java library dedicated to the subject. It implements the 5 algorithms which appear to be the most useful for practical purposes : Tiernan's, Tarjan's, Johnson's, Szwarcfiter-Lauer's and Paton's. The implementation uses JgraphT graphs.

This library is currently incorporated into the JgraphT project (release 0.9.0 and later).

##Performance Notes:
The worst case time complexity of the algorithms for finding cycles in directed graphs is:
- Tiernan - O(V.const^V)
- Tarjan - O(VEC)
- Johnson - O(((V+E)C)
- Szwarcfiter and Lauer - O(V+EC)

where V is the number of vertices, E is the number of edges and C is the number of the simple cycles in the graph.
The worst case performance is achieved for graphs with special structure, so on practical workloads an algorithm with higher worst case complexity may outperform an algorithm with lower worst case complexity. Note also that "administrative costs" of algorithms with better worst case performance are higher. Also higher is their memory cost which is in all cases O(V+E)).

The author's workloads contain typically several hundred nodes and up to several thousand simple cycles. On these workloads the algorithms score by performance (best to worst) so :

1. Szwarcfiter and Lauer
2. Tarjan
3. Johnson
4. Tiernan

The worst case time complexity of the Paton's algorithm for finding a cycle base in undirected graphs is O(V^3)

##Implementation Note:

All the implementations work correctly with loops but not with multiple duplicate edges.

##Literature: 

1. J.C.Tiernan An Efficient Search Algorithm Find the Elementary Circuits of a Graph., Communications of the ACM, V13, 12, (1970), pp. 722 - 726.R.Tarjan, Depth-first search and linear graph algorithms., SIAM J. Comput. 1 (1972), pp. 146-160.
2. R. Tarjan, Enumeration of the elementary circuits of a directed graph, SIAM J. Comput., 2 (1973), pp. 211-216.
3. D.B.Johnson, Finding all the elementary circuits of a directed graph, SIAM J. Comput., 4 (1975), pp. 77-84.
4. J.L.Szwarcfiter and P.E.Lauer, Finding the elementary cycles of a directed graph in O(n + m) per cycle, Technical Report Series, #60, May 1974, Univ. of Newcastle upon Tyne, Newcastle upon Tyne, England.
5. P.Mateti and N.Deo, On algorithms for enumerating all circuits of a graph., SIAM J. Comput., 5 (1978), pp. 90-99.
6. L.G.Bezem and J.van Leeuwen, Enumeration in graphs., Technical report RUU-CS-87-7, University of Utrecht, The Netherlands, 1987.
7. K. Paton, An algorithm for finding a fundamental set of cycles for an undirected linear graph, Comm. ACM 12 (1969), pp. 514-518.
