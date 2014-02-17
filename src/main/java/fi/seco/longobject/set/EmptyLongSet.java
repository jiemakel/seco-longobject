/**
 * 
 */
package fi.seco.longobject.set;

import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

import fi.seco.collections.iterator.EmptyIterator;
import fi.seco.collections.iterator.IIterableIterator;
import fi.seco.collections.iterator.primitive.IOrderedLongAdvanceIterator;

/**
 * @author jiemakel
 * 
 */
public class EmptyLongSet implements IOrderedLongSet {

	private static final long[] emptyLong = new long[0];

	public static final EmptyLongSet instance = new EmptyLongSet();

	@Override
	public boolean contains(long id) {
		return false;
	}

	@Override
	public IIterableIterator<LongCursor> iterator() {
		return EmptyIterator.getInstance();
	}

	@Override
	public long size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public long[] toArray() {
		return emptyLong;
	}

	@Override
	public <T extends LongProcedure> T forEach(T procedure) {
		return procedure;
	}

	@Override
	public <T extends LongPredicate> T forEach(T procedure) {
		return procedure;
	}

	@Override
	public String toString() {
		return "[]";
	}

	@Override
	public IOrderedLongAdvanceIterator orderedAdvanceIterator() {
		return new IOrderedLongAdvanceIterator() {

			@Override
			public long next() {
				return Long.MAX_VALUE;
			}

			@Override
			public long advance(long value) {
				return Long.MAX_VALUE;
			}
		};
	}

}
