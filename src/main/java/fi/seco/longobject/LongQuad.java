package fi.seco.longobject;

public class LongQuad extends LongTriple implements ILongQuad {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (graph ^ (graph >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (!(obj instanceof ILongQuad)) return false;
		ILongQuad other = (ILongQuad) obj;
		if (graph != other.getGraph()) return false;
		return true;
	}

	protected final long graph;

	public LongQuad(long subject, long property, long object, long graph) {
		super(subject, property, object);
		this.graph = graph;
	}

	public LongQuad(ILongTriple t, long graph) {
		super(t);
		this.graph = graph;
	}

	@Override
	public long getGraph() {
		return graph;
	}

	@Override
	public String toString() {
		return super.toString() + "," + graph;
	}

}
