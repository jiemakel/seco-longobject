/**
 * 
 */
package fi.seco.longobject.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

import fi.seco.collections.iterator.IIterableIterator;
import fi.seco.collections.iterator.IteratorIterableExpandingIterator;

public class NonOverlappingUnionLongSet implements ILongSet {
	private final List<ILongSet> sets = new ArrayList<ILongSet>();

	public NonOverlappingUnionLongSet() {}

	public NonOverlappingUnionLongSet(ILongSet... sets) {
		this.sets.addAll(Arrays.asList(sets));
	}

	public NonOverlappingUnionLongSet addSet(ILongSet set) {
		sets.add(set);
		return this;
	}

	@Override
	public boolean contains(long id) {
		for (ILongSet set : sets)
			if (set.contains(id)) return true;
		return false;
	}

	@Override
	public boolean isEmpty() {
		for (ILongSet set : sets)
			if (!set.isEmpty()) return false;
		return true;
	}

	@Override
	public IIterableIterator<LongCursor> iterator() {
		IteratorIterableExpandingIterator<LongCursor> ret = new IteratorIterableExpandingIterator<LongCursor>();
		for (ILongSet set : sets)
			ret.add(set.iterator());
		return ret;
	}

	@Override
	public long size() {
		long size = 0;
		for (ILongSet set : sets)
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
		for (ILongSet set : sets)
			set.forEach(procedure);
		return procedure;
	}

	@Override
	public <T extends LongPredicate> T forEach(T procedure) {
		for (ILongSet set : sets)
			set.forEach(procedure);
		return procedure;
	}

	@Override
	public String toString() {
		return "NonOverlappingUnionLongSet [" + sets + "]";
	}

}