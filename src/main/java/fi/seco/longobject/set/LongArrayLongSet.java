package fi.seco.longobject.set;

import java.util.Arrays;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

public class LongArrayLongSet implements ILongSet {

	private static final Logger log = LoggerFactory.getLogger(LongArrayLongSet.class);

	private final long[] l;

	public LongArrayLongSet(long[] l) {
		this.l = l;
	}

	private boolean sorted = false;

	@Override
	public boolean contains(long id) {
		if (!sorted) {
			Arrays.sort(l);
			sorted = true;
		}
		return Arrays.binarySearch(l, id) > 0;
	}

	@Override
	public Iterator<LongCursor> iterator() {
		return new Iterator<LongCursor>() {

			private final LongCursor lc = new LongCursor();
			int index = 0;

			@Override
			public boolean hasNext() {
				return index < l.length;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public LongCursor next() {
				lc.value = l[index++];
				return lc;
			}
		};
	}

	@Override
	public long size() {
		return l.length;
	}

	@Override
	public boolean isEmpty() {
		return l.length == 0;
	}

	@Override
	public long[] toArray() {
		return l;
	}

	@Override
	public String toString() {
		return Arrays.toString(l);
	}

	@Override
	public <T extends LongProcedure> T forEach(T procedure) {
		for (int i = 0; i < l.length; i++)
			procedure.apply(l[i]);
		return procedure;
	}

	@Override
	public <T extends LongPredicate> T forEach(T procedure) {
		for (int i = 0; i < l.length; i++)
			if (!procedure.apply(l[i])) return procedure;
		return procedure;
	}
}