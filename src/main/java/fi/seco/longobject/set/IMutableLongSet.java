package fi.seco.longobject.set;

import com.carrotsearch.hppc.predicates.LongPredicate;

public interface IMutableLongSet extends ILongSet {
	public void add(long id);

	public void remove(long id);

	public void or(ILongSet other);

	public void and(ILongSet other);

	public void andNot(ILongSet other);

	public void clear();

	public long retainAll(LongPredicate predicate);
}
