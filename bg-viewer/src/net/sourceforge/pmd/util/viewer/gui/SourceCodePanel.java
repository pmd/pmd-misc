package net.sourceforge.pmd.util.viewer.gui;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.model.ViewerModelEvent;
import net.sourceforge.pmd.util.viewer.model.ViewerModelListener;
import net.sourceforge.pmd.util.viewer.util.NLS;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.BorderLayout;
import java.awt.Color;


/**
 * source code panel
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class SourceCodePanel
        extends JPanel
        implements ViewerModelListener {
    private ViewerModel model;
    private JTextArea sourceCodeArea;

    public SourceCodePanel(ViewerModel model) {
        this.model = model;

        init();
    }

    private void init() {
        model.addViewerModelListener(this);

        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), NLS.nls("SOURCE.PANEL.TITLE")));

        setLayout(new BorderLayout());

        sourceCodeArea = new JTextArea();

        add(new JScrollPane(sourceCodeArea), BorderLayout.CENTER);
    }

    /**
     * retrieves the string representation of the source code
     *
     * @return source code's string representation
     */
    public String getSourceCode() {
        return sourceCodeArea.getText();
    }

    /**
     * @see ViewerModelListener#viewerModelChanged(ViewerModelEvent)
     */
    public void viewerModelChanged(ViewerModelEvent e) {
        if (e.getReason() == ViewerModelEvent.NODE_SELECTED) {
            final SimpleNode node = (SimpleNode) e.getParameter();

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        sourceCodeArea.getHighlighter().removeAllHighlights();

                        if (node == null) {
                            return;
                        }

                        int startOffset =
                                (sourceCodeArea.getLineStartOffset(node.getBeginLine() - 1) +
                                node.getBeginColumn()) - 1;

                        int end =
                                (sourceCodeArea.getLineStartOffset(node.getEndLine() - 1) +
                                node.getEndColumn());

                        sourceCodeArea.getHighlighter().addHighlight(startOffset, end,
                                new DefaultHighlighter.DefaultHighlightPainter(new Color(79, 237, 111)));

                        sourceCodeArea.moveCaretPosition(startOffset);
                    } catch (BadLocationException exc) {
                        throw new IllegalStateException(exc.getMessage());
                    }
                }
            });
        }
    }
}


/*
 * $Log$
 * Revision 1.1  2005/08/15 19:51:42  tomcopeland
 * Initial revision
 *
 * Revision 1.5  2005/02/16 15:47:27  mikkey
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
 * Revision 1.1  2003/09/22 05:21:54  bgr
 * initial commit
 *
 */
