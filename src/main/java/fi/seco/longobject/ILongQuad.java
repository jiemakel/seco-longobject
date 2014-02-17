package fi.seco.longobject;

/**
 * A quad with <code>long</code> identifiers for subject, property, object and
 * graph
 * 
 * @author jiemakel
 * 
 */
public interface ILongQuad extends ILongTriple {

	public long getGraph();

}
