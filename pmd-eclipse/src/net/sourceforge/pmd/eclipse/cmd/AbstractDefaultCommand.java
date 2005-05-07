/*
 * Created on 20 nov. 2004
 *
 * Copyright (c) 2004, PMD for Eclipse Development Team
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
package net.sourceforge.pmd.eclipse.cmd;

import org.eclipse.core.runtime.IProgressMonitor;

import name.herlin.command.CommandException;
import name.herlin.command.AbstractProcessableCommand;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDPluginConstants;

/**
 * This is a base implementation for a command inside the PMD plugin.
 * This must be used as a root implementation for all the plugin commands.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2005/05/07 13:32:04  phherlin
 * Continuing refactoring
 * Fix some PMD violations
 * Fix Bug 1144793
 * Fix Bug 1190624 (at least try)
 *
 * Revision 1.2  2004/12/03 00:22:42  phherlin
 * Continuing the refactoring experiment.
 * Implement the Command framework.
 * Refine the MVC pattern usage.
 *
 * Revision 1.1  2004/11/21 21:39:45  phherlin
 * Applying Command and CommandProcessor patterns
 *
 *
 */
public abstract class AbstractDefaultCommand extends AbstractProcessableCommand implements PMDConstants, PMDPluginConstants {
    private boolean readOnly;
    private boolean outputProperties;
    private boolean readyToExecute;
    private String description;
    private String name;
    private IProgressMonitor monitor;
    private int stepsCount;

    /**
     * @return Returns the readOnly.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @param readOnly The readOnly to set.
     */
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param outputProperties The outputProperties to set.
     */
    public void setOutputProperties(final boolean outputProperties) {
        this.outputProperties = outputProperties;
    }

    /**
     * @return Returns the outputProperties.
     */
    public boolean hasOutputProperties() {
        return outputProperties;
    }

    /**
     * @return Returns the readyToExecute.
     */
    public boolean isReadyToExecute() {
        return readyToExecute;
    }

    /**
     * @param readyToExecute The readyToExecute to set.
     */
    public void setReadyToExecute(final boolean readyToExecute) {
        this.readyToExecute = readyToExecute;
    }

    /**
     * @return Returns the number of steps for that command
     */
    public int getStepsCount() {
        return stepsCount;
    }
    
    /**
     * @param stepsCount The number of steps for that command
     */
    public void setStepsCount(final int stepsCount) {
        this.stepsCount = stepsCount;
    }
    
    /**
     * @return Returns the monitor.
     */
    public IProgressMonitor getMonitor() {
        return this.monitor;
    }
    
    /**
     * @param monitor The monitor to set.
     */
    public void setMonitor(final IProgressMonitor monitor) {
        this.monitor = monitor;
    }
    
    /**
     * @see name.herlin.command.AbstractProcessableCommand#execute()
     */
    public abstract void execute() throws CommandException;

    /**
     * @see name.herlin.command.Command#reset()
     */
    public abstract void reset();

    /**
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    protected String getMessage(final String key) {
        return PMDPlugin.getDefault().getMessage(key);
    }
    
    /**
     * delegate method for monitor.beginTask
     * @see org.eclipse.core.runtime.IProgressMonitor#beginTask
     */
    protected void beginTask(final String name, final int totalWork) {
        if (this.monitor != null) {
            this.monitor.beginTask(name, totalWork);
        }
    }
    
    /**
     * delegate method to monitor.done()
     * @see org.eclipse.core.runtime.IProgressMonitor#done
     */
    protected void done() {
        if (this.monitor != null) {
            this.monitor.done();
        }
    }
    
    /**
     * deletegate method for monitor.isCanceled()
     * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled
     */
    protected boolean isCanceled() {
        return this.monitor == null ? false : this.monitor.isCanceled();
    }
    
    /**
     * delegate method for monitor.setTaskName()
     * @see org.eclipse.core.runtime.IProgressMonitor#setTaskName
     */
    protected void setTaskName(final String name) {
        if (this.monitor != null) {
            this.monitor.setTaskName(name);
        }
    }
    
    /**
     * delegate method for monitor.subTask()
     * @see org.eclipse.core.runtime.IProgressMonitor#subTask
     */
    protected void subTask(final String name) {
        if (this.monitor != null) {
            this.monitor.subTask(name);
        }
    }
    
    /**
     * delegate method for monitor.worked()
     * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled
     */
    protected void worked(final int work) {
        if (this.monitor != null) {
            this.monitor.worked(work);
        }
    }
}