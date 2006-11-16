/*
 * Created on 9 mai 2005
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.ui.model.AbstractPMDRecord;
import net.sourceforge.pmd.ui.model.FileRecord;
import net.sourceforge.pmd.ui.model.FileToMarkerRecord;
import net.sourceforge.pmd.ui.model.MarkerRecord;
import net.sourceforge.pmd.ui.model.PackageRecord;
import net.sourceforge.pmd.ui.model.ProjectRecord;
import net.sourceforge.pmd.ui.model.RootRecord;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * Provides the Violation Overview with Content Elements can be
 * PackageRecords or FileRecords
 * 
 * @author SebastianRaffel ( 09.05.2005 ), Philppe Herlin, Sven Jacob
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.5  2006/11/16 17:11:08  holobender
 * Some major changes:
 * - new CPD View
 * - changed and refactored ViolationOverview
 * - some minor changes to dataflowview to work with PMD
 *
 * Revision 1.4  2006/10/10 21:43:20  phherlin
 * Review Sebastian code... and fix most PMD warnings
 * Revision 1.3 2006/10/09 13:26:40 phherlin Review Sebastian code... and
 * fix most PMD warnings
 * 
 */
public class ViolationOverviewContentProvider implements ITreeContentProvider, IStructuredContentProvider, IResourceChangeListener {
    private static final Log LOG = LogFactory.getLog(ViolationOverviewContentProvider.class);
    protected static final Object[] NO_CHILDREN = new Object[0];
    protected boolean filterPackages;

    final private ViolationOverview violationView;
    private TreeViewer treeViewer;

    private RootRecord root;

    /**
     * Constructor
     * 
     * @param view
     */
    public ViolationOverviewContentProvider(ViolationOverview view) {
        super();

        this.violationView = view;
        this.treeViewer = view.getViewer();
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        if (root != null) {
            final IWorkspaceRoot workspaceRoot = (IWorkspaceRoot) this.root.getResource();
            workspaceRoot.getWorkspace().removeResourceChangeListener(this);
        }
    }

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object parentElement) {
        Object[] children = NO_CHILDREN;

        if ((parentElement instanceof IWorkspaceRoot) || (parentElement instanceof RootRecord)) {
            children = getChildrenOfRoot();
        } else if (parentElement instanceof PackageRecord) {
            children = getChildrenOfPackage((PackageRecord)parentElement);            
        } else if (parentElement instanceof FileRecord) {
            children = getChildrenOfFile((FileRecord)parentElement);
        } else if (parentElement instanceof MarkerRecord) {
            children = getChildrenOfMarker((MarkerRecord)parentElement);
        }
        return children;
    }

    /**
     * Gets the children of a file record.
     * @param record FileRecord
     * @return children as array
     */
    private Object[] getChildrenOfFile(FileRecord record) {
        return record.getChildren();
    }

    /**
     * Gets the children of a marker record.
     * @param record MarkerRecord
     * @return children as array
     */
    private Object[] getChildrenOfMarker(MarkerRecord record) {
        record.updateChildren();
        return record.getChildren();
    }

    /**
     * Gets the children of a PackageRecord.
     * If the presentation type is {@link ViolationOverview#SHOW_MARKERS_FILES} the children (MarkerRecord)
     * of the children (FileRecord) will be get. 
     * 
     * @param record PackageRecord
     * @return children as array
     */
    private Object[] getChildrenOfPackage(PackageRecord record) {
        Object[] children = NO_CHILDREN;
        
        if (this.violationView.getShowType() == ViolationOverview.SHOW_MARKERS_FILES) {
            final Map markers = new HashMap();
            final List files = record.getChildrenAsList();
            for (int i = 0; i < files.size(); i++) {
                final AbstractPMDRecord fileRec = (AbstractPMDRecord) files.get(i);
                final List newMarkers = fileRec.getChildrenAsList();
                
                for (int j = 0; j < newMarkers.size(); j++) {
                    final AbstractPMDRecord markerRec = (AbstractPMDRecord) newMarkers.get(j);
                    markers.put(markerRec.getName(), markerRec);
                }                
            }
            
            children = markers.values().toArray(new MarkerRecord[markers.size()]);
        } else {
            children = record.getChildren();    
        }
        return children;
    }

    /**
     * Gets the children of the root depending on the show type.
     * @return children
     */
    private Object[] getChildrenOfRoot() {
        Object[] children = NO_CHILDREN;
        
        // ... we care about its Project's
        final List projects = root.getChildrenAsList();
        final ProjectRecord[] projectArray = new ProjectRecord[projects.size()];
        projects.toArray(projectArray);

        // we make a List of all Packages
        final List packages = new ArrayList();
        for (int i = 0; i < projectArray.length; i++) {
            if (projectArray[i].isProjectOpen()) {
                packages.addAll(projectArray[i].getChildrenAsList());
            }
        }
       
        switch (this.violationView.getShowType()) {
        case ViolationOverview.SHOW_MARKERS_FILES:
        case ViolationOverview.SHOW_PACKAGES_FILES_MARKERS:
            // show packages
            children = packages.toArray();
            break;               
            
        case ViolationOverview.SHOW_FILES_MARKERS:
            // show files
            final List files = new ArrayList();
            for (int j = 0; j < packages.size(); j++) {
                final AbstractPMDRecord packageRec = (AbstractPMDRecord) packages.get(j);
                files.addAll(packageRec.getChildrenAsList());
            }
            
            children = files.toArray();
            break;
                    
        default:
            // do nothing
        }             
        return children;
    }

     /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
        Object parent = null;
        final AbstractPMDRecord record = (AbstractPMDRecord) element;
        
        switch (violationView.getShowType()) {
        case ViolationOverview.SHOW_FILES_MARKERS:
            if (element instanceof FileRecord) {
                parent = this.root;
            } else {
                parent = record.getParent();
            }
            break;
        case ViolationOverview.SHOW_MARKERS_FILES:
            if (element instanceof FileToMarkerRecord) {
                parent = record.getParent();
            } else if (element instanceof PackageRecord) {
                parent = this.root;
            } else if (element instanceof MarkerRecord) {
                parent = record.getParent().getParent();
            }
            
            break;
        case ViolationOverview.SHOW_PACKAGES_FILES_MARKERS:
            if (element instanceof PackageRecord) {
                parent = this.root;
            } else {
                parent = record.getParent();
            }

            break;
        default:
            // do nothing
        }

        return parent;
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element) {
        boolean hasChildren = true;
        
        // find out if this is the last level in the tree (to avaoid recursion)
        switch (violationView.getShowType()) {
        case ViolationOverview.SHOW_PACKAGES_FILES_MARKERS:
        case ViolationOverview.SHOW_FILES_MARKERS:
            hasChildren ^= element instanceof MarkerRecord;
            break;
        case ViolationOverview.SHOW_MARKERS_FILES:
            hasChildren ^= element instanceof FileToMarkerRecord;
            break;
        default:
            // do nothing
        }
        
        if (hasChildren) {
            hasChildren = getChildren(element).length > 0;
        }
        return hasChildren;
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        LOG.debug("ViolationOverview inputChanged");
        this.treeViewer = (TreeViewer) viewer;

        // this is called, when the View is instantiated and gets Input
        // or if the Source of Input changes

        // we remove an existing ResourceChangeListener
        IWorkspaceRoot workspaceRoot = null;
        if (this.root != null) {
            LOG.debug("remove current listener");
            workspaceRoot = (IWorkspaceRoot) this.root.getResource();
            workspaceRoot.getWorkspace().removeResourceChangeListener(this);
        }

        // ... to add a new one, so we can listen to Changes made
        // to Resources in the Workspace
        if (newInput instanceof IWorkspaceRoot) {
            LOG.debug("the new input is a workspace root");
            // either we got a WorkspaceRoot
            workspaceRoot = (IWorkspaceRoot) newInput;
            this.root = new RootRecord(workspaceRoot);
            workspaceRoot.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
        } else if (newInput instanceof RootRecord) {
            LOG.debug("the new input is a root record");
            // ... or already a Record for it
            this.root = (RootRecord) newInput;
            workspaceRoot = (IWorkspaceRoot) this.root.getResource();
            workspaceRoot.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
        }
    }

    /**
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged(IResourceChangeEvent event) {
        LOG.debug("resource changed event");
        final IMarkerDelta[] markerDeltas = event.findMarkerDeltas(PMDRuntimeConstants.PMD_MARKER, true);

        // first we get a List of changes to Files and Projects
        // so we won't need updating everything
        final List changedFiles = new ArrayList();
        final List changedProjects = new ArrayList();
        for (int i = 0; i < markerDeltas.length; i++) {
            final IResource resource = markerDeltas[i].getResource();
            final IProject project = resource.getProject();

            // the lists should not contain
            // Projects or Resources twice

            if (!changedFiles.contains(resource)) {
                changedFiles.add(resource);
                LOG.debug("Resource " + resource.getName() + " has changed");
            }

            if (!changedProjects.contains(project)) {
                changedProjects.add(project);
                LOG.debug("Project " + project.getName() + " has changed");
            }
        }

        // we can add, change or remove Resources
        // all this changes are given to the viewer later
        final List additions = new ArrayList();
        final List removals = new ArrayList();
        final List changes = new ArrayList();

        // we got through the changed Projects
        for (int i = 0; i < changedProjects.size(); i++) {
            final IProject project = (IProject) changedProjects.get(i);
            LOG.debug("Processing changes for project " + project.getName());
            ProjectRecord projectRec = (ProjectRecord) this.root.findResource(project);

            // if the Project is closed or deleted,
            // we also delete it from the Model and go on
            if (!(project.isOpen() && project.isAccessible())) { // NOPMD by Sven on 09.11.06 22:17
                LOG.debug("The project is not open or not accessible. Remove it");
                final List[] array = updateFiles(project, changedFiles);
                removals.addAll(array[1]);
                this.root.removeResource(project);
            }

            // if we couldn't find the Project
            // then it has to be new
            else if (projectRec == null) {
                LOG.debug("Cannot find a project record for it. Add it.");
                projectRec = (ProjectRecord) this.root.addResource(project);
            }

            // then we can update the Files for the new or updated Project
            final List[] array = updateFiles(project, changedFiles);
            additions.addAll(array[0]);
            removals.addAll(array[1]);
            changes.addAll(array[2]);
        }

        // the addtions, removals and changes are given to the viewer
        // so that it can update itself
        // updating the table MUST be in sync
        this.treeViewer.getControl().getDisplay().syncExec(new Runnable() {
            public void run() {
                updateViewer(additions, removals, changes);
            }
        });
    }

    /**
     * Updates the Files for a given Project
     * 
     * @param project
     * @param changedFiles, a List of all changed Files
     * @return an ArrayList of ArrayLists containing additons [0], removals [1]
     *         and changes [2] (Array-Position in Brackets)
     */
    protected List[] updateFiles(IProject project, List changedFiles) {
        final List additions = new ArrayList();
        final List removals = new ArrayList();
        final List changes = new ArrayList();
        List[] updatedFiles = new List[] { additions, removals, changes };

        // we search for the ProjectRecord to the Project
        // if it doesn't exist, we return nothing
        final ProjectRecord projectRec = (ProjectRecord) this.root.findResource(project);

        // we got through all files
        if ((projectRec != null) && (project.isAccessible())) {
            updatedFiles = searchProjectForModifications(projectRec, changedFiles);
        }

        // if the project is deleted or closed
        else if (projectRec != null) {
            final List packages = projectRec.getChildrenAsList();
            // ... we add all Packages to the removals
            // so they are not shown anymore
            removals.addAll(packages);
            for (int k = 0; k < packages.size(); k++) {
                final PackageRecord packageRec = (PackageRecord) packages.get(k);
                removals.addAll(packageRec.getChildrenAsList());
            }
            updatedFiles = new List[] { additions, removals, changes };
        }

        return updatedFiles;
    }

    /**
     * Analyses the modification inside a single project and compute the list of additions, updates and removals.
     * 
     * @param projectRec
     * @param changedFiles
     * @return
     */
    private List[] searchProjectForModifications(ProjectRecord projectRec, List changedFiles) {
        final List additions = new ArrayList();
        final List removals = new ArrayList();
        final List changes = new ArrayList();
        final IProject project = (IProject) projectRec.getResource();
        
        LOG.debug("Analyses project " + project.getName());

        for (int i = 0; i < changedFiles.size(); i++) {
            final IResource resource = (IResource) changedFiles.get(i);
            LOG.debug("Analyses resource " + resource.getName());

            // ... and first check, if the project is the right one
            if (project.equals(resource.getProject())) {
                final AbstractPMDRecord rec = projectRec.findResource(resource);
                if ((rec != null) && (rec.getResourceType() == IResource.FILE)) {
                    final FileRecord fileRec = (FileRecord) rec;
                    fileRec.updateChildren();
                    if ((fileRec.getResource().isAccessible()) && (fileRec.hasMarkers())) {
                        LOG.debug("The file has changed");
                        changes.add(fileRec);
                    } else {
                        LOG.debug("The file has been removed");
                        projectRec.removeResource(fileRec.getResource());
                        removals.add(fileRec);

                        // remove parent if no more markers
                        final PackageRecord packageRec = (PackageRecord) fileRec.getParent();
                        if (!packageRec.hasMarkers()) {
                            projectRec.removeResource(fileRec.getParent().getResource());
                            removals.add(packageRec);
                        }
                    } 
                } else if (rec == null) {
                    LOG.debug("This is a new file.");
                    final AbstractPMDRecord fileRec = projectRec.addResource(resource);
                    additions.add(fileRec);
                } else {
                    LOG.debug("The found resource is not a file ! type found : " + rec.getResourceType());
                }
            } else {
                LOG.debug("The project resource is not the same ! (" + resource.getProject().getName() + ")");
            }
        }

        return new List[] { additions, removals, changes };
    }

    /**
     * Applies found updates on the table, adapted from Philippe Herlin
     * 
     * @param additions
     * @param removals
     * @param changes
     */
    protected void updateViewer(List additions, List removals, List changes) {
        final List refreshList = new ArrayList();
        
        // perform removals
        if (removals.size() > 0) {
            this.treeViewer.remove(removals.toArray(new AbstractPMDRecord[removals.size()]));
            refreshList.addAll(removals);
        }

        // perform additions
        if (additions.size() > 0) {
            for (int i = 0; i < additions.size(); i++) {
                final AbstractPMDRecord addedRec = (AbstractPMDRecord) additions.get(i);
                if (addedRec instanceof FileRecord) {
                    this.treeViewer.add(addedRec.getParent(), addedRec);
                } else {
                    this.treeViewer.add(this.root, addedRec);
                }
            }
            refreshList.addAll(additions);
        }

        // perform changes
        if (changes.size() > 0) {
            for (int i = 0; i < changes.size(); i++) {
                final AbstractPMDRecord changedRec = (AbstractPMDRecord) changes.get(i);
                if (changedRec instanceof FileRecord) {
                    this.treeViewer.update(changedRec, null);
                    this.treeViewer.update(changedRec.getChildren(), null);
                }
            }
            refreshList.addAll(changes);
        }
        
        refreshViewer(refreshList);
    }
    
    /**
     * Refresh the viewer with the parent elements of each element in the list.
     * @param elements list of changes, removals or additions (AbstractPMDRecord)
     */
    protected void refreshViewer(List elements) {
        final Iterator elementIterator = elements.iterator();
        while (elementIterator.hasNext()) {
            final AbstractPMDRecord record = (AbstractPMDRecord)elementIterator.next();
            this.treeViewer.refresh(record.getParent());
        }
    }
}
