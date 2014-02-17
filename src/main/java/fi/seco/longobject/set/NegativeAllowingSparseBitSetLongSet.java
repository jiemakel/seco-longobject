package fi.seco.longobject.set;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

import fi.seco.collections.iterator.AMappingIterator;
import fi.seco.collections.iterator.IteratorIterableExpandingIterator;
import fi.seco.collections.set.SparseBitSet;

public class NegativeAllowingSparseBitSetLongSet implements IMutableLongSetWithConcurrentAdd {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(NegativeAllowingSparseBitSetLongSet.class);

	private final SparseBitSet pset;
	private final SparseBitSet nset;

	public NegativeAllowingSparseBitSetLongSet() {
		this.pset = new SparseBitSet();
		this.nset = new SparseBitSet();
	}

	public NegativeAllowingSparseBitSetLongSet(SparseBitSet nset, SparseBitSet pset) {
		this.pset = pset;
		this.nset = nset;
	}

	@Override
	public boolean contains(long id) {
		if (id > 0)
			return pset.get(id);
		else return nset.get(-id);
	}

	@Override
	public boolean isEmpty() {
		return pset.isEmpty() && nset.isEmpty();
	}

	@Override
	public Iterator<LongCursor> iterator() {
		return new IteratorIterableExpandingIterator<LongCursor>().add(pset.iterator()).add(new AMappingIterator<LongCursor, LongCursor>(nset.iterator()) {

			@Override
			protected LongCursor map(LongCursor src) {
				src.value = -src.value;
				return src;
			}
		});
	}

	@Override
	public long size() {
		return (pset.cardinality() + nset.cardinality());
	}

	@Override
	public long[] toArray() {
		long[] ret = new long[(int) (pset.cardinality() + nset.cardinality())];
		Iterator<LongCursor> ti = nset.iterator();
		int j = 0;
		for (long i = nset.cardinality(); i-- > 0;)
			ret[j++] = -ti.next().value;
		ti = pset.iterator();
		for (long i = pset.cardinality(); i-- > 0;)
			ret[j++] = ti.next().value;
		return ret;
	}

	@Override
	public void add(long id) {
		if (id > 0)
			pset.set(id);
		else nset.set(-id);
	}

	@Override
	public void and(final ILongSet other) {
		if (other instanceof NegativeAllowingSparseBitSetLongSet) {
			pset.and(((NegativeAllowingSparseBitSetLongSet) other).pset);
			nset.and(((NegativeAllowingSparseBitSetLongSet) other).nset);
		} else if (other instanceof SparseBitSetLongSet) {
			pset.and(((SparseBitSetLongSet) other).getSet());
			nset.clear();
		} else if (other instanceof NegativeSparseBitSetLongSet) {
			nset.and(((NegativeSparseBitSetLongSet) other).getSet());
			pset.clear();
		} else {
			pset.forEach(new LongProcedure() {

				@Override
				public void apply(long value) {
					if (!other.contains(value)) pset.clear(value);
				}
			});
			nset.forEach(new LongProcedure() {

				@Override
				public void apply(long value) {
					if (!other.contains(-value)) nset.clear(value);
				}

			});
		}

	}

	@Override
	public void andNot(final ILongSet other) {
		if (other instanceof NegativeAllowingSparseBitSetLongSet) {
			pset.andNot(((NegativeAllowingSparseBitSetLongSet) other).pset);
			nset.andNot(((NegativeAllowingSparseBitSetLongSet) other).nset);
		} else if (other instanceof SparseBitSetLongSet)
			pset.andNot(((SparseBitSetLongSet) other).getSet());
		else if (other instanceof NegativeSparseBitSetLongSet)
			nset.andNot(((NegativeSparseBitSetLongSet) other).getSet());
		else if (size() < other.size()) {
			pset.forEach(new LongProcedure() {

				@Override
				public void apply(long value) {
					if (other.contains(value)) pset.clear(value);
				}

			});
			nset.forEach(new LongProcedure() {

				@Override
				public void apply(long value) {
					if (other.contains(-value)) nset.clear(value);
				}

			});
		} else other.forEach(new LongProcedure() {

			@Override
			public void apply(long value) {
				remove(value);
			}
		});
	}

	@Override
	public void clear() {
		pset.clear();
		nset.clear();
	}

	@Override
	public void or(ILongSet other) {
		if (other instanceof NegativeAllowingSparseBitSetLongSet) {
			pset.or(((NegativeAllowingSparseBitSetLongSet) other).pset);
			nset.or(((NegativeAllowingSparseBitSetLongSet) other).nset);
		} else if (other instanceof SparseBitSetLongSet)
			pset.or(((SparseBitSetLongSet) other).getSet());
		else if (other instanceof NegativeSparseBitSetLongSet)
			nset.or(((NegativeSparseBitSetLongSet) other).getSet());
		else other.forEach(new LongProcedure() {

			@Override
			public void apply(long value) {
				add(value);
			}
		});
	}

	@Override
	public void remove(long id) {
		if (id > 0)
			pset.clear(id);
		else nset.clear(-id);
	}

	@Override
	public String toString() {
		return pset.toString() + nset.toString();
	}

	@Override
	public <T extends LongProcedure> T forEach(final T procedure) {
		pset.forEach(procedure);
		nset.forEach(new LongProcedure() {

			@Override
			public void apply(long value) {
				procedure.apply(-value);
			}

		});
		return procedure;
	}

	@Override
	public <T extends LongPredicate> T forEach(final T procedure) {
		pset.forEach(procedure);
		nset.forEach(new LongPredicate() {

			@Override
			public boolean apply(long value) {
				return procedure.apply(-value);
			}

		});
		return procedure;
	}

	@Override
	public long retainAll(final LongPredicate predicate) {
		long c = pset.retainAll(predicate);
		c += nset.retainAll(new LongPredicate() {

			@Override
			public boolean apply(long value) {
				return predicate.apply(-value);
			}
		});
		return c;
	}
}