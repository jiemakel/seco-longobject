package fi.seco.longobject.set;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

import fi.seco.collections.iterator.primitive.IOrderedLongAdvanceIterator;
import fi.seco.collections.set.SparseBitSet;

public class SparseBitSetLongSet implements IMutableOrderedLongSetWithConcurrentAdd {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(SparseBitSetLongSet.class);

	private final SparseBitSet set;

	public SparseBitSetLongSet() {
		this.set = new SparseBitSet();
	}

	public SparseBitSetLongSet(SparseBitSet set) {
		this.set = set;
	}

	@Override
	public boolean contains(long id) {
		return set.get(id);
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public Iterator<LongCursor> iterator() {
		return set.iterator();
	}

	@Override
	public long size() {
		return set.cardinality();
	}

	@Override
	public long[] toArray() {
		final long[] ret = new long[(int) set.cardinality()];
		set.forEach(new LongProcedure() {

			int j = 0;

			@Override
			public void apply(long value) {
				ret[j++] = value;
			}

		});
		return ret;
	}

	@Override
	public void add(long id) {
		set.set(id);
	}

	@Override
	public void and(final ILongSet other) {
		if (other instanceof SparseBitSetLongSet)
			set.and(((SparseBitSetLongSet) other).set);
		else set.retainAll(new LongPredicate() {

			@Override
			public boolean apply(long value) {
				return other.contains(value);
			}

		});
	}

	@Override
	public void andNot(final ILongSet other) {
		if (other instanceof SparseBitSetLongSet)
			set.andNot(((SparseBitSetLongSet) other).set);
		else if (set.cardinality() < other.size())
			set.retainAll(new LongPredicate() {

				@Override
				public boolean apply(long value) {
					return !other.contains(value);
				}
			});
		else other.forEach(new LongProcedure() {

			@Override
			public void apply(long value) {
				set.clear(value);
			}

		});
	}

	@Override
	public void clear() {
		set.clear();
	}

	@Override
	public void or(ILongSet other) {
		if (other instanceof SparseBitSetLongSet)
			set.or(((SparseBitSetLongSet) other).set);
		else other.forEach(new LongProcedure() {

			@Override
			public void apply(long value) {
				set.set(value);
			}

		});
	}

	@Override
	public void remove(long id) {
		set.clear(id);
	}

	@Override
	public String toString() {
		return set.toString();
	}

	public SparseBitSet getSet() {
		return set;
	}

	@Override
	public <T extends LongProcedure> T forEach(T procedure) {
		return set.forEach(procedure);
	}

	@Override
	public <T extends LongPredicate> T forEach(T procedure) {
		return set.forEach(procedure);
	}

	@Override
	public long retainAll(LongPredicate predicate) {
		return set.retainAll(predicate);
	}

	@Override
	public IOrderedLongAdvanceIterator orderedAdvanceIterator() {
		return set.orderedAdvanceIterator();
	}
}