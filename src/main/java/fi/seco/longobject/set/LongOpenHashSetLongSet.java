/**
 * 
 */
package fi.seco.longobject.set;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.LongOpenHashSet;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

public class LongOpenHashSetLongSet implements IMutableLongSetWithConcurrentAdd {

	private final LongOpenHashSet set;
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(LongOpenHashSetLongSet.class);

	public LongOpenHashSetLongSet() {
		set = new LongOpenHashSet();
	}

	public LongOpenHashSetLongSet(LongOpenHashSet set) {
		this.set = set;
	}

	public LongOpenHashSetLongSet(int size) {
		set = new LongOpenHashSet(size);
	}

	public LongOpenHashSetLongSet(ILongSet oset) {
		set = new LongOpenHashSet((int) oset.size());
		or(oset);
	}

	@Override
	public void and(final ILongSet other) {
		if (other == this) return;
		set.retainAll(new LongPredicate() {

			@Override
			public boolean apply(long value) {
				return other.contains(value);
			}
		});
	}

	@Override
	public void andNot(final ILongSet other) {
		if (other == this) {
			clear();
			return;
		}
		if (set.size() < other.size())
			set.removeAll(new LongPredicate() {

				@Override
				public boolean apply(long value) {
					return other.contains(value);
				}
			});
		else other.forEach(new LongProcedure() {

			@Override
			public void apply(long value) {
				set.remove(value);
			}
		});
	}

	@Override
	public Iterator<LongCursor> iterator() {
		return set.iterator();
	}

	@Override
	public void or(ILongSet other) {
		if (other == this) return;
		other.forEach(new LongProcedure() {

			@Override
			public void apply(long value) {
				set.add(value);
			}

		});
	}

	@Override
	public long size() {
		return set.size();
	}

	@Override
	public boolean contains(long id) {
		return set.contains(id);
	}

	@Override
	public synchronized void add(long id) {
		set.add(id);
	}

	@Override
	public void remove(long id) {
		set.removeAllOccurrences(id);
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public void clear() {
		set.clear();
	}

	@Override
	public long[] toArray() {
		return set.toArray();
	}

	@Override
	public String toString() {
		return set.toString();
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

}