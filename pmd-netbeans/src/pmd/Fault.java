/*
 *  Copyright (c) 2002, Ole-Martin M�rk
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 */
package pmd;

/**
 * Contains a fault that pmd discovers
 * @created 17.oktober 2002
 * @author Ole-Martin M�rk
 */
public class Fault implements Comparable {

	private int line;
	private String clazz;
	private String message;


	/**
	 * Creates a new instance of Fault
	 *
	 * @param line the line of the fault
	 * @param clazz the class of the fault
	 * @param message the pmd message
	 */
	public Fault( int line, String clazz, String message ) {
		this.line = line;
		this.message = message;
		this.clazz = clazz;
	}


	/**
	 * Compares <code>obj</code> to <code>this</code>. Sorts by linenumber
	 *
	 * @param object the other object
	 * @return this.linenumber - that.linenumber
	 */
	public int compareTo( Object object ) {
		int compared = 0;
		if( object instanceof Fault ) {
			compared = line - ( ( Fault )object ).line;
		}
		return compared;
	}


	/**
	 * Returns a string representation of this object
	 *
	 * @return the fault as a string @see#getFault()
	 */
	public String toString() {
		return getFault();
	}


	/**
	 * Returns the fault as listed in the output pane
	 *
	 * @return the fault as a string
	 */
	public String getFault() {
		return clazz + " [" + line + "]: " + message;
	}


	/**
	 * Parses the fault and returns the linenumber
	 *
	 * @param fault the fault
	 * @return the linenumber @see#getFault()
	 */
	public static int getLineNum( String fault ) {
		return Integer.parseInt( fault.substring( fault.indexOf( '[' ) + 1, fault.indexOf( ']' ) ) );
	}


	/**
	 * Parses the fault and returns the errormessage
	 *
	 * @param fault the output message
	 * @return The errormessage
	 */
	public static String getErrorMessage( String fault ) {
		return fault.substring( fault.indexOf( ":" ) + 2 );
	}
}
