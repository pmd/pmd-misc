package net.sourceforge.pmd.ui.actions;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.HTMLRenderer;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.renderers.VBHTMLRenderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.runtime.cmd.RenderReportCmd;
import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Process GenerateReport action menu.
 * Generate a HTML report on the current project.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2006/10/10 22:31:01  phherlin
 * Fix other PMD warnings
 *
 * Revision 1.1  2006/05/22 21:23:56  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.3  2006/04/11 21:01:17  phherlin
 * Add new VBHTML report
 *
 * Revision 1.2  2005/10/24 22:39:35  phherlin
 * Refactor command processing
 *
 * Revision 1.1  2005/04/20 23:15:53  phherlin
 * Implement reports generation RFE#1177802
 *
 *
 */
public class GenerateReportAction implements IObjectActionDelegate {
    private static final Logger log = Logger.getLogger(GenerateReportAction.class);
    private IWorkbenchPart targetPart;

    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public final void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(IAction)
     */
    public final void run(final IAction action) {
        log.info("Generation Report action requested");
        final ISelection sel = targetPart.getSite().getSelectionProvider().getSelection();
        if (sel instanceof IStructuredSelection) {
            try {
                final IProject project = getProject((IStructuredSelection) sel);
                if (project != null) {
                    final RenderReportCmd cmd1 = new RenderReportCmd();
                    cmd1.setProject(project);
                    cmd1.setRenderer(new HTMLRenderer());
                    cmd1.setReportName(PMDUiConstants.HTML_REPORT_NAME);
                    cmd1.setUserInitiated(true);
                    cmd1.performExecute();
                    
                    final RenderReportCmd cmd2 = new RenderReportCmd();                    
                    cmd2.setProject(project);
                    cmd2.setRenderer(new CSVRenderer());
                    cmd2.setReportName(PMDUiConstants.CSV_REPORT_NAME);
                    cmd1.setUserInitiated(true);
                    cmd2.performExecute();
                    
                    final RenderReportCmd cmd3 = new RenderReportCmd();                    
                    cmd3.setProject(project);
                    cmd3.setRenderer(new XMLRenderer());
                    cmd3.setReportName(PMDUiConstants.XML_REPORT_NAME);
                    cmd1.setUserInitiated(true);
                    cmd3.performExecute();
                    
                    final RenderReportCmd cmd4 = new RenderReportCmd();                    
                    cmd4.setProject(project);
                    cmd4.setRenderer(new TextRenderer());
                    cmd4.setReportName(PMDUiConstants.TXT_REPORT_NAME);
                    cmd1.setUserInitiated(true);
                    cmd4.performExecute();
                    
                    final RenderReportCmd cmd5 = new RenderReportCmd();                    
                    cmd5.setProject(project);
                    cmd5.setRenderer(new VBHTMLRenderer());
                    cmd5.setReportName(PMDUiConstants.VBHTML_REPORT_NAME);
                    cmd5.setUserInitiated(true);
                    cmd5.performExecute();
                }
            } catch (CommandException e) {
                PMDUiPlugin.getDefault().showError(
                        PMDUiPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_ERROR_PMD_EXCEPTION),
                    e);
            }
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public final void selectionChanged(IAction action, ISelection selection) {
        // nothing to do
    }
    
    /**
     * Get a project from a selection
     * @param selection
     * @return
     */
    private IProject getProject(final IStructuredSelection selection) {
        IProject project = null;
        final Object object = selection.getFirstElement();
        if ((object != null) && (object instanceof IAdaptable)) {
           final IResource resource = (IResource) ((IAdaptable) object).getAdapter(IResource.class);            
           if (resource != null) {
               project = resource.getProject();
           }
        }
        
        return project;
    }

}
