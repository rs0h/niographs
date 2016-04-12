# NioGraphs #

## A Library of Graph Algorithms ##
<p>
Surprisingly, algorithms for enumeration of simple cycles in a graph are not readily available in textbooks and on the web. This inspired me to develop a small Java library  dedicated to the subject. It implements the 5 algorithms which appear to be the most useful for practical purposes : Tiernan's, Tarjan's, Johnson's, Szwarcfiter-Lauer's and Paton's. The implementation uses <a href='http://jgrapht.org'>JgraphT</a> graphs.<br>
<br>
This library is currently incorporated into the <a href='http://jgrapht.org'>JgraphT</a> project (release 0.9.0 and later).<br>
<br>
</p>
> <b>Performance Notes:</b>
> <p>The worst case time complexity of the algorithms for finding cycles in directed graphs is:<br>
<blockquote><ol>
<blockquote><li>Tiernan - O(V.const^V)</li>
<li>Tarjan - O(VEC)</li>
<li>Johnson - O(((V+E)C)</li>
<li>Szwarcfiter and Lauer - O(V+EC)</li>
</blockquote></ol>
where V is the number of vertices, E is the number of edges and C is the number of the simple cycles in the graph.<br>
</p>
<p>The worst case performance is achieved for graphs with special structure, so on practical workloads an algorithm with higher worst case complexity may outperform an algorithm with lower worst case complexity. Note also that "administrative costs" of algorithms with better worst case performance are higher. Also higher is their memory cost which is in all cases O(V+E)).</p>
<p>The author's workloads contain typically several hundred nodes and up to several  thousand simple cycles. On these workloads the algorithms score by performance (best to worst) so :<br>
<ol>
<blockquote><li>Szwarcfiter and Lauer</li>
<li>Tarjan</li>
<li>Johnson</li>
<li>Tiernan</li>
</blockquote></ol>
The worst case time complexity of the Paton's algorithm for finding a cycle base in undirected graphs is O(V^3)<br>
<p>
<b>Implementation Note:</b><p> All the implementations work correctly with loops but not with multiple duplicate edges.<br>
</p>
<b>Literature:</b>
<br>
<ol>
<li>J.C.Tiernan An Efficient Search Algorithm Find the Elementary Circuits of a Graph., Communications of the ACM, V13, 12, (1970), pp. 722 - 726.</li>
<li>R.Tarjan, Depth-first search and linear graph algorithms., SIAM J. Comput. 1 (1972), pp. 146-160.</li>
<li>R. Tarjan, Enumeration of the elementary circuits of a directed graph, SIAM J. Comput., 2 (1973), pp. 211-216.</li>
<li>D.B.Johnson, Finding all the elementary circuits of a directed graph, SIAM J. Comput., 4 (1975), pp. 77-84.</li>
<li>J.L.Szwarcfiter and P.E.Lauer, Finding the elementary cycles of a directed graph in O(n + m) per cycle, Technical Report Series, #60, May 1974, Univ. of Newcastle upon Tyne, Newcastle upon Tyne, England.</li>
<li>P.Mateti and N.Deo, On algorithms for enumerating all circuits of a graph., SIAM J. Comput., 5 (1978), pp. 90-99.</li>
<li>L.G.Bezem and J.van Leeuwen, Enumeration in graphs., Technical report RUU-CS-87-7, University of Utrecht, The Netherlands, 1987.</li>
<li>K. Paton, An algorithm for finding a fundamental set of cycles for an undirected linear graph, Comm. ACM 12 (1969), pp. 514-518.</li>
</ol>