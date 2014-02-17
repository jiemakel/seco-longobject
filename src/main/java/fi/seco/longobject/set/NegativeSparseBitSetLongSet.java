package fi.seco.longobject.set;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

import fi.seco.collections.iterator.AMappingIterator;
import fi.seco.collections.set.SparseBitSet;

public class NegativeSparseBitSetLongSet implements IMutableLongSetWithConcurrentAdd {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(NegativeSparseBitSetLongSet.class);

	private final SparseBitSet set;

	public NegativeSparseBitSetLongSet() {
		this.set = new SparseBitSet();
	}

	public NegativeSparseBitSetLongSet(SparseBitSet set) {
		this.set = set;
	}

	@Override
	public boolean contains(long id) {
		if (id > 0) return false;
		return set.get(-id);
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public Iterator<LongCursor> iterator() {
		return new AMappingIterator<LongCursor, LongCursor>(set.iterator()) {

			@Override
			protected LongCursor map(LongCursor src) {
				src.value = -src.value;
				return src;
			}
		};
	}

	@Override
	public long size() {
		return set.cardinality();
	}

	@Override
	public long[] toArray() {
		final long[] ret = new long[(int) set.cardinality()];
		set.forEach(new LongProcedure() {
			int i = 0;

			@Override
			public void apply(long value) {
				ret[i++] = -value;
			}

		});
		return ret;
	}

	@Override
	public void add(long id) {
		set.set(-id);
	}

	@Override
	public void and(final ILongSet other) {
		if (other instanceof NegativeSparseBitSetLongSet)
			set.and(((NegativeSparseBitSetLongSet) other).set);
		else set.retainAll(new LongPredicate() {

			@Override
			public boolean apply(long value) {
				return other.contains(-value);
			}
		});
	}

	@Override
	public void andNot(final ILongSet other) {
		if (other instanceof NegativeSparseBitSetLongSet)
			set.andNot(((NegativeSparseBitSetLongSet) other).set);
		else if (set.cardinality() < other.size())
			set.retainAll(new LongPredicate() {

				@Override
				public boolean apply(long value) {
					return !other.contains(-value);
				}

			});
		else other.forEach(new LongProcedure() {

			@Override
			public void apply(long value) {
				if (value < 0) set.clear(-value);
			}
		});
	}

	@Override
	public void clear() {
		set.clear();
	}

	@Override
	public void or(ILongSet other) {
		if (other instanceof NegativeSparseBitSetLongSet)
			set.or(((NegativeSparseBitSetLongSet) other).set);
		else other.forEach(new LongProcedure() {

			@Override
			public void apply(long value) {
				set.set(-value);
			}

		});
	}

	@Override
	public void remove(long id) {
		if (id < 0) set.clear(-id);
	}

	@Override
	public String toString() {
		return set.toString();
	}

	public SparseBitSet getSet() {
		return set;
	}

	@Override
	public long retainAll(final LongPredicate predicate) {
		return set.retainAll(new LongPredicate() {

			@Override
			public boolean apply(long value) {
				return predicate.apply(-value);
			}

		});
	}

	@Override
	public <T extends LongProcedure> T forEach(final T procedure) {
		set.forEach(new LongProcedure() {

			@Override
			public void apply(long value) {
				procedure.apply(-value);
			}

		});
		return procedure;
	}

	@Override
	public <T extends LongPredicate> T forEach(final T procedure) {
		set.forEach(new LongPredicate() {

			@Override
			public boolean apply(long value) {
				return procedure.apply(-value);
			}

		});
		return procedure;
	}
}