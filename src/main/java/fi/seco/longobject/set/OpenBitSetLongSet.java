/**
 * 
 */
package fi.seco.longobject.set;

import java.util.Iterator;

import org.apache.lucene.util.OpenBitSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

import fi.seco.collections.iterator.primitive.IOrderedLongAdvanceIterator;

public class OpenBitSetLongSet implements IMutableOrderedLongSet {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(OpenBitSetLongSet.class);

	private final OpenBitSet set;

	public OpenBitSetLongSet(ILongSet other) {
		if (other instanceof OpenBitSetLongSet)
			set = new OpenBitSet(((OpenBitSetLongSet) other).getBitSetSize());
		else set = new OpenBitSet();
		or(other);
	}

	public OpenBitSetLongSet() {
		set = new OpenBitSet();
	}

	public OpenBitSetLongSet(long size) {
		set = new OpenBitSet(size);
	}

	public OpenBitSetLongSet(OpenBitSet set) {
		this.set = set;
	}

	@Override
	public void and(ILongSet other) {
		if (other == this) return;
		if (other instanceof OpenBitSetLongSet)
			set.and(((OpenBitSetLongSet) other).set);
		else for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1))
			if (!other.contains(i)) set.clear(i);
	}

	@Override
	public void andNot(ILongSet other) {
		if (other == this) {
			clear();
			return;
		}
		if (other instanceof OpenBitSetLongSet)
			set.andNot(((OpenBitSetLongSet) other).set);
		else if (set.size() < other.size()) {
			for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1))
				if (other.contains(i)) set.clear(i);
		} else other.forEach(new LongProcedure() {

			@Override
			public void apply(long value) {
				set.clear(value);
			}

		});
	}

	@Override
	public Iterator<LongCursor> iterator() {
		return new Iterator<LongCursor>() {

			private final LongCursor lc = new LongCursor();
			{
				lc.value = -1;
			}

			@Override
			public LongCursor next() {
				hasNext();
				moved = false;
				return lc;
			}

			private boolean moved = false;

			@Override
			public boolean hasNext() {
				if (!moved) {
					lc.value = set.nextSetBit(lc.value + 1);
					moved = true;
				}
				return lc.value >= 0;
			}

			@Override
			public void remove() {
				set.clear(lc.value);
			}
		};
	}

	@Override
	public void or(ILongSet other) {
		if (other == this) return;
		if (other instanceof OpenBitSetLongSet)
			set.or(((OpenBitSetLongSet) other).set);
		else other.forEach(new LongProcedure() {

			@Override
			public void apply(long value) {
				set.set(value);
			}

		});
	}

	@Override
	public long size() {
		return set.cardinality();
	}

	public long getBitSetSize() {
		return set.size();
	}

	@Override
	public boolean contains(long id) {
		return id >= 0 ? set.get(id) : false;
	}

	@Override
	public void add(long id) {
		set.set(id);
	}

	@Override
	public void remove(long id) {
		set.clear(id);
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public void clear() {
		set.clear(0, set.size());
	}

	@Override
	public long[] toArray() {
		long[] ret = new long[(int) set.size()];
		int j = 0;
		for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1))
			ret[j++] = i;
		return ret;
	}

	public OpenBitSet getBitSet() {
		return set;
	}

	@Override
	public long retainAll(LongPredicate predicate) {
		long c = 0;
		for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1))
			if (!predicate.apply(i)) {
				c++;
				set.clear(i);
			}
		return c;
	}

	@Override
	public <T extends LongProcedure> T forEach(T procedure) {
		for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1))
			procedure.apply(i);
		return procedure;
	}

	@Override
	public <T extends LongPredicate> T forEach(T procedure) {
		for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1))
			if (!procedure.apply(i)) return procedure;
		return procedure;
	}

	@Override
	public IOrderedLongAdvanceIterator orderedAdvanceIterator() {
		return new IOrderedLongAdvanceIterator() {

			private long cindex = -1;

			@Override
			public long next() {
				cindex = set.nextSetBit(cindex + 1);
				if (cindex == -1) return Long.MAX_VALUE;
				return cindex;
			}

			@Override
			public long advance(long value) {
				cindex = set.nextSetBit(value);
				if (cindex == -1) return Long.MAX_VALUE;
				return cindex;
			}
		};
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append('[');
		forEach(new LongProcedure() {

			@Override
			public void apply(long value) {
				sb.append(value);
				sb.append(',');
			}

		});
		sb.setLength(sb.length() - 1);
		sb.append(']');
		return sb.toString();
	}
}