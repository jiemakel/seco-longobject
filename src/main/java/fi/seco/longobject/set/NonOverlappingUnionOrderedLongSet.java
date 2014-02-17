/**
 * 
 */
package fi.seco.longobject.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

import fi.seco.collections.iterator.IIterableIterator;
import fi.seco.collections.iterator.IteratorIterableExpandingIterator;
import fi.seco.collections.iterator.primitive.IOrderedLongAdvanceIterator;

public class NonOverlappingUnionOrderedLongSet implements IOrderedLongSet {
	private final List<IOrderedLongSet> sets;

	public NonOverlappingUnionOrderedLongSet() {
		sets = new ArrayList<IOrderedLongSet>();
	}

	public NonOverlappingUnionOrderedLongSet(IOrderedLongSet... sets) {
		this.sets = Arrays.asList(sets);
	}

	public NonOverlappingUnionOrderedLongSet(List<IOrderedLongSet> sets) {
		this.sets = sets;
	}

	public NonOverlappingUnionOrderedLongSet addSet(IOrderedLongSet set) {
		sets.add(set);
		return this;
	}

	@Override
	public boolean contains(long id) {
		for (IOrderedLongSet set : sets)
			if (set.contains(id)) return true;
		return false;
	}

	@Override
	public boolean isEmpty() {
		for (IOrderedLongSet set : sets)
			if (!set.isEmpty()) return false;
		return true;
	}

	@Override
	public IIterableIterator<LongCursor> iterator() {
		IteratorIterableExpandingIterator<LongCursor> ret = new IteratorIterableExpandingIterator<LongCursor>();
		for (IOrderedLongSet set : sets)
			ret.add(set.iterator());
		return ret;
	}

	@Override
	public long size() {
		long size = 0;
		for (IOrderedLongSet set : sets)
			size += set.size();
		return size;
	}

	@Override
	public long[] toArray() {
		long[][] parts = new long[sets.size()][];
		int s = 0;
		for (int i = 0; i < parts.length; i++) {
			long[] tmp = sets.get(i).toArray();
			s += tmp.length;
			parts[i] = tmp;
		}
		long[] ret = new long[s];
		s = 0;
		for (int i = 0; i < parts.length; i++) {
			System.arraycopy(parts[i], 0, ret, s, parts[i].length);
			s += parts[i].length;
		}
		return ret;
	}

	@Override
	public <T extends LongProcedure> T forEach(T procedure) {
		for (IOrderedLongSet set : sets)
			set.forEach(procedure);
		return procedure;
	}

	@Override
	public <T extends LongPredicate> T forEach(T procedure) {
		for (IOrderedLongSet set : sets)
			set.forEach(procedure);
		return procedure;
	}

	@Override
	public String toString() {
		return "NonOverlappingUnionOrderedLongSet [" + sets + "]";
	}

	@Override
	public IOrderedLongAdvanceIterator orderedAdvanceIterator() {
		return new IOrderedLongAdvanceIterator() {

			private final Iterator<IOrderedLongSet> seti = sets.iterator();
			private IOrderedLongAdvanceIterator curi = seti.next().orderedAdvanceIterator();

			@Override
			public long next() {
				long ret = curi.next();
				while (ret == Long.MAX_VALUE && seti.hasNext()) {
					curi = seti.next().orderedAdvanceIterator();
					ret = curi.next();
				}
				return ret;
			}

			@Override
			public long advance(long value) {
				long ret = curi.advance(value);
				while (ret == Long.MAX_VALUE && seti.hasNext()) {
					curi = seti.next().orderedAdvanceIterator();
					ret = curi.advance(value);
				}
				return ret;
			}
		};
	}

}