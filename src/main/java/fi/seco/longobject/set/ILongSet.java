package fi.seco.longobject.set;

import java.util.Iterator;

import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

public interface ILongSet extends Iterable<LongCursor> {
	public boolean contains(long id);

	@Override
	public Iterator<LongCursor> iterator();

	public long size();

	public boolean isEmpty();

	public long[] toArray();

	/**
	 * Applies a <code>procedure</code> to all container elements. Returns the
	 * argument (any subclass of {@link LongProcedure}. This lets the caller to
	 * call methods of the argument by chaining the call (even if the argument
	 * is an anonymous type) to retrieve computed values, for example
	 * (IntContainer):
	 * 
	 * <pre>
	 * int count = container.forEach(new IntProcedure() {
	 * 	int count; // this is a field declaration in an anonymous class.
	 * 
	 * 	public void apply(int value) {
	 * 		count++;
	 * 	}
	 * }).count;
	 * </pre>
	 */
	public <T extends LongProcedure> T forEach(T procedure);

	/**
	 * Applies a <code>predicate</code> to container elements as long, as the
	 * predicate returns <code>true</code>. The iteration is interrupted
	 * otherwise.
	 */
	public <T extends LongPredicate> T forEach(T predicate);
}
