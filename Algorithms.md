# Algorithms #

Here is a crude explanation of the core ideas of the implemented algorithms:

### Directed Graphs ###
Perform a depth-first traversal of all simple paths in the graph. Every time when the current node has a successor on the stack a simple cycle is discovered. It consists of the elements on the stack starting with the discovered successor and ending at the top. The problems with this brute force approach are terrible performance and generation of multiple copies of the cycles. The implemented algorithms apply different schemes to eliminate generation of multiple copies and to improve the performance.

### Undirected Graphs ###
Build a spanning tree of the graph. Then each edge which is not in the tree forms a simple cycle together with some edges from the tree. Here the performance of a straightforward implementation is not as horrible as with path traversal but it can still be improved by careful organization of the job and the Paton's algorithm does that.

Given a cycle base all simple cycles can be discovered by examining all combinations of 2 or more distinct base cycles. The procedure is described in more detail here :  http://dspace.mit.edu/bitstream/handle/1721.1/68106/FTL_R_1982_07.pdf .