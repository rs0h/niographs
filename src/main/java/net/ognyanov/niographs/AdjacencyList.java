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

import java.util.LinkedHashSet;

/**
 * Adjacency list type to be used in {@link Graph}
 *
 * @param <V> The vertex type.
 */
public final class AdjacencyList<V>
    extends LinkedHashSet<V>
{
    private static final long serialVersionUID = -107367584272648278L;
}
