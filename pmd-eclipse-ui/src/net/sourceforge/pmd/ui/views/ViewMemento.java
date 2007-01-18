/*
 * Created on 24 mai 2005
 *
 * Copyright (c) 2005, PMD for Eclipse Development Team
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

package net.sourceforge.pmd.ui.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

/**
 * Provides Functions to save the State of a View during a Session, even when the view is cloes and re-opened Saves the State in a
 * XML-File in the Plugins-Path (Standard: .metadata in the workspace)
 * 
 * @author SebastianRaffel ( 24.05.2005 ), Philippe Herlin, Sven Jacob
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.7  2007/01/18 21:03:56  phherlin
 * Fix several problems on memento usage
 *
 * Revision 1.6  2006/11/16 17:11:08  holobender
 * Some major changes:
 * - new CPD View
 * - changed and refactored ViolationOverview
 * - some minor changes to dataflowview to work with PMD
 *
 * Revision 1.5  2006/10/10 22:19:59  phherlin
 * Review Sebastian code... and fix most PMD warnings
 *
 * Revision 1.4  2006/10/10 21:43:20  phherlin
 * Review Sebastian code... and fix most PMD warnings
 *
 */
public class ViewMemento {
    final private IPath path; // NOPMD by Herlin on 11/10/06 00:15
    final private File file;
    private XMLMemento memento;

    protected final static String XML_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    protected final static String LIST_SEPARATOR = ":";
    protected final static String MEMENTO_PREFIX = "memento";
    protected final static String ATTRIBUTE_PREFIX = "attribute";
    protected final static String ATTR_NAME = "name";
    protected final static String ATTR_VALUE = "value";

    /**
     * Constructor Searches for the XML-File, where the Memento should be saved and creates it if there is none
     * 
     * @param type, a String identifying the View, used for the File's Name
     */
    public ViewMemento(String type) {
        this.path = PMDUiPlugin.getDefault().getStateLocation();
        this.file = new File(this.path.toString(), type);

        // we check for an existing XML-File
        // and create one, if needed
        if ((!this.file.exists()) || (!checkForXMLFile(this.file))) {
            createNewFile(this.file);
        }

        // then we create a ReadRoot for the Memento
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            this.memento = XMLMemento.createReadRoot(reader);
        } catch (WorkbenchException wbe) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_VIEW_EXCEPTION + this.toString(), wbe);
        } catch (FileNotFoundException fnfe) {
            PMDUiPlugin.getDefault().logError(
                    StringKeys.MSGKEY_ERROR_FILE_NOT_FOUND + path.toString() + "/" + type + " in " + this.toString(), fnfe);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) { // NOPMD by Herlin on 10/10/06 23:55
                    // Ignored
                }
            }
        }
        
        // Validate that memento has correctly been build
        if (this.memento == null) {
            throw new IllegalStateException("Memento has not been built correctly. Check error log for details");
        }
    }

    /**
     * Creates a new XML-Structure in a given File
     * 
     * @param file
     */
    protected final void createNewFile(File file) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(XML_PREFIX + "\n" + "<" + MEMENTO_PREFIX + "/>");
        } catch (IOException ioe) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_IO_EXCEPTION + this.toString(), ioe);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) { // NOPMD by Herlin on 10/10/06 23:55
                    // ignored
                }
            }
        }
    }

    /**
     * Checks for an XML-Structure in a File
     * 
     * @param file
     * @return true, if the File is a XML-File we can use, false otherwise
     */
    protected final boolean checkForXMLFile(File file) {
        boolean isXmlFile = false;
        BufferedReader contentReader = null;
        try {
            contentReader = new BufferedReader(new FileReader(file));

            while (contentReader.ready()) {
                final String line = contentReader.readLine();
                if (line.length() != 0) {
                    // the first Line of Text has to be the XML-Prefix
                    isXmlFile = XML_PREFIX.equalsIgnoreCase(line);
                    break;
                }
            }
        } catch (FileNotFoundException fnfe) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_FILE_NOT_FOUND + file.toString() + " in " + this.toString(),
                    fnfe);
        } catch (IOException ioe) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_IO_EXCEPTION + this.toString(), ioe);
        } finally {
            if (contentReader != null) {
                try {
                    contentReader.close();
                } catch (IOException e) { // NOPMD by Herlin on 10/10/06 23:57
                    // Ignored
                }
            }
        }

        return isXmlFile;
    }

    /**
     * Saves the Memento into the File
     * 
     * @param type
     */
    public void save(String type) {
        if (this.memento != null) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(this.file);
                this.memento.save(writer);
            } catch (IOException ioe) {
                PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_IO_EXCEPTION + this.toString(), ioe);
            } finally {
                if (writer == null) {
                    try {
                        writer.close();
                    } catch (IOException e) { // NOPMD by Herlin on 11/10/06 00:00
                        // Ignored
                    }
                }
            }
        }
    }

    /**
     * Returns a Mewmento with the given Attribute
     * 
     * @param name
     * @return a Memento
     */
    private IMemento getAttribute(String name) {
        final IMemento[] mementos = this.memento.getChildren(ATTRIBUTE_PREFIX);
        IMemento mem = null;

        for (int k = 0; k < mementos.length; k++) {
            final String attrName = mementos[k].getString(ATTR_NAME);
            if (name.equalsIgnoreCase(attrName)) {
                mem = mementos[k];
            }
        }

        if (mem == null) {
            mem = this.memento.createChild(ATTRIBUTE_PREFIX);
            mem.putString(ATTR_NAME, name);
        }

        return mem;
    }

    /**
     * Puts a String into a Memento
     * 
     * @param key
     * @param value
     */
    public void putString(String key, String value) {
        final IMemento mem = getAttribute(key);
        mem.putString(ATTR_VALUE, value);
    }

    /**
     * Puts an Integer into a Memento
     * 
     * @param key
     * @param value
     */
    public void putInteger(String key, int value) {
        final IMemento mem = getAttribute(key);
        mem.putInteger(ATTR_VALUE, value);
    }

    /**
     * Puts a Float into a Memento
     * 
     * @param key
     * @param value
     */
    public void putFloat(String key, float value) {
        final IMemento mem = getAttribute(key);
        mem.putFloat(ATTR_VALUE, value);
    }

    /**
     * puts an ArrayList into a Memento, the List is changed into a seperated String
     * 
     * @param key
     * @param valueList
     */
    public void putList(String key, List valueList) {
        final StringBuffer valueString = new StringBuffer();
        for (int k = 0; k < valueList.size(); k++) {
            if (k > 0) {
                valueString.append(LIST_SEPARATOR);
            }
            valueString.append(valueList.get(k));
        }

        putString(key, valueString.toString());
    }

    /**
     * Gets a String from a Memento
     * 
     * @param key
     * @return a String with the Value
     */
    public String getString(String key) {
        final IMemento mem = getAttribute(key);
        return mem.getString(ATTR_VALUE);
    }

    /**
     * Gets an Integer From a Memento
     * 
     * @param key
     * @return an Integer with the Value
     */
    public Integer getInteger(String key) {
        final IMemento mem = getAttribute(key);
        return mem.getInteger(ATTR_VALUE);
    }

    /**
     * Returns a Float from a Memento
     * 
     * @param key
     * @return a Float with the Value
     */
    public Float getFloat(String key) {
        final IMemento mem = getAttribute(key);
        return mem.getFloat(ATTR_VALUE);
    }

    /**
     * Returns an ArrayList of Integers from a Memento
     * 
     * @param key
     * @return ArrayList of Integer-Values
     */
    public List getIntegerList(String key) {
        final ArrayList valuelist = new ArrayList();
        final String valueString = getString(key);
        if (valueString != null) {
            final String[] objects = valueString.split(LIST_SEPARATOR);
            for (int k = 0; k < objects.length; k++) {
                if ((objects[k].trim().length() == 0) || ("null".equals(objects[k]))) {
                    valuelist.add(new Integer(0)); // NOPMD by Herlin on 11/10/06 00:13
                } else {
                    valuelist.add(new Integer(objects[k])); // NOPMD by Herlin on 11/10/06 00:14
                }
            }
        }
        return valuelist;
    }

    /**
     * Returns an ArrayList of Strings from a Memento
     * 
     * @param key
     * @return a ArrayList of String values
     */
    public List getStringList(String key) {
        List valuelist = Collections.EMPTY_LIST;
        final String valueString = getString(key);
        if (valueString != null) {
            valuelist = new ArrayList(Arrays.asList(valueString.split(LIST_SEPARATOR)));
        }

        return valuelist;
    }
}
