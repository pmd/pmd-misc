package net.sourceforge.pmd.ui.views.actions;

import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TableViewer;

/**
 * Deletes selected Violations Adapted from Phillipe Herlin
 * 
 * @author SebastianRaffel ( 21.05.2005 )
 */
public class RemoveViolationAction extends ViolationSelectionAction {

    /**
     * Constructor
     * 
     * @param viewer
     */
    public RemoveViolationAction(TableViewer viewer) {
        super(viewer);

        // set Image, Text and ToolTip-Text for this Action
        setImageDescriptor(PMDUiPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_REMVIO));
        setText(PMDUiPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_ACTION_REMOVE_VIOLATION));
        setToolTipText(PMDUiPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_TOOLTIP_REMOVE_VIOLATION));

    }

    /**
     * Executes the Action
     */
    public void run() {
        // simply: get all Markers
        final IMarker[] markers = getSelectedViolations();
        if (markers != null) {
            try {
                IWorkspace workspace = ResourcesPlugin.getWorkspace();
                workspace.run(new IWorkspaceRunnable() {
                    public void run(IProgressMonitor monitor) throws CoreException {
                        for (int i = 0; i < markers.length; i++) {
                            // ... and delete them
                            markers[i].delete();
                        }
                    }
                }, null);
            } catch (CoreException ce) {
                PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION, ce);
            }
        }
    }
}
