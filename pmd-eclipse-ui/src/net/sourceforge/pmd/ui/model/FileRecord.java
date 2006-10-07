/*
 * Created on 7 mai 2005
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.ui.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.pmd.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * AbstractPMDRecord for Files
 * 
 * @author SebastianRaffel ( 16.05.2005 ), Philippe Herlin, Sven Jacob
 * @version $$Revision$$
 * 
 * $$Log$
 * $Revision 1.4  2006/10/07 16:01:21  phherlin
 * $Integrate Sven updates
 * $$
 * 
 */
public class FileRecord extends AbstractPMDRecord {
    private IResource resource;
    private PackageRecord parent;
    private int numberOfLOC;
    private int numberOfMethods;

    /**
     * Constructor (not for use with the Model, no PackageRecord is provided here)
     * 
     * @param javaResource the given File
     */
    public FileRecord(IResource javaResource) {
        this(javaResource, null);
    }

    /**
     * Constructor (for use with the Model)
     * 
     * @param javaResource
     * @param record
     */
    public FileRecord(IResource javaResource, PackageRecord record) {
        super();
        
        if (javaResource == null) {
            throw new IllegalArgumentException("javaResouce cannot be null");
        }
        
        this.resource = javaResource;
        this.parent = record;
        this.numberOfLOC = 0;
        this.numberOfMethods = 0;
    }

    /**
     *  @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getParent()
     */
    public AbstractPMDRecord getParent() {
        return this.parent;
    }

    /**
     * @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getChildren()
     */
    public AbstractPMDRecord[] getChildren() {
        return new AbstractPMDRecord[0]; // getChildren should never return null!
    }

    /**
     * @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getResource()
     */
    public IResource getResource() {
        return this.resource;
    }

    /**
     * @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#createChildren()
     */
    protected AbstractPMDRecord[] createChildren() {
        return new AbstractPMDRecord[0]; // no children so return an empty array, not null!
    }

    /**
     * Checks the File for PMD-Markers
     * 
     * @return true if the File has PMD-Markers, false otherwise
     */
    public boolean hasMarkers() {
        final IMarker[] markers = findMarkers();
        return ((markers != null) && (markers.length > 0));
    }

    /**
     * Finds PMD-Markers in the File
     * 
     * @return an Array of markers
     */
    public IMarker[] findMarkers() {
        IMarker[] markers = new IMarker[0]; // to avoid returning null
        try {
            // this is the overwritten Function from AbstractPMDRecord
            // we simply call the IResource-function to find Markers
            if (this.resource.isAccessible()) {
                markers = this.resource.findMarkers(PMDRuntimeConstants.PMD_MARKER, true, IResource.DEPTH_INFINITE);
            }
        } catch (CoreException ce) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_FIND_MARKER + this.toString(), ce);
        }

        return markers;
    }
    
    /**
     * Finds PMD PDFA Markers in the File
     * 
     * @return an Array of markers
     */
    public IMarker[] findDFAMarkers() {
        IMarker[] markers = new IMarker[0];
        try {
            // we can only find Markers for a file
            // we use the DFA-Marker-ID set for Dataflow Anomalies
            if (this.resource.isAccessible()) {
                markers = this.resource.findMarkers(PMDRuntimeConstants.PMD_DFA_MARKER, true, IResource.DEPTH_INFINITE);
            }
        } catch (CoreException ce) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_FIND_MARKER + this.toString(), ce);
        }
        
        return markers;
    }
    
    /**
     * Finds Markers, that have a given Attribute with a given Value
     * 
     * @param attributeName
     * @param value
     * @return an Array of markers matching these Attribute and Value
     */
    public IMarker[] findMarkersByAttribute(String attributeName, Object value) {
        final IMarker[] markers = findMarkers();
        final List attributeMarkers = new ArrayList();
        try {
            // we get all Markers and cath the ones that matches our criteria
            for (int i = 0; i < markers.length; i++) {
                final Object val = markers[i].getAttribute(attributeName);

                // if the value is null, the Attribute doesn't exist
                if ((val != null) && (val.equals(value))) {
                    attributeMarkers.add(markers[i]);
                }
            }
        } catch (CoreException ce) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_FIND_MARKER + this.toString(), ce);
        }

        // return an Array of the Markers
        return (IMarker[]) attributeMarkers.toArray(new IMarker[attributeMarkers.size()]);
    }

    /**
     * Calculates the Number of Code-Lines this File has.
     * The Function is adapted from the Eclipse Metrics-Plugin available at:
     * http://www.sourceforge.net/projects/metrics
     * 
     * @return the Lines of Code
     */
    public void calculateLinesOfCode() {
        if (this.resource.isAccessible()) {

            // the whole while has to be a String for this operation
            // so we read the File line-wise into a String
            int loc = 0;
            final String source = resourceToString(this.resource);
            final int firstCurly = source.indexOf('{');
            if (firstCurly != -1) {
                final String body = source.substring(firstCurly+1, source.length()-1).trim();
                final StringTokenizer lines = new StringTokenizer(body, "\n");
                while(lines.hasMoreTokens()) {
                    String trimmed = lines.nextToken().trim();
                    if ((trimmed.length() > 0) && (trimmed.startsWith("/*"))) {
                        while (trimmed.indexOf("*/") == -1) {
                            trimmed = lines.nextToken().trim();
                        }
                        if (lines.hasMoreTokens()) {
                            trimmed = lines.nextToken().trim();
                        }
                    }
                    
                    if (!trimmed.startsWith("//")) {
                        loc++;                          
                    }
                }
            }
            
            this.numberOfLOC = loc;
        }
    }
    
    /**
     * Gets the Number of Code-Lines this File has.
     * 
     * @return the Lines of Code
     */
    public int getLinesOfCode() {
        return this.resource instanceof IFile ? this.numberOfLOC : 0; // deactivate this method for now
    }

    /**
     * Reads a Resource's File and return the Code as String.
     * 
     * @param resource a resource to read ; the resource must be accessible.
     * @return a String which is the Files Content
     */
    protected String resourceToString(IResource resource) {
        final StringBuffer fileContents = new StringBuffer();
        BufferedReader bReader = null;
        try {
            // we create a FileReader
            bReader = new BufferedReader(new FileReader(resource.getRawLocation().toFile()));

            // ... and read the File line by line
            while (bReader.ready()) {
                fileContents.append(bReader.readLine()).append('\n');
            }
        } catch (FileNotFoundException fnfe) {
            PMDUiPlugin.getDefault().logError(
                    StringKeys.MSGKEY_ERROR_FILE_NOT_FOUND + resource.toString() + " in " + this.toString(), fnfe);
        } catch (IOException ioe) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_IO_EXCEPTION + this.toString(), ioe);
        } finally {
            if (bReader != null) {
                try {
                    bReader.close();
                } catch (IOException e) { // NOPMD by Herlin on 07/10/06 17:47
                    // ignore
                }
            }
        }

        return fileContents.toString();
    }

    /**
     * Calculate the number of methods.
     */
    public void calculateNumberOfMethods() {
        if (this.resource.isAccessible()) {

            // we need to change the Resource into a Java-File
            final IJavaElement element = JavaCore.create(this.resource);
            final List methods = new ArrayList();
                 
            if (element instanceof ICompilationUnit) {
                try {
                    // ITypes can be Package Declarations or other Java Stuff too
                    final IType[] types = ((ICompilationUnit) element).getTypes();
                    for (int i=0; i<types.length; i++) {
                        if (types[i] instanceof IType) {
                            // only if it is an IType itself, it's a Class
                            // from which we can get it's Methods
                            methods.addAll( Arrays.asList(
                                    ((IType) types[i]).getMethods() ));
                        }
                    }
                } catch (JavaModelException jme) {
                    PMDUiPlugin.getDefault().logError(
                            StringKeys.MSGKEY_ERROR_JAVAMODEL_EXCEPTION + this.toString(), jme);
                }
            }
            if (!methods.isEmpty()) {
                this.numberOfMethods = methods.size();
            }
        }
    }
    
    /**
     * Gets the Number of Methods, this class contains.
     * 
     * @return the Number of Methods
     */
    public int getNumberOfMethods() {
        return numberOfMethods; // deactivate this method for now
    }

    /* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#addResource(org.eclipse.core.resources.IResource) */
    public AbstractPMDRecord addResource(IResource resource) {
        return null;
    }

    /* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#removeResource(org.eclipse.core.resources.IResource) */
    public AbstractPMDRecord removeResource(IResource resource) {
        return null;
    }

    /* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getName() */
    public String getName() {
        return resource.getName();
    }

    /* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getResourceType() */
    public int getResourceType() {
        return AbstractPMDRecord.TYPE_FILE;
    }
}
