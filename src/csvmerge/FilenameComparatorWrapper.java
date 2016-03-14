/**
 * 
 */
package csvmerge;

import java.io.File;
import java.util.Comparator;

/**
 * CSVMerge - FilenameComparatorWrapper
 * @version 1.0
 * @author <a href="mailto:Dessimat0r@ntlworld.com">Chris Dennett</a>
 */
public class FilenameComparatorWrapper implements Comparator<File> {
	protected final Comparator<String> comparator;
	
	/**
     * 
     */
    public FilenameComparatorWrapper(Comparator<String> comparator) {
	    this.comparator = comparator;
    }
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(File o1, File o2) {
	    return comparator.compare(o1.getName(), o2.getName());
	}
	
	/**
     * @return the comparator
     */
    public Comparator<String> getComparator() {
	    return comparator;
    }
}
