/*
 * Created on 12 avr. 2005
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
package net.sourceforge.pmd.runtime.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import name.herlin.command.CommandException;
import name.herlin.command.Timer;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.runtime.PMDRuntimePlugin;
import net.sourceforge.pmd.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.runtime.properties.PropertiesException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * This command executes the PMD engine on a specified resource
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.5  2006/10/06 18:42:59  phherlin
 * Fix 1554639 Clear markers in Project JAVA-files and other bugs related to dataflows
 *
 * Revision 1.4  2006/06/26 21:23:08  phherlin
 * Fix IllegalArgumentException issue when checking projects where root folder is source folder.
 *
 * Revision 1.3  2006/06/20 20:48:24  phherlin
 * Fix the issue where the whole project is processed instead of a single file.
 * Revision 1.2 2006/06/05 22:25:21 phherlin Use the 3.0 SchedulingRule
 * API to be compatible with Rational Products
 * 
 * Revision 1.1 2006/05/22 21:37:35 phherlin Refactor the plug-in architecture
 * to better support future evolutions Revision 1.11 2006/04/26 21:15:02
 * phherlin Add the include derived files option
 * 
 * Revision 1.10 2006/04/24 20:55:15 phherlin Batch markers update to try to
 * gain performance
 * 
 * Revision 1.9 2006/04/24 19:35:01 phherlin Add performance mesures on commands
 * and on pmd execution Revision 1.8 2006/04/10 20:55:31 phherlin Update to PMD
 * 3.6
 * 
 * Revision 1.7 2005/12/30 16:24:01 phherlin Adding a null resource is illegal.
 * Throw an IllegalArgumentException.
 * 
 * Revision 1.6 2005/10/24 22:40:54 phherlin Refactor command processing
 * 
 * Revision 1.5 2005/06/30 23:24:19 phherlin Add the JDK 1.5 support
 * 
 * Revision 1.4 2005/06/07 18:38:14 phherlin Move classes to limit packages
 * cycle dependencies
 * 
 * Revision 1.3 2005/05/31 20:44:41 phherlin Continuing refactoring
 * 
 * Revision 1.2 2005/05/10 21:49:18 phherlin Fix new violations detected by PMD
 * 3.1
 * 
 * Revision 1.1 2005/05/07 13:32:04 phherlin Continuing refactoring Fix some PMD
 * violations Fix Bug 1144793 Fix Bug 1190624 (at least try)
 * 
 * 
 */
public class ReviewCodeCmd extends AbstractDefaultCommand {
    private static final Logger log = Logger.getLogger(ReviewCodeCmd.class);
    final private List resources = new ArrayList();
    private IResourceDelta resourceDelta;
    private Map markers = new HashMap();
    private boolean taskMarker = false;
    private boolean openPmdPerspective = false;
    private int rulesCount;
    private int filesCount;
    private long pmdDuration;

    /**
     * Default constructor
     */
    public ReviewCodeCmd() {
        super();
        this.setDescription("Run PMD on a list of workbench resources.");
        this.setName("ReviewCode");
        this.setOutputProperties(true);
        this.setReadOnly(true);
        this.setTerminated(false);
    }

    /**
     * @see name.herlin.command.AbstractProcessableCommand#execute()
     */
    public void execute() throws CommandException {
        log.info("ReviewCode command starting.");
        try {
            this.filesCount = 0;
            this.rulesCount = 0;
            this.pmdDuration = 0;

            // Lancer PMD
            if (this.resources.size() == 0) {
                beginTask("PMD Checking...", getStepsCount());
                processResourceDelta();
            } else {
                beginTask("PMD Checking...", getStepsCount());
                processResources();
            }

            // Appliquer les marqueurs
            final IWorkspaceRunnable action = new IWorkspaceRunnable() {
                public void run(IProgressMonitor monitor) throws CoreException {
                    applyMarkers();
                }
            };

            final IWorkspace workspace = ResourcesPlugin.getWorkspace();
            workspace.run(action, getschedulingRule(), IWorkspace.AVOID_UPDATE, getMonitor());

            // Switch to the PMD perspective if required
            if (this.openPmdPerspective) {
                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        switchToPmdPerspective();
                    }
                });
            }

        } catch (CoreException e) {
            throw new CommandException("Core exception when reviewing code", e);
        } finally {
            log.info("ReviewCode command has ended.");
            setTerminated(true);
            done();

            // Log performances information
            if (this.filesCount > 0) {
                PMDRuntimePlugin.getDefault().logInformation(
                        "Review code command terminated. " + this.rulesCount + " rules were executed against " + this.filesCount
                                + " files. Actual PMD duration is about " + this.pmdDuration + "ms, that is about "
                                + (this.pmdDuration / ((long) this.filesCount * (long) this.rulesCount)));
            } else {
                PMDRuntimePlugin.getDefault().logInformation(
                        "Review code command terminated. " + this.rulesCount + " rules were executed against " + this.filesCount
                                + " files. PMD was not executed.");
            }
        }
    }

    /**
     * @return Returns the file markers
     */
    public Map getMarkers() {
        return this.markers;
    }

    /**
     * @param resource The resource to set.
     */
    public void setResources(final List resources) {
        this.resources.clear();
        this.resources.addAll(resources);
    }

    /**
     * Add a resource to the list of resources to be reviewed.
     * 
     * @param resource a workbench resource
     */
    public void addResource(final IResource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource parameter can not be null");
        }

        this.resources.add(resource);
    }

    /**
     * @param resourceDelta The resourceDelta to set.
     */
    public void setResourceDelta(final IResourceDelta resourceDelta) {
        this.resourceDelta = resourceDelta;
    }

    /**
     * @param taskMarker The taskMarker to set.
     */
    public void setTaskMarker(final boolean taskMarker) {
        this.taskMarker = taskMarker;
    }

    /**
     * @param openPmdPerspective Tell whether the PMD perspective should be
     *            opened after processing.
     */
    public void setOpenPmdPerspective(boolean openPmdPerspective) {
        this.openPmdPerspective = openPmdPerspective;
    }

    /**
     * @see name.herlin.command.Command#reset()
     */
    public void reset() {
        this.resources.clear();
        this.markers = new HashMap();
        this.setTerminated(false);
        this.openPmdPerspective = false;
    }

    /**
     * @see name.herlin.command.Command#isReadyToExecute()
     */
    public boolean isReadyToExecute() {
        return (this.resources.size() != 0) || (this.resourceDelta != null);
    }

    /**
     * @return the scheduling rule needed to apply markers
     */
    private ISchedulingRule getschedulingRule() {
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IResourceRuleFactory ruleFactory = workspace.getRuleFactory();
        ISchedulingRule rule = null;

        if (this.resources.size() == 0) {
            rule = ruleFactory.markerRule(this.resourceDelta.getResource().getProject());
        } else {
            ISchedulingRule rules[] = new ISchedulingRule[this.resources.size()];
            for (int i = 0; i < rules.length; i++) {
                rules[i] = ruleFactory.markerRule((IResource) this.resources.get(i));
            }
            rule = new MultiRule((ISchedulingRule[]) this.resources.toArray(rules));
        }

        return rule;
    }

    /**
     * Return a PMD Engine for that project. The engine is parameterized
     * according to the target JDK of that project.
     * 
     * @param project
     * @return
     */
    private PMD getPmdEngineForProject(final IProject project) throws CommandException {
        final IJavaProject javaProject = JavaCore.create(project);
        final PMD pmdEngine = new PMD();

        if (javaProject.exists()) {
            final String compilerCompliance = javaProject.getOption(JavaCore.COMPILER_COMPLIANCE, true);
            log.debug("compilerCompliance = " + compilerCompliance);
            if (JavaCore.VERSION_1_3.equals(compilerCompliance)) {
                pmdEngine.setJavaVersion(SourceType.JAVA_13);
            } else if (JavaCore.VERSION_1_4.equals(compilerCompliance)) {
                pmdEngine.setJavaVersion(SourceType.JAVA_14);
            } else if (JavaCore.VERSION_1_5.equals(compilerCompliance)) {
                pmdEngine.setJavaVersion(SourceType.JAVA_15);
            } else {
                throw new CommandException("The target JDK, " + compilerCompliance + " is not yet supported"); // TODO:
                // NLS
            }
        } else {
            throw new CommandException("The project " + project.getName() + " is not a Java project"); // TODO:
            // NLS
        }
        return pmdEngine;
    }

    /**
     * Process the list of workbench resources
     * 
     * @throws CommandException
     */
    private void processResources() throws CommandException {
        final Iterator i = this.resources.iterator();
        while (i.hasNext()) {
            final IResource resource = (IResource) i.next();

            // if resource is a project, visit only its source folders
            if (resource instanceof IProject) {
                processProject((IProject) resource);
            } else {
                processResource(resource);
            }
        }
    }

    /**
     * Review a single resource
     */
    private void processResource(IResource resource) throws CommandException {
        try {
            final IProject project = resource.getProject();
            final IProjectProperties properties = PMDRuntimePlugin.getDefault().loadProjectProperties(project);
            final RuleSet ruleSet = properties.getProjectRuleSet();
            final PMD pmdEngine = getPmdEngineForProject(project);
            setStepsCount(countResourceElement(resource));
            log.debug("Visiting resource " + resource.getName() + " : " + getStepsCount());

            final ResourceVisitor visitor = new ResourceVisitor();
            visitor.setMonitor(this.getMonitor());
            visitor.setRuleSet(ruleSet);
            visitor.setPmdEngine(pmdEngine);
            visitor.setAccumulator(this.markers);
            visitor.setUseTaskMarker(this.taskMarker);
            visitor.setProjectProperties(properties);
            resource.accept(visitor);

            this.rulesCount = ruleSet.getRules().size();
            this.filesCount += visitor.getProcessedFilesCount();
            this.pmdDuration += visitor.getActualPmdDuration();

        } catch (PropertiesException e) {
            throw new CommandException(e);
        } catch (CoreException e) {
            throw new CommandException(e);
        }
    }

    /**
     * Review an entire project
     */
    private void processProject(IProject project) throws CommandException {
        try {
            setStepsCount(countResourceElement(project));
            log.debug("Visiting  project " + project.getName() + " : " + getStepsCount());

            final IJavaProject javaProject = JavaCore.create(project);
            final IClasspathEntry[] entries = javaProject.getRawClasspath();
            for (int i = 0; i < entries.length; i++) {
                if (entries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                    
                    // phherlin note: this code is ugly but I don't how to do otherwise.
                    // The IWorkspaceRoot getContainerLocation(IPath) always return null.
                    // Catching the IllegalArgumentException on getFolder is the only way I found
                    // to know if the entry is a folder or a project !
                    IContainer sourceContainer = null;
                    try {
                        sourceContainer = ResourcesPlugin.getWorkspace().getRoot().getFolder(entries[i].getPath());
                    } catch (IllegalArgumentException e) {
                        sourceContainer = ResourcesPlugin.getWorkspace().getRoot().getProject(entries[i].getPath().toString());
                    }
                    if (sourceContainer == null) {
                        log.warn("Source container " + entries[i].getPath() + " for project " + project.getName() + " is not valid");
                    } else {
                        processResource(sourceContainer);
                    }
                }
            }

        } catch (CoreException e) {
            throw new CommandException(e);
        }
    }

    /**
     * Review a resource delta
     */
    private void processResourceDelta() throws CommandException {
        try {
            final IProject project = this.resourceDelta.getResource().getProject();
            final IProjectProperties properties = PMDRuntimePlugin.getDefault().loadProjectProperties(project);
            final RuleSet ruleSet = properties.getProjectRuleSet();
            final PMD pmdEngine = getPmdEngineForProject(project);
            this.setStepsCount(countDeltaElement(this.resourceDelta));
            log.debug("Visit of resource delta : " + getStepsCount());

            final DeltaVisitor visitor = new DeltaVisitor();
            visitor.setMonitor(this.getMonitor());
            visitor.setRuleSet(ruleSet);
            visitor.setPmdEngine(pmdEngine);
            visitor.setAccumulator(this.markers);
            visitor.setUseTaskMarker(this.taskMarker);
            visitor.setProjectProperties(properties);
            this.resourceDelta.accept(visitor);

            this.rulesCount = ruleSet.getRules().size();
            this.filesCount += visitor.getProcessedFilesCount();
            this.pmdDuration += visitor.getActualPmdDuration();

        } catch (PropertiesException e) {
            throw new CommandException(e);
        } catch (CoreException e) {
            throw new CommandException(e);
        }
    }

    /**
     * Apply PMD markers after the review
     * 
     */
    private void applyMarkers() {
        log.info("Processing marker directives");
        int violationsCount = 0;
        final Timer timer = new Timer();

        String currentFile = ""; // for logging
        try {
            final Set filesSet = this.markers.keySet();
            final Iterator i = filesSet.iterator();

            beginTask("PMD Applying markers", filesSet.size());

            while (i.hasNext() && !isCanceled()) {
                final IFile file = (IFile) i.next();
                currentFile = file.getName();

                final Set markerInfoSet = (Set) this.markers.get(file);
                file.deleteMarkers(PMDRuntimeConstants.PMD_MARKER, true, IResource.DEPTH_INFINITE);
                file.deleteMarkers(PMDRuntimeConstants.PMD_DFA_MARKER, true, IResource.DEPTH_INFINITE);
                final Iterator j = markerInfoSet.iterator();
                while (j.hasNext()) {
                    final MarkerInfo markerInfo = (MarkerInfo) j.next();
                    final IMarker marker = file.createMarker(markerInfo.getType());
                    marker.setAttributes(markerInfo.getAttributeNames(), markerInfo.getAttributeValues());
                    violationsCount++;
                }

                worked(1);

            }
        } catch (CoreException e) {
            log.warn("CoreException when setting marker info for file " + currentFile + " : " + e.getMessage()); // TODO:
                                                                                                                    // NLS
        } finally {
            timer.stop();
            PMDRuntimePlugin.getDefault().logInformation(
                    "" + violationsCount + " markers applied on " + this.markers.size() + " files in " + timer.getDuration()
                            + "ms.");
            log.info("End of processing marker directives. " + violationsCount + " violations for " + this.markers.size()
                    + " files.");
        }

    }

    /**
     * Count the number of sub-resources of a resource
     * 
     * @param resource a project
     * @return the element count
     */
    private int countResourceElement(final IResource resource) {
        final CountVisitor visitor = new CountVisitor();

        try {
            resource.accept(visitor);
        } catch (CoreException e) {
            PMDRuntimePlugin.getDefault().logError("Exception when counting elements of a project", e);
        }

        return visitor.count;
    }

    /**
     * Count the number of sub-resources of a delta
     * 
     * @param delta a resource delta
     * @return the element count
     */
    private int countDeltaElement(final IResourceDelta delta) {
        final CountVisitor visitor = new CountVisitor();

        try {
            delta.accept(visitor);
        } catch (CoreException e) {
            PMDRuntimePlugin.getDefault().logError("Exception counting elemnts in a delta selection", e);
        }

        return visitor.count;
    }

    /**
     * opens the PMD perspective
     * 
     * @author SebastianRaffel ( 07.05.2005 )
     */
    private void switchToPmdPerspective() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IPerspectiveRegistry reg = workbench.getPerspectiveRegistry();
        final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        window.getActivePage().setPerspective(reg.findPerspectiveWithId(PMDRuntimeConstants.ID_PERSPECTIVE));
    }

    /**
     * Private inner class to count a number of resources or delta elements
     */
    private final class CountVisitor implements IResourceVisitor, IResourceDeltaVisitor {
        public int count = 0;

        public boolean visit(final IResource resource) {
            boolean fVisitChildren = true;
            count++;

            if ((resource instanceof IFile) && (((IFile) resource).getFileExtension() != null)
                    && ((IFile) resource).getFileExtension().equals("java")) {

                fVisitChildren = false;
            }

            return fVisitChildren;
        }

        // @PMD:REVIEWED:UnusedFormalParameter: by Herlin on 10/05/05 23:46
        public boolean visit(final IResourceDelta delta) {
            count++;
            return true;
        }
    };

}