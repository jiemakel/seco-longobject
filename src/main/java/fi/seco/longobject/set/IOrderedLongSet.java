package fi.seco.longobject.set;

import fi.seco.collections.iterator.primitive.IOrderedLongAdvanceIterator;

public interface IOrderedLongSet extends ILongSet {

	public IOrderedLongAdvanceIterator orderedAdvanceIterator();

}
