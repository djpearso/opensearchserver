/**   
 * License Agreement for OpenSearchServer
 *
 * Copyright (C) 2014 Emmanuel Keller / Jaeksoft
 * 
 * http://www.open-search-server.com
 * 
 * This file is part of OpenSearchServer.
 *
 * OpenSearchServer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * OpenSearchServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenSearchServer. 
 *  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.jaeksoft.searchlib.index.osse.memory;

import java.io.Closeable;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.TreeMap;

public abstract class AbstractBuffer<T extends BufferItemInterface> implements
		Closeable {

	private long reused;
	private TreeMap<Long, ArrayDeque<T>> available;

	public AbstractBuffer() {
		available = new TreeMap<Long, ArrayDeque<T>>();
		reused = 0;
	}

	@Override
	final public void close() {
		System.out.println(this.getClass().getName() + " RELEASE "
				+ available.size() + " " + reused);
		available.clear();
		reused = 0;
	}

	protected abstract T newBufferItem(final long size);

	@SuppressWarnings("unchecked")
	final public T getNewBufferItem(final long size) {
		Map.Entry<Long, ArrayDeque<T>> entry = available.ceilingEntry(size);
		if (entry == null)
			return newBufferItem(size);
		ArrayDeque<T> memoryQue = entry.getValue();
		if (memoryQue.isEmpty())
			return newBufferItem(size);
		reused++;
		return (T) memoryQue.poll().reset();
	}

	final void closed(T bufferItem) {
		final long size = bufferItem.getSize();
		ArrayDeque<T> memoryQue = available.get(size);
		if (memoryQue == null) {
			memoryQue = new ArrayDeque<T>();
			available.put(size, memoryQue);
		}
		memoryQue.offer(bufferItem);
	}

}
