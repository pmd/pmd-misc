package net.sourceforge.pmd.eclipse.preferences;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.PMDPlugin;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Implements a label provider for the rules item to be displayed in the
 * rule table of the PMD Preference page
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 *
 */
public class RuleLabelProvider implements ITableLabelProvider {
    private static final String PRIORITY_ILLEGAL = "* illegal *";
    private static final String[] PRIORITY_LABEL = PMDPlugin.getDefault().getPriorityLabels();

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(Object, int)
     */
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(Object, int)
     */
    public String getColumnText(Object element, int columnIndex) {
        String result = "";
        if (element instanceof Rule) {
            Rule rule = (Rule) element;
            if (columnIndex == 0) {
                result = rule.getName();
            } else if (columnIndex == 1) {
                if ((rule.getPriority() <= PRIORITY_LABEL.length) && (rule.getPriority() > 0)) {
                    result = PRIORITY_LABEL[rule.getPriority() - 1];
                } else {
                    result = PRIORITY_ILLEGAL;
                }
            } else if (columnIndex == 2) {
                result = rule.getDescription().trim();
            }
        }

        return result;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(ILabelProviderListener)
     */
    public void addListener(ILabelProviderListener listener) {
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(Object, String)
     */
    public boolean isLabelProperty(Object element, String property) {
        return true;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(ILabelProviderListener)
     */
    public void removeListener(ILabelProviderListener listener) {
    }

}
