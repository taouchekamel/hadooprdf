/**
 * 
 */
package edu.utdallas.hadooprdf.data.metadata.summarystatistics;

/**
 * @author Mohammad Farhan Husain
 *
 */
public class SummaryStatisticsFileDoesNotExistException extends
		SummaryStatisticsException {

	private static final long serialVersionUID = -7100222351228496147L;

	/**
	 * @param errorMessage
	 */
	public SummaryStatisticsFileDoesNotExistException(String errorMessage) {
		super(errorMessage);
	}

}
