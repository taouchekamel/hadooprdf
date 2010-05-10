package edu.utdallas.hadooprdf.lib.util;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

/**
 * A path filter class which filters files based on extension
 * @author Mohammad Farhan Husain
 *
 */
public class PathFilterOnFilenameExtension implements PathFilter {
	/**
	 * The extension to filter on
	 */
	private String m_sFileExtension;
	/**
	 * The class constructor
	 * @param sFileExtension the extension to filter on
	 */
	public PathFilterOnFilenameExtension(String sFileExtension) {
		m_sFileExtension = "." + sFileExtension;
	}
	/* (non-Javadoc)
	 * @see org.apache.hadoop.fs.PathFilter#accept(org.apache.hadoop.fs.Path)
	 */
	@Override
	public boolean accept(Path path) {
		return path.getName().toLowerCase().endsWith(m_sFileExtension);
	}
}
