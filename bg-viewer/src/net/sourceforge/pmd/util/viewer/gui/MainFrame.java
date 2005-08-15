package net.sourceforge.pmd.util.viewer.gui;

import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.model.ViewerModelEvent;
import net.sourceforge.pmd.util.viewer.model.ViewerModelListener;
import net.sourceforge.pmd.util.viewer.util.NLS;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * viewer's main frame
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class MainFrame
        extends JFrame
        implements ActionListener, ActionCommands, ViewerModelListener {
    private ViewerModel model;
    private SourceCodePanel sourcePanel;
    private ASTPanel astPanel;
    private XPathPanel xPathPanel;
    private JButton compileBtn;
    private JButton evalBtn;
    private JLabel statusLbl;

    /**
     * constructs and shows the frame
     */
    public MainFrame() {
        super(NLS.nls("MAIN.FRAME.TITLE"));

        init();
    }

    private void init() {
        model = new ViewerModel();

        model.addViewerModelListener(this);

        sourcePanel = new SourceCodePanel(model);
        astPanel = new ASTPanel(model);
        xPathPanel = new XPathPanel(model);

        getContentPane().setLayout(new BorderLayout());

        JSplitPane editingPane =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sourcePanel, astPanel);
        editingPane.setResizeWeight(0.5d);

        JPanel interactionsPane = new JPanel(new BorderLayout());

        interactionsPane.add(xPathPanel, BorderLayout.SOUTH);
        interactionsPane.add(editingPane, BorderLayout.CENTER);

        getContentPane().add(interactionsPane, BorderLayout.CENTER);

        compileBtn = new JButton(NLS.nls("MAIN.FRAME.COMPILE_BUTTON.TITLE"));
        compileBtn.setActionCommand(COMPILE_ACTION);
        compileBtn.addActionListener(this);

        evalBtn = new JButton(NLS.nls("MAIN.FRAME.EVALUATE_BUTTON.TITLE"));
        evalBtn.setActionCommand(EVALUATE_ACTION);
        evalBtn.addActionListener(this);
        evalBtn.setEnabled(false);

        statusLbl = new JLabel();
        statusLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JPanel btnPane = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnPane.add(compileBtn);
        btnPane.add(evalBtn);
        btnPane.add(statusLbl);

        getContentPane().add(btnPane, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        pack();
        setSize(800, 600);

        setVisible(true);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        long t0, t1;
        if (command.equals(COMPILE_ACTION)) {
            try {
                t0 = System.currentTimeMillis();
                model.commitSource(sourcePanel.getSourceCode());
                t1 = System.currentTimeMillis();
                setStatus(NLS.nls("MAIN.FRAME.COMPILATION.TOOK")+ " "+(t1-t0)+" ms"); 
            } catch (ParseException exc) {
                setStatus(NLS.nls("MAIN.FRAME.COMPILATION.PROBLEM")+ " "+exc.toString());
                new ParseExceptionHandler(this, exc);
            } 
        } else if (command.equals(EVALUATE_ACTION)) {
            try {
                t0 = System.currentTimeMillis();
                model.evaluateXPathExpression(xPathPanel.getXPathExpression(), this);
                t1 = System.currentTimeMillis();
                setStatus(NLS.nls("MAIN.FRAME.EVALUATION.TOOK")+ " "+(t1-t0)+" ms");
            } catch (Exception exc) {
                setStatus(NLS.nls("MAIN.FRAME.EVALUATION.PROBLEM")+ " "+exc.toString());
                new ParseExceptionHandler(this, exc);
            }
        }
    }

    /**
     * Sets the status bar message
	 * @param string the new status, the empty string will be set if the value is <code>null</code>
	 */
	private void setStatus(String string) {
        if (string == null)
            string = "";
        statusLbl.setText(string);		
	}

	/**
     * @see ViewerModelListener#viewerModelChanged(ViewerModelEvent)
     */
    public void viewerModelChanged(ViewerModelEvent e) {
        evalBtn.setEnabled(model.hasCompiledTree());
    }
}


/*
 * $Log$
 * Revision 1.1  2005/08/15 19:51:42  tomcopeland
 * Initial revision
 *
 * Revision 1.6  2005/02/16 15:47:01  mikkey
 * javadoc fixes
 *
 * Revision 1.5  2004/12/13 14:46:11  tomcopeland
 * Applied, thanks Miguel!
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
 * Revision 1.2  2003/09/24 00:40:35  bgr
 * evaluation results browsing added
 *
 * Revision 1.1  2003/09/22 05:21:54  bgr
 * initial commit
 *
 */
