package net.sourceforge.pmd.util.viewer.gui;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.model.ViewerModelEvent;
import net.sourceforge.pmd.util.viewer.model.ViewerModelListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.util.Vector;


/**
 * A panel showing XPath expression evaluation results
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class EvaluationResultsPanel
        extends JPanel
        implements ViewerModelListener {
    private ViewerModel model;
    private JList list;

    /**
     * constructs the panel
     *
     * @param model model to refer to
     */
    public EvaluationResultsPanel(ViewerModel model) {
        super(new BorderLayout());

        this.model = model;

        init();
    }

    private void init() {
        model.addViewerModelListener(this);

        list = new JList();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (list.getSelectedValue() != null) {
                    model.selectNode((SimpleNode) list.getSelectedValue(), EvaluationResultsPanel.this);
                }
            }
        });

        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    /**
     * @see ViewerModelListener#viewerModelChanged(ViewerModelEvent)
     */
    public void viewerModelChanged(ViewerModelEvent e) {
        switch (e.getReason()) {
            case ViewerModelEvent.PATH_EXPRESSION_EVALUATED:

                if (e.getSource() != this) {
                    list.setListData(new Vector(model.getLastEvaluationResults()));
                }

                break;

            case ViewerModelEvent.CODE_RECOMPILED:
                list.setListData(new Vector(0));

                break;
        }
    }
}


/*
 * $Log$
 * Revision 1.1  2005/08/15 19:51:42  tomcopeland
 * Initial revision
 *
 * Revision 1.5  2005/02/16 15:46:34  mikkey
 * javadoc fixes
 *
 * Revision 1.4  2004/09/27 19:42:52  tomcopeland
 * A ridiculously large checkin, but it's all just code reformatting.  Nothing to see here...
 *
 * Revision 1.3  2004/04/15 18:21:58  tomcopeland
 * Cleaned up imports with new version of IDEA; fixed some deprecated Ant junx
 *
 * Revision 1.2  2003/09/23 20:51:06  tomcopeland
 * Cleaned up imports
 *
 * Revision 1.1  2003/09/23 20:32:42  tomcopeland
 * Added Boris Gruschko's new AST/XPath viewer
 *
 * Revision 1.1  2003/09/24 01:33:03  bgr
 * moved to a new package
 *
 * Revision 1.1  2003/09/24 00:40:35  bgr
 * evaluation results browsing added
 *
 */
