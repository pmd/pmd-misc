package net.sourceforge.pmd.eclipse;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleViolation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * A class to process IFile resource against PMD
 * 
 * @author phherlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2003/03/17 23:35:25  phherlin
 * first version
 *
 */
public class PMDProcessor {
    private static final PMDProcessor SELF = new PMDProcessor();
    private PMD pmdEngine;

    /**
     * Default construtor
     */
    private PMDProcessor() {
        initialize();
    }

    /**
     * Return the processor instance
     */
    public static PMDProcessor getInstance() {
        return SELF;
    }

    /**
     * Process an IFile resource
     * @param file the IFile to process
     * @param fTask indicate if a task marker should be created
     */
    public void run(IFile file, boolean fTask) {
        try {
            Reader input = new InputStreamReader(file.getContents());
            RuleContext context = new RuleContext();
            context.setSourceCodeFilename(file.getName());
            context.setReport(new Report());

            pmdEngine.processFile(input, getRuleSet(), context);

            updateMarkers(file, context, fTask);

        } catch (CoreException e) {
            MessageDialog.openError(
                null,
                getMessage(PMDConstants.MSGKEY_ERROR_TITLE),
                getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION) + e.toString());
        } catch (PMDException e) {
            MessageDialog.openError(
                null,
                getMessage(PMDConstants.MSGKEY_ERROR_TITLE),
                getMessage(PMDConstants.MSGKEY_ERROR_PMD_EXCEPTION) + e.toString());
        }
    }

    /**
     * Update markers list for the specified file
     * @param file the file for which markes are to be updated
     * @param context a PMD context
     * @param fTask indicate if a task marker should be created
     */
    private void updateMarkers(IFile file, RuleContext context, boolean fTask) throws CoreException {
        file.deleteMarkers(PMDPlugin.PMD_MARKER, true, IResource.DEPTH_INFINITE);

        Iterator iter = context.getReport().iterator();
        while (iter.hasNext()) {
            RuleViolation violation = (RuleViolation) iter.next();

            IMarker marker = file.createMarker(fTask ? PMDPlugin.PMD_TASKMARKER : PMDPlugin.PMD_MARKER);
            marker.setAttribute(IMarker.MESSAGE, violation.getDescription());
            marker.setAttribute(IMarker.LINE_NUMBER, violation.getLine());
            marker.setAttribute("description", violation.getRule().getMessage());
            marker.setAttribute("example", violation.getRule().getExample());

            switch (violation.getRule().getPriority()) {
                case 1 :
                    marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
                case 2 :
                    marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                    break;

                case 5 :
                    marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
                    break;

                case 3 :
                    marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
                case 4 :
                default :
                    marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
                    break;
            }
        }
    }

    /**
     * Initialize the processor
     */
    private void initialize() {
        pmdEngine = new PMD();
    }

    /**
     * Get the rule set from preferences
     */
    private RuleSet getRuleSet() {
        RuleSetFactory factory = new RuleSetFactory();
        String[] ruleSetFiles = PMDPlugin.getDefault().getRuleSetsPreference();

        RuleSet ruleSet = factory.createRuleSet(getClass().getClassLoader().getResourceAsStream(ruleSetFiles[0]));
        for (int i = 1; i < ruleSetFiles.length; i++) {
            RuleSet tmpRuleSet = factory.createRuleSet(getClass().getClassLoader().getResourceAsStream(ruleSetFiles[i]));
            ruleSet.addRuleSet(tmpRuleSet);
        }

        return ruleSet;
    }

    /**
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    private String getMessage(String key) {
        return PMDPlugin.getDefault().getMessage(key);
    }
}