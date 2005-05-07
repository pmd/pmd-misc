package net.sourceforge.pmd.eclipse.builder;

import java.util.Map;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.cmd.ReviewCodeCmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Implements an incremental builder for PMD. Use ResourceVisitor and DeltaVisitor
 * to process each file of the project.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.9  2005/05/07 13:32:06  phherlin
 * Continuing refactoring
 * Fix some PMD violations
 * Fix Bug 1144793
 * Fix Bug 1190624 (at least try)
 *
 * Revision 1.8  2003/07/01 20:22:16  phherlin
 * Make rules selectable from projects
 *
 * Revision 1.7  2003/06/30 22:05:07  phherlin
 * Improving incremental building
 *
 * Revision 1.6  2003/06/19 20:58:33  phherlin
 * Improve progress indicator accuracy
 *
 * Revision 1.5  2003/05/19 22:27:33  phherlin
 * Refactoring to improve performance
 *
 * Revision 1.4  2003/03/30 20:51:08  phherlin
 * Adding logging
 *
 */
public class PMDBuilder extends IncrementalProjectBuilder {
    public static final String PMD_BUILDER = "net.sourceforge.pmd.eclipse.pmdBuilder";
    public static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.builder.PMDBuilder");

    /**
     * @see org.eclipse.core.internal.events.InternalBuilder#build(int, Map, IProgressMonitor)
     */
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
        log.info("Incremental builder activated");

        try {
            if (kind == AUTO_BUILD) {
                log.debug("Auto build requested.");
                buildAuto(monitor);
            } else if (kind == FULL_BUILD) {
                log.debug("Full build requested.");
                buildFull(monitor);
            } else if (kind == INCREMENTAL_BUILD) {
                log.debug("Incremental build requested.");
                buildIncremental(monitor);
            } else {
                log.warn("This kind of build is not supported : " + kind);
            }
        } catch (CommandException e) {
            throw new CoreException(new Status(IStatus.ERROR, PMDPlugin.getDefault().getBundle().getSymbolicName(), 0, e.getMessage(), e));
        }

        log.info("Build done.");
        return null;
    }

    /**
     * Automatic build
     * @param monitor a progress monitor
     * @throws CommandException
     */
    private void buildAuto(IProgressMonitor monitor) throws CommandException {
        this.buildIncremental(monitor);
    }

    /**
     * Full build
     * @param monitor A progress monitor.
     * @throws CommandException
     */
    private void buildFull(IProgressMonitor monitor) throws CommandException {
        IProject currentProject = this.getProject();
        if (currentProject != null) {
            this.processProjectFiles(currentProject, monitor);
        }
    }

    /**
     * Incremental build
     * @param monitor a progress monitor.
     * @throws CommandException
     */
    private void buildIncremental(IProgressMonitor monitor) throws CommandException {
        IProject result[] = null;

        IProject currentProject = getProject();
        if (currentProject != null) {
            IResourceDelta resourceDelta = this.getDelta(currentProject);
            if ((resourceDelta != null) && (resourceDelta.getAffectedChildren().length != 0)) {
                ReviewCodeCmd cmd = new ReviewCodeCmd();
                cmd.setResourceDelta(resourceDelta);
                cmd.setTaskMarker(false);
                cmd.setMonitor(monitor);
                cmd.performExecute();
            } else {
                log.info("No change reported. Performing no build");
            }
        }
    }

    /**
     * Process all files in the project
     * @param project the project
     * @param monitor a progress monitor
     * @throws CommandException
     */
    private void processProjectFiles(IProject project, IProgressMonitor monitor) throws CommandException {
        ReviewCodeCmd cmd = new ReviewCodeCmd();
        cmd.setResource(project);
        cmd.setTaskMarker(false);
        cmd.setMonitor(monitor);
        cmd.performExecute();
    }

}
