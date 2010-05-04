package edu.utdallas.hadooprdf.commons;

/**
 * A class for constant values used across the framework
 * @author Mohammad Farhan Husain
 */
public class Constants {
	/**
	 * RDF Serialization Formats
	 */
	public enum SerializationFormat {
		RDF_XML,	// RDF/XML
		NTRIPLES,	// NTriples
		N3,			// Notation 3
		TURTLE;		// Turtle
		
		/**
		 * @return the name of the format as a string
		 */
		public static String getSerializationFormatName(SerializationFormat format) {
			switch (format) {
			case RDF_XML:
				return "RDF/XML";
			case NTRIPLES:
				return "N-TRIPLE";
			case N3:
				return "N3";
			case TURTLE:
				return "TURTLE";
			default:
				return "Unknown";
			}
		}
	}
}
