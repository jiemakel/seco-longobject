package fi.seco.longobject.set;

import java.util.Collection;

import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

import fi.seco.collections.collection.ABinaryHeap;

public class ISets {

	public static final IMutableLongSet getNewMutableLongSet(boolean maybeLarge, boolean needsNegatives,
			boolean needsPositives) {
		if (!maybeLarge) return new LongOpenHashSetLongSet();
		if (needsNegatives) {
			if (needsPositives) return new NegativeAllowingSparseBitSetLongSet();
			return new NegativeSparseBitSetLongSet();
		}
		return new SmartBackingLongSet();
		//return new SparseBitSetLongSet();
	}

	public static IMutableOrderedLongSet getNewMutableOrderedLongSet(boolean maybeLarge, boolean needsNegatives,
			boolean needsPositives) {
		if (!maybeLarge) return new OpenBitSetLongSet();
		return new SparseBitSetLongSet();
	}

	public static final IMutableLongSetWithConcurrentAdd getNewMutableLongSetWithConcurrentAdd(boolean maybeLarge,
			boolean needsNegatives, boolean needsPositives) {
		if (!maybeLarge) return new LongOpenHashSetLongSet();
		if (needsNegatives) {
			if (needsPositives) return new NegativeAllowingSparseBitSetLongSet();
			return new NegativeSparseBitSetLongSet();
		}
		return new SmartBackingLongSet();
		//return new SparseBitSetLongSet();
	}

	public static final ILongSet andPositive(Collection<ILongSet> sets) {
		return andPositive(sets.toArray(new ILongSet[sets.size()]));
	}

	public static final ILongSet andPositive(ILongSet... sets) {
		return and(getNewMutableLongSet(true, false, true), sets);
	}

	public static final ILongSet andPositiveAndNegative(ILongSet... sets) {
		return and(new LongOpenHashSetLongSet(), sets);
	}

	private static final ILongSet and(final IMutableLongSet ret, final ILongSet... sets) {
		if (sets.length == 1) return sets[0];
		new ABinaryHeap<ILongSet>(sets) {
			@Override
			public boolean lessThan(ILongSet i1, ILongSet i2) {
				return i1.size() < i2.size();
			}
		}; // heapify array to get min entry
		sets[0].forEach(new LongProcedure() {

			@Override
			public void apply(long id) {
				for (int i = 1; i < sets.length; i++)
					if (!sets[i].contains(id)) return;
				ret.add(id);
			}

		});
		return ret;
	}

	/**
	 * @param sets
	 *            the sets to check
	 * @return true if the intersection of the sets is not empty
	 */
	public static boolean intersectionNotEmpty(final ILongSet... sets) {
		new ABinaryHeap<ILongSet>(sets) {
			@Override
			public boolean lessThan(ILongSet i1, ILongSet i2) {
				return i1.size() < i2.size();
			}
		}; // heapify array to get min entry
		return sets[0].forEach(new LongPredicate() {

			private boolean found = false;

			@Override
			public boolean apply(long id) {
				for (int i = 1; i < sets.length; i++)
					if (!sets[i].contains(id)) return true;
				found = true;
				return false;
			}

		}).found;
	}

}