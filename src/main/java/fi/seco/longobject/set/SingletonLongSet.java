package fi.seco.longobject.set;

import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

import fi.seco.collections.iterator.IIterableIterator;
import fi.seco.collections.iterator.SingletonIterator;
import fi.seco.collections.iterator.primitive.IOrderedLongAdvanceIterator;

public class SingletonLongSet implements IOrderedLongSet {

	private final LongCursor singleton = new LongCursor();

	public SingletonLongSet(long singleton) {
		this.singleton.value = singleton;
	}

	@Override
	public boolean contains(long id) {
		return id == singleton.value;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public IIterableIterator<LongCursor> iterator() {
		return new SingletonIterator<LongCursor>(singleton);
	}

	@Override
	public long size() {
		return 1;
	}

	@Override
	public long[] toArray() {
		return new long[] { singleton.value };
	}

	@Override
	public <T extends LongProcedure> T forEach(T procedure) {
		procedure.apply(singleton.value);
		return procedure;
	}

	@Override
	public <T extends LongPredicate> T forEach(T procedure) {
		procedure.apply(singleton.value);
		return procedure;
	}

	@Override
	public String toString() {
		return "[" + singleton.value + "]";
	}

	@Override
	public IOrderedLongAdvanceIterator orderedAdvanceIterator() {
		return new IOrderedLongAdvanceIterator() {

			private long val = singleton.value;

			@Override
			public long next() {
				long tmp = val;
				val = Long.MAX_VALUE;
				return tmp;
			}

			@Override
			public long advance(long value) {
				if (value <= val) return next();
				return Long.MAX_VALUE;
			}
		};
	}

}
