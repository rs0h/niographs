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

/** 
 * Interface for visitors of {@link Graph}.
 * 
 * @param <V> The vertex type of the graph.
 */
public interface GraphVisitor<V> {
    /**
     * Called before the start of visit.
     */
    public void startVisit();

    /**
     * Called before a vertex is traversed. 
     * 
     * @param vertex The vertex.
     */
    public void preVisit(V vertex);

    /**
     * Called after a vertex is traversed.
     * 
     * @param vertex The vertex.
     */
    public void postVisit(V vertex);

    /**
     * Called after each visiting operation to check whether the visitor has
     * done its work. If so then the traversal of the graph is terminated.
     * 
     * @return A boolean indicating whether the visitor is done.
     */
    public boolean isDone();

    /** 
     * Called after the end of visit.
     */
    public void endVisit();
}
