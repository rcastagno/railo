package railo.runtime.type;

import java.util.Iterator;

/**
 * interface that define that in a class a iterator is available
 */
public interface Iteratorable {

    /**
     * @return return a Iterator for Keys as Collection.Keys
     */
    public Iterator<Collection.Key> keyIterator();
    
    /**
     * @return return a Iterator for Keys as String
     */
    public Iterator<String> keysAsStringIterator();
    
    /**
     *
     * @return return a Iterator for Values
     */
    public Iterator<Object> valueIterator();
    
    /**
     * @return return a Iterator for keys
     * @deprecated use instead <code>{@link #keyIterator()}</code>
     */
    public Iterator iterator();
    
}