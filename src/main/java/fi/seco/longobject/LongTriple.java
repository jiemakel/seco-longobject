package fi.seco.longobject;

public class LongTriple implements ILongTriple {

	protected final long subject;
	protected final long property;
	protected final long object;

	public LongTriple(long subject, long property, long object) {
		this.subject = subject;
		this.property = property;
		this.object = object;
	}

	public LongTriple(ILongTriple t) {
		this.subject = t.getSubject();
		this.property = t.getProperty();
		this.object = t.getObject();
	}

	@Override
	public long getSubject() {
		return subject;
	}

	@Override
	public long getProperty() {
		return property;
	}

	@Override
	public long getObject() {
		return object;
	}

	@Override
	public String toString() {
		return subject + "," + property + "," + object;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (object ^ (object >>> 32));
		result = prime * result + (int) (property ^ (property >>> 32));
		result = prime * result + (int) (subject ^ (subject >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof ILongTriple)) return false;
		ILongTriple other = (ILongTriple) obj;
		if (object != other.getObject()) return false;
		if (property != other.getProperty()) return false;
		if (subject != other.getSubject()) return false;
		return true;
	}

}
