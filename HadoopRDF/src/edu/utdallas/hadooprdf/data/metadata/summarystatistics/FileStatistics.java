/**
 * 
 */
package edu.utdallas.hadooprdf.data.metadata.summarystatistics;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class FileStatistics {
	private String fileName;
	private String filePath;
	private long largestSubjectId;
	private long smallestSubjectId;
	private long numberOfRecords;
	
	public FileStatistics(String fileName, String filePath,
			long largestSubjectId, long smallestSubjectId, long numberOfRecords) {
		this.fileName = fileName;
		this.filePath = filePath;
		this.largestSubjectId = largestSubjectId;
		this.smallestSubjectId = smallestSubjectId;
		this.numberOfRecords = numberOfRecords;
	}
	
	public FileStatistics() {
		this(null, null, 0, 0, 0);
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * @return the largestSubjectId
	 */
	public long getLargestSubjectId() {
		return largestSubjectId;
	}

	/**
	 * @param largestSubjectId the largestSubjectId to set
	 */
	public void setLargestSubjectId(long largestSubjectId) {
		this.largestSubjectId = largestSubjectId;
	}

	/**
	 * @return the smallestSubjectId
	 */
	public long getSmallestSubjectId() {
		return smallestSubjectId;
	}

	/**
	 * @param smallestSubjectId the smallestSubjectId to set
	 */
	public void setSmallestSubjectId(long smallestSubjectId) {
		this.smallestSubjectId = smallestSubjectId;
	}

	/**
	 * @return the numberOfRecords
	 */
	public long getNumberOfRecords() {
		return numberOfRecords;
	}

	/**
	 * @param numberOfRecords the numberOfRecords to set
	 */
	public void setNumberOfRecords(long numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("File summary statistics\n\tName: ");
		sb.append(fileName);
		sb.append("\n\tPath: ");
		sb.append(filePath);
		sb.append("\n\tLargest subject id: ");
		sb.append(largestSubjectId);
		sb.append("\n\tSmallest subject id: ");
		sb.append(smallestSubjectId);
		sb.append("\n\tNumber of records: ");
		sb.append(numberOfRecords);
		return sb.toString();
	}

}
