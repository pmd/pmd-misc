/*
 *  Copyright (c) 2002-2003, the pmd-netbeans team
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleViolation;

import org.netbeans.api.java.classpath.ClassPath;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.cookies.SourceCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.TopComponent;

import pmd.config.ConfigUtils;
import pmd.scan.EditorChangeListener;

/**
 * Action that can always be invoked and work procedurally.
 *
 * @author Ole-Martin M�rk
 * @created 17. oktober 2002
 */
public class RunPMDAction extends CookieAction {
	
	/**
	 * Overridden to log that the action is being initialized, and to register an editor change listener for
	 * scanning.
	 */
	protected void initialize() {
		ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "Initializing RunPMDAction");
		super.initialize();
		TopComponent.getRegistry().addPropertyChangeListener( new EditorChangeListener(TopComponent.getRegistry()) );
	}
	/**
	 * Gets the name of this action
	 *
	 * @return the name of this action
	 */
	public String getName() {
		return NbBundle.getMessage( RunPMDAction.class, "LBL_Action" );
	}


	/**
	 * Gets the filename of the icon associated with this action
	 *
	 * @return the name of the icon
	 */
	protected String iconResource() {
		return "pmd/resources/PMDOptionsSettingsIcon.gif";
	}


	/**
	 * Returns default help
	 *
	 * @return HelpCtx.DEFAULT_HELP
	 */
	public HelpCtx getHelpCtx() {
		return HelpCtx.DEFAULT_HELP;
	}


	/**
	 * Returns the cookies that can use this action
	 *
	 * @return an array of the two elements DataFolder.class and SourceCookie.class
	 */
	protected Class[] cookieClasses() {
		return new Class[]{DataFolder.class, SourceCookie.class};
	}


	/**
	 * Returns the mode of this action
	 *
	 * @return the mode of this action
	 * @see org.openide.util.actions.CookieAction#MODE_ALL
	 */
	protected int mode() {
		return MODE_ALL;
	}


	/**
	 * Runs PMD on the given list of DataObjects, with no callback.
	 * This just calls {@link #checkCookies(List, RunPMDCallback)} with a default callback that displays
	 * progress in the status bar.
	 *
	 * @param dataobjects the list of data objects to run PMD on, not null. Elements are instanceof
	 *                    {@link DataObject}.
	 * @return the list of rule violations found in the run, not null. Elements are instanceof {@link Fault}.
	 * @throws IOException on failure to read one of the files or to write to the output window.
	 */
	public static List checkCookies( List dataobjects ) throws IOException {
		return checkCookies(dataobjects, new DefaultCallback());
	}
	
	/**
	 * Runs PMD on the given list of DataObjects, interacting with the given callback.
	 *
	 * @param dataobjects the list of data objects to run PMD on, not null. Elements are instanceof
	 *                    {@link DataObject}.
	 * @param callback the callback to interact with. Receives notifications and can stop the run.
	 * @return the list of rule violations found in the run, not null. Elements are instanceof {@link Fault}.
	 * @throws IOException on failure to read one of the files or to write to the output window.
	 */
	public static List checkCookies( List dataobjects, RunPMDCallback callback ) throws IOException {
		RuleSet set = constructRuleSets();
		PMD pmd = new PMD();
		ArrayList list = new ArrayList( 100 );
		callback.pmdStart( dataobjects.size() );
		for( int i = 0; i < dataobjects.size(); i++ ) {
			boolean keepGoing = callback.pmdProgress( i + 1 );
			if(!keepGoing) {
				break;
			}
			DataObject dataobject = ( DataObject )dataobjects.get( i );
			FileObject fobj = dataobject.getPrimaryFile();
			String name = ClassPath.getClassPath( fobj, ClassPath.COMPILE ).getResourceName( fobj, '.', false );
			
			//The file is not a java file
			if( !dataobject.getPrimaryFile().hasExt( "java" ) || dataobject.getCookie( LineCookie.class ) == null ) {
				continue;
			}
			Reader reader;
			try {
				reader = getSourceReader( dataobject );
			}
			catch( IOException ioe) {
				Fault fault = new Fault( 1, name, "IOException reading file for class " + name + ": " + ioe.toString());
				ErrorManager.getDefault().notify( ioe );
				list.add( fault );
				FaultRegistry.getInstance().registerFault( fault, dataobject );
				continue;
			}
			
			RuleContext ctx = new RuleContext();
			Report report = new Report();
			ctx.setReport( report );
			ctx.setSourceCodeFilename( name );
			try {
				pmd.processFile( reader, set, ctx );
			}
			catch( PMDException e ) {
				Fault fault = new Fault( 1, name, e );
				ErrorManager.getDefault().notify( e );
				list.add( fault );
				FaultRegistry.getInstance().registerFault( fault, dataobject );
			}

			Iterator iterator = ctx.getReport().iterator();
			while( iterator.hasNext() ) {
				RuleViolation violation = ( RuleViolation )iterator.next();
				StringBuffer buffer = new StringBuffer();
				buffer.append( violation.getRule().getName() ).append( ", " );
				buffer.append( violation.getDescription() );
				Fault fault = new Fault( violation.getLine(),
					violation.getFilename(),
					buffer.toString() );
				list.add( fault );
				FaultRegistry.getInstance().registerFault( fault, dataobject );
			}
		}
		callback.pmdEnd();
		Collections.sort( list );
		return list;
	}


	/**
	 * Performs the action this action is set up to do on the specified nodes
	 *
	 * @param node the nodes that the action is involved on
	 */
	protected void performAction( Node[] node ) {
		PMDOutputListener listener = PMDOutputListener.getInstance();
		listener.detach();
		FaultRegistry.getInstance().clearRegistry();
		ProgressDialog progressDlg = null;
		try {
			StatusDisplayer.getDefault().setStatusText( "PMD checking for rule violations" );
			List list = getDataObjects( node );
			progressDlg = new ProgressDialog();
			List violations = checkCookies( list, progressDlg );
			progressDlg = null;
			IOProvider ioProvider = (IOProvider)Lookup.getDefault().lookup( IOProvider.class );
			InputOutput io = ioProvider.getIO( "PMD output", false );
			if( violations.isEmpty() ) {
				StatusDisplayer.getDefault().setStatusText( "PMD found no rule violations" );
				io.closeInputOutput();
			}
			else {
				io.select();
				io.getOut().reset();
				for( int i = 0; i < violations.size(); i++ ) {
					Fault fault = (Fault)violations.get( i );
					if( fault.getLine() == -1 ) {
						io.getOut().println( String.valueOf( fault ) );
					}
					else {
						io.getOut().println( String.valueOf( fault ), listener );
					}
				}
				StatusDisplayer.getDefault().setStatusText( "PMD found rule violations" );
			}

		}
		catch( IOException e ) {
			ErrorManager.getDefault().notify( e );
			if(progressDlg != null) {
				progressDlg.pmdEnd();
			}
		}
	}


	/**
	 * Constructs the ruleset.
	 *
	 * @return the constructed ruleset
	 * @see pmd.config.PMDOptionsSettings#getRulesets()
	 */
	private static RuleSet constructRuleSets() {
		RuleSet rules = new RuleSet();
		List list = ConfigUtils.getRuleList();
		Iterator iterator = list.iterator();
		while( iterator.hasNext() ) {
			rules.addRule( ( Rule )iterator.next() );
		}
		return rules;
	}


	/**
	 * Get the reader for the specified dataobject
	 *
	 * @param dataobject the dataobject to read
	 * @return a reader for the dataobject
	 * @exception IOException if the object can't be read
	 */
	private static Reader getSourceReader( DataObject dataobject ) throws IOException {
		Reader reader;
		EditorCookie editor = ( EditorCookie )dataobject.getCookie( EditorCookie.class );

		//If it's the currently open document that's being checked
		if( editor != null && editor.getOpenedPanes() != null ) {
			String text = editor.getOpenedPanes()[0].getText();
			reader = new StringReader( text );
		}
		else {
			Iterator iterator = dataobject.files().iterator();
			FileObject file = ( FileObject )iterator.next();
			reader = new BufferedReader( new InputStreamReader( file.getInputStream() ) );
		}
		return reader;
	}


	/**
	 * Gets the dataObjects attribute of the RunPMDAction object
	 *
	 * @param node Description of the Parameter
	 * @return The dataObjects value
	 */
	private List getDataObjects( Node[] node ) {
		ArrayList list = new ArrayList();
		for( int i = 0; i < node.length; i++ ) {
			DataObject data = (DataObject)node[i].getCookie( DataObject.class );
			
			//Checks to see if it's a java source file
			if( data.getPrimaryFile().hasExt( "java" ) ) {
				list.add( data );
			}
			//Or if it's a folder
			else {
				DataFolder folder = ( DataFolder )node[i].getCookie( DataFolder.class );
				Enumeration enumeration = folder.children( true );
				while( enumeration.hasMoreElements() ) {
					DataObject dataobject = ( DataObject )enumeration.nextElement();
					if( dataobject.getPrimaryFile().hasExt( "java" ) ) {
						list.add( dataobject );
					}
				}
			}
		}
		return list;
	}
	
	/**
	 * Default callback implementation, to use when no callback is provided to <code>checkCookies</code>.
	 * Writes progress information into the StatusDisplayer (generally the status bar). Use a separate
	 * instance of this for each run, as it stores state (the total number of files).
	 */
	private static class DefaultCallback implements RunPMDCallback {
		
		/**
		 * This implementation is a no-op.
		 */
		public void pmdEnd() {
			// NO-OP
		}
		
		/**
		 * This implementation reports progress in the status bar and returns true.
		 *
		 * @param index index of the file on which PMD execution is starting. Greater than 0,
		 *              less than or equal to the number of files reported in {@link #pmdStart}.
		 * @return true
		 */
		public boolean pmdProgress(int index) {
			StatusDisplayer.getDefault().setStatusText(
				"PMD checking for rule violations in file " + ( index + 1 ) + "/" + numFiles );
			return true;
		}
		
		/**
		 * This implementation stores the number of files.
		 *
		 * @param numFiles the number of files to be scanned, greater than 0.
		 */
		public void pmdStart(int numFiles) {
			this.numFiles = numFiles;
		}
		
		private int numFiles;
		
	}
}
