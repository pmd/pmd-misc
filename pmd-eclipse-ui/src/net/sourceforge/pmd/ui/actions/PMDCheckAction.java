/*
 * Copyright (c) 2005,2006 PMD for Eclipse Development Team
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
package net.sourceforge.pmd.ui.actions;

import java.util.Iterator;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.runtime.PMDRuntimePlugin;
import net.sourceforge.pmd.runtime.cmd.ReviewCodeCmd;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Implements action on the "Check code with PMD" action menu on a file
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/05/22 21:23:56  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.14  2006/01/27 00:03:11  phherlin
 * Fix BUG#1365407 Problems with PMD in Eclipse/Issue 3
 * Revision 1.13 2005/12/30 16:22:31 phherlin Add a debug log to check what happens when this action
 * is launched in an editor pane
 * 
 * Revision 1.12 2005/10/24 22:39:00 phherlin Integrating Sebastian Raffel's work Refactor command processing Revision 1.11
 * 2005/05/07 13:32:06 phherlin Continuing refactoring Fix some PMD violations Fix Bug 1144793 Fix Bug 1190624 (at least try)
 * 
 * Revision 1.10 2003/11/30 22:57:37 phherlin Merging from eclipse-v2 development branch
 * 
 * Revision 1.8.2.2 2003/11/04 16:27:19 phherlin Refactor to use the adaptable framework instead of downcasting
 * 
 * Revision 1.8.2.1 2003/10/30 22:09:51 phherlin Simplify the code : moving the deep nested CountVisitor class as a first level
 * nested inner class. This also correct a rule violation from PMD.
 * 
 * Revision 1.8 2003/08/13 20:08:40 phherlin Refactoring private->protected to remove warning about non accessible member access in
 * enclosing types
 * 
 * Revision 1.7 2003/07/01 20:21:37 phherlin Correcting some PMD violations ! (empty if stmt)
 * 
 * Revision 1.6 2003/06/19 20:58:13 phherlin Improve progress indicator accuracy
 * 
 * Revision 1.5 2003/05/19 22:27:33 phherlin Refactoring to improve performance
 * 
 * Revision 1.4 2003/03/30 20:48:19 phherlin Adding logging Displaying error dialog in a thread safe way Adding support for folders
 * and package
 * 
 */
public class PMDCheckAction implements IObjectActionDelegate {
    private static final Logger log = Logger.getLogger(PMDCheckAction.class);
    private IWorkbenchPart targetPart;

    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(IAction)
     */
    public void run(IAction action) {
        log.info("Check PMD action requested");

        try {

            // Execute PMD on a range of selected resource if action selected from a view part
            if (this.targetPart instanceof IViewPart) {
                ISelection selection = this.targetPart.getSite().getSelectionProvider().getSelection();
                if (selection instanceof IStructuredSelection) {
                    reviewSelectedResources((IStructuredSelection) selection);
                } else {
                    log.debug("The selection is not an instance of IStructuredSelection. This is not supported: "
                            + selection.getClass().getName());
                }
            }

            // If action is selected from an editor, run PMD on the file currently edited
            else if (this.targetPart instanceof IEditorPart) {
                IEditorInput editorInput = ((IEditorPart) this.targetPart).getEditorInput();
                if (editorInput instanceof IFileEditorInput) {
                    reviewSingleResource(((IFileEditorInput) editorInput).getFile());
                } else {
                    log.debug("The kind of editor input is not supported. The editor input if of type: "
                            + editorInput.getClass().getName());
                }
            }

            // Else, this is not supported for now
            else {
                log.debug("Running PMD from this kind of part is not supported. Part is of type "
                        + this.targetPart.getClass().getName());
            }

        } catch (CommandException e) {
            PMDUiPlugin.getDefault().showError(PMDUiPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION), e);
        }

    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

    /**
     * Run the reviewCode command on a single resource
     * 
     * @param resource
     * @throws CommandException
     */
    private void reviewSingleResource(IResource resource) throws CommandException {
        ReviewCodeCmd cmd = new ReviewCodeCmd();
        cmd.addResource(resource);
        cmd.setStepsCount(1);
        cmd.setTaskMarker(true);
        cmd.setOpenPmdPerspective(PMDRuntimePlugin.getDefault().loadPreferences().isPmdPerspectiveEnabled());
        cmd.setUserInitiated(true);
        cmd.performExecute();
    }

    /**
     * Prepare and run the reviewCode command for all selected resources
     * 
     * @param selection the selected resources
     */
    private void reviewSelectedResources(IStructuredSelection selection) throws CommandException {
        ReviewCodeCmd cmd = new ReviewCodeCmd();

        // Add selected resources to the list of resources to be reviewed
        for (Iterator i = selection.iterator(); i.hasNext();) {
            Object element = i.next();

            if (element instanceof IAdaptable) {
                IAdaptable adaptable = (IAdaptable) element;
                IResource resource = (IResource) adaptable.getAdapter(IResource.class);
                if (resource != null) {
                    cmd.addResource(resource);
                } else {
                    log.warn("The selected object cannot adapt to a resource");
                    log.debug("   -> selected object : " + element);
                }
            } else {
                log.warn("The selected object is not adaptable");
                log.debug("   -> selected object : " + element);
            }
        }

        // Run the command
        cmd.setStepsCount(countElement(selection));
        cmd.setTaskMarker(true);
        cmd.setOpenPmdPerspective(PMDRuntimePlugin.getDefault().loadPreferences().isPmdPerspectiveEnabled());
        cmd.setUserInitiated(true);
        cmd.performExecute();

    }

    /**
     * Count the number of resources of a selection
     * 
     * @param selection a selection
     * @return the element count
     */
    private int countElement(IStructuredSelection selection) {
        CountVisitor visitor = new CountVisitor();

        for (Iterator i = selection.iterator(); i.hasNext();) {
            Object element = i.next();

            try {
                if (element instanceof IAdaptable) {
                    IAdaptable adaptable = (IAdaptable) element;
                    IResource resource = (IResource) adaptable.getAdapter(IResource.class);
                    if (resource != null) {
                        resource.accept(visitor);
                    } else {
                        log.warn("The selected object cannot adapt to a resource");
                        log.debug("   -> selected object : " + element);
                    }
                } else {
                    log.warn("The selected object is not adaptable");
                    log.debug("   -> selected object : " + element);
                }
            } catch (CoreException e) {
                // Ignore any exception
                PMDUiPlugin.getDefault().logError(
                        "Exception when counting the number of impacted elements when running PMD from menu", e);
            }
        }

        return visitor.count;
    }

    // Inner visitor to count number of childs of a resource
    private class CountVisitor implements IResourceVisitor {
        public int count = 0;

        public boolean visit(IResource resource) {
            boolean fVisitChildren = true;
            count++;

            if ((resource instanceof IFile) && (((IFile) resource).getFileExtension() != null)
                    && ((IFile) resource).getFileExtension().equals("java")) {

                fVisitChildren = false;
            }

            return fVisitChildren;
        }
    }
}
