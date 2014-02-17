package fi.seco.longobject.set;

import java.lang.management.ManagementFactory;
import java.util.Iterator;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

public class SmartBackingLongSet implements IMutableLongSetWithConcurrentAdd {

	private IMutableLongSetWithConcurrentAdd back = new LongOpenHashSetLongSet(SPLIT_HIGH);
	private long psize;
	private boolean isSmall = true;

	private static final int SPLIT_HIGH = 10000;
	private static final int SPLIT_LOW = 9000;

	private static long creationCount = 0;
	private static long smallToLargeSwitchCount = 0;
	private static long largeToSmallSwitchCount = 0;

	{
		creationCount++;
	}

	public static interface SmartBackingLongSetInfoMBean {

		public long getSmallToLargeSwitchCount();

		public long getLargeToSmallSwitchCount();

		public long getCreationCount();
	}

	public static class SmartBackingLongSetInfo implements SmartBackingLongSetInfoMBean {

		@Override
		public long getSmallToLargeSwitchCount() {
			return smallToLargeSwitchCount;
		}

		@Override
		public long getLargeToSmallSwitchCount() {
			return largeToSmallSwitchCount;
		}

		@Override
		public long getCreationCount() {
			return creationCount;
		}
	}

	private static final SmartBackingLongSetInfo info = new SmartBackingLongSetInfo();

	static {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		try {
			mbs.registerMBean(info, new ObjectName("fi.seco.base.util.set:type=SmartBackingLongSet"));
		} catch (Exception e) {}
	}

	@Override
	public boolean contains(long id) {
		return back.contains(id);
	}

	@Override
	public Iterator<LongCursor> iterator() {
		return back.iterator();
	}

	@Override
	public long size() {
		return back.size();
	}

	@Override
	public boolean isEmpty() {
		return back.isEmpty();
	}

	@Override
	public long[] toArray() {
		return back.toArray();
	}

	private final void checkEnlarge() {
		if (isSmall & psize >= SPLIT_HIGH) {
			smallToLargeSwitchCount++;
			IMutableLongSetWithConcurrentAdd tmp = new SparseBitSetLongSet();
			tmp.or(back);
			back = tmp;
			isSmall = false;
		}
	}

	private final void checkShorten() {
		if (!isSmall && psize <= SPLIT_LOW) {
			largeToSmallSwitchCount++;
			IMutableLongSetWithConcurrentAdd tmp = new LongOpenHashSetLongSet(SPLIT_HIGH);
			tmp.or(back);
			back = tmp;
			isSmall = true;
		}
	}

	@Override
	public void add(long id) {
		synchronized (this) {
			psize++;
			checkEnlarge();
		}
		back.add(id);
	}

	@Override
	public void remove(long id) {
		psize--;
		checkShorten();
		back.remove(id);
	}

	@Override
	public void or(ILongSet other) {
		if (other == this) return;
		psize += other.size();
		checkEnlarge();
		if (other instanceof SmartBackingLongSet)
			back.or(((SmartBackingLongSet) other).back);
		else back.or(other);
	}

	@Override
	public void and(ILongSet other) {
		if (other == this) return;
		if (other instanceof SmartBackingLongSet)
			back.and(((SmartBackingLongSet) other).back);
		else back.and(other);
		psize = back.size();
		checkShorten();
	}

	@Override
	public void andNot(ILongSet other) {
		if (other == this) {
			clear();
			return;
		}
		if (other instanceof SmartBackingLongSet)
			back.andNot(((SmartBackingLongSet) other).back);
		else back.andNot(other);
		psize = back.size();
		checkShorten();
	}

	@Override
	public void clear() {
		psize = 0;
		back = new LongOpenHashSetLongSet(SPLIT_HIGH);
	}

	@Override
	public String toString() {
		return "SmartBackingLongSet(" + (back instanceof LongOpenHashSetLongSet ? "S" : "L") + "): " + back.toString();
	}

	@Override
	public <T extends LongProcedure> T forEach(T procedure) {
		return back.forEach(procedure);
	}

	@Override
	public <T extends LongPredicate> T forEach(T procedure) {
		return back.forEach(procedure);
	}

	@Override
	public long retainAll(LongPredicate predicate) {
		return back.retainAll(predicate);
	}

}
