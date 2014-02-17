package fi.seco.longobject.set;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.lucene.util.Bits;

import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

import fi.seco.collections.iterator.primitive.IOrderedLongAdvanceIterator;

public class AllLongSetIf implements IOrderedLongSet {

	private final long minId;
	private final long maxId;
	private final Bits accept;
	private final long size;

	public AllLongSetIf(long minId, Bits accept, int size) {
		this.minId = minId;
		this.maxId = minId + accept.length() - 1;
		this.accept = accept;
		this.size = size;
	}

	@Override
	public boolean contains(long id) {
		return id > minId && id <= maxId && accept.get((int) (id - minId));
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Iterator<LongCursor> iterator() {
		return new Iterator<LongCursor>() {

			private final LongCursor lc = new LongCursor();
			private long i = minId;

			@Override
			public boolean hasNext() {
				while (i <= maxId && !accept.get((int) (i - minId)))
					i++;
				return i <= maxId;
			}

			@Override
			public LongCursor next() {
				while (!accept.get((int) (i - minId)))
					i++;
				lc.value = i++;
				return lc;
			}

			@Override
			public void remove() {}
		};
	}

	@Override
	public long size() {
		return size;
	}

	@Override
	public long[] toArray() {
		long[] ret = new long[(int) size];
		int j = 0;
		for (long i = minId; i <= maxId; i++)
			if (accept.get((int) (i - minId))) ret[j++] = i;
		return ret;
	}

	@Override
	public String toString() {
		return Arrays.toString(toArray());
	}

	@Override
	public <T extends LongProcedure> T forEach(T procedure) {
		for (long i = minId; i <= maxId; i++)
			if (accept.get((int) (i - minId))) procedure.apply(i);
		return procedure;
	}

	@Override
	public <T extends LongPredicate> T forEach(T procedure) {
		for (long i = minId; i <= maxId; i++)
			if (accept.get((int) (i - minId))) if (!procedure.apply(i)) return procedure;
		return procedure;
	}

	@Override
	public IOrderedLongAdvanceIterator orderedAdvanceIterator() {
		return new IOrderedLongAdvanceIterator() {

			private long cur = minId - 1;

			@Override
			public long next() {
				while (true) {
					if (++cur > maxId) return Long.MAX_VALUE;
					if (accept.get((int) (cur - minId))) return cur;
				}
			}

			@Override
			public long advance(long value) {
				if (value > maxId) return Long.MAX_VALUE;
				cur = value - 1;
				return next();
			}

		};
	}

}