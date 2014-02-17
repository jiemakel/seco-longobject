package fi.seco.longobject;

/**
 * A triple with <code>long</code> identifiers for subject, property and object
 * 
 * @author jiemakel
 * 
 */
public interface ILongTriple {
	public long getSubject();

	public long getProperty();

	public long getObject();

	@Override
	public int hashCode();

	@Override
	public boolean equals(Object other);
}
