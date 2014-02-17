/**
 * 
 */
package fi.seco.longobject.set;

import java.util.Arrays;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.LongArrayList;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

public class LongArrayListLongSet implements IMutableLongSetWithConcurrentAdd {

	private static final Logger log = LoggerFactory.getLogger(LongArrayListLongSet.class);

	private final LongArrayList l;

	public LongArrayListLongSet() {
		l = new LongArrayList();
	}

	public LongArrayListLongSet(LongArrayList l) {
		this.l = l;
	}

	@Override
	public synchronized void add(long id) {
		l.add(id);
		sorted = false;
	}

	@Override
	public void and(ILongSet other) {
		for (int i = l.size() - 1; i >= 0; i--)
			if (!other.contains(l.get(i))) l.remove(i);
	}

	@Override
	public void andNot(ILongSet other) {
		for (int i = l.size() - 1; i >= 0; i--)
			if (other.contains(l.get(i))) l.remove(i);
	}

	@Override
	public Iterator<LongCursor> iterator() {
		return l.iterator();
	}

	@Override
	public void or(ILongSet other) {
		other.forEach(new LongProcedure() {

			@Override
			public void apply(long value) {
				l.add(value);
			}

		});
		sorted = false;
	}

	@Override
	public long size() {
		return l.size();
	}

	private boolean sorted = false;

	@Override
	public boolean contains(long id) {
		if (!sorted) {
			log.warn("contains is slow for LongArrayListSet");
			Arrays.sort(l.buffer, 0, l.elementsCount);
			sorted = true;
		}
		return Arrays.binarySearch(l.buffer, 0, l.elementsCount, id) > 0;
	}

	@Override
	public void remove(long id) {
		log.warn("remove is slow for LongArrayListSet");
		l.removeAllOccurrences(id);
	}

	@Override
	public boolean isEmpty() {
		return l.isEmpty();
	}

	@Override
	public void clear() {
		l.clear();
	}

	@Override
	public long[] toArray() {
		return l.toArray();
	}

	@Override
	public <T extends LongProcedure> T forEach(T procedure) {
		return l.forEach(procedure);
	}

	@Override
	public <T extends LongPredicate> T forEach(T procedure) {
		return l.forEach(procedure);
	}

	@Override
	public long retainAll(LongPredicate predicate) {
		return l.retainAll(predicate);
	}

}