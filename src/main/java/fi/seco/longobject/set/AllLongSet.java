/**
 * 
 */
package fi.seco.longobject.set;

import java.util.Iterator;

import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

import fi.seco.collections.iterator.primitive.IOrderedLongAdvanceIterator;

public class AllLongSet implements IOrderedLongSet {

	private final long minId;
	private final long maxId;

	public AllLongSet(long minId, long maxId) {
		this.minId = minId;
		this.maxId = maxId;
	}

	@Override
	public boolean contains(long id) {
		return id >= minId && id <= maxId;
	}

	@Override
	public boolean isEmpty() {
		return maxId >= minId;
	}

	@Override
	public Iterator<LongCursor> iterator() {
		return new Iterator<LongCursor>() {

			private final LongCursor lc = new LongCursor();
			private long i = minId;

			@Override
			public boolean hasNext() {
				return i <= maxId;
			}

			@Override
			public LongCursor next() {
				lc.value = i++;
				return lc;
			}

			@Override
			public void remove() {}
		};
	}

	@Override
	public long size() {
		return maxId - minId + 1;
	}

	@Override
	public long[] toArray() {
		long[] ret = new long[(int) (maxId - minId + 1)];
		int j = 0;
		for (long i = minId; i <= maxId; i++)
			ret[j++] = i;
		return ret;
	}

	@Override
	public String toString() {
		return "[" + minId + ".." + maxId + "]";
	}

	@Override
	public <T extends LongProcedure> T forEach(T procedure) {
		for (long i = minId; i <= maxId; i++)
			procedure.apply(i);
		return procedure;
	}

	@Override
	public <T extends LongPredicate> T forEach(T procedure) {
		for (long i = minId; i <= maxId; i++)
			if (!procedure.apply(i)) return procedure;
		return procedure;
	}

	@Override
	public IOrderedLongAdvanceIterator orderedAdvanceIterator() {
		return new IOrderedLongAdvanceIterator() {

			private long cur = minId - 1;

			@Override
			public long next() {
				if (++cur > maxId) return Long.MAX_VALUE;
				return cur;
			}

			@Override
			public long advance(long value) {
				if (value > maxId) return Long.MAX_VALUE;
				cur = value;
				return cur;
			}
		};
	}

}