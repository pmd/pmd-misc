/*
 *  Copyright (c) 2002-2003, Ole-Martin M�rk
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 */
package pmd.config.ui;

import java.beans.PropertyEditorSupport;
import javax.swing.JPanel;
import net.sourceforge.pmd.Rule;
import java.awt.event.MouseEvent;

/** The JPanel used to edit the Rule property
 * @author ole martin m�rk
 */
public class RuleEnabler extends JPanel {

	private final PropertyEditorSupport editor;
	/** Creates a new editor
	 * @param editor The object to be notified of changes in the property
	 */
	public RuleEnabler( PropertyEditorSupport editor ) {
		this.editor = editor;
		initComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        topSeparator = new javax.swing.JSeparator();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        availableList = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        chosenList = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        chooseOne = new javax.swing.JButton();
        choseAll = new javax.swing.JButton();
        removeOne = new javax.swing.JButton();
        removeAll = new javax.swing.JButton();
        middleSeparator = new javax.swing.JSeparator();
        jPanel6 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        information = new javax.swing.JEditorPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        example = new javax.swing.JEditorPane();
        bottomSeparator = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        properties = new javax.swing.JTable();

        setLayout(new java.awt.GridBagLayout());

        setPreferredSize(new java.awt.Dimension(610, 510));
        setMinimumSize(new java.awt.Dimension(300, 300));
        jLabel1.setText("Select rules that should be used");
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel1, gridBagConstraints);

        jLabel2.setText("See http://pmd.sf.net for more information");
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(topSeparator, gridBagConstraints);

        jPanel5.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(270, 100));
        availableList.setModel(AvailableListModel.getInstance());
        availableList.setCellRenderer(new ListCell());
        availableList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                availableListValueChanged();
            }
        });

        availableList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                availableListMouseClicked(evt);
            }
        });

        jScrollPane1.setViewportView(availableList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(jScrollPane1, gridBagConstraints);

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setPreferredSize(new java.awt.Dimension(270, 100));
        chosenList.setModel(SelectedListModel.getSelectedListModelInstance());
        chosenList.setCellRenderer(new ListCell());
        chosenList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                chosenListValueChanged();
            }
        });

        chosenList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chosenListMouseClicked(evt);
            }
        });

        jScrollPane3.setViewportView(chosenList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(jScrollPane3, gridBagConstraints);

        jLabel3.setDisplayedMnemonic('A');
        jLabel3.setLabelFor(availableList);
        jLabel3.setText("Available rules");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
        jPanel5.add(jLabel3, gridBagConstraints);

        jLabel4.setDisplayedMnemonic('C');
        jLabel4.setLabelFor(chosenList);
        jLabel4.setText("Chosen rules");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 0);
        jPanel5.add(jLabel4, gridBagConstraints);

        chooseOne.setText(">");
        chooseOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseOneActionPerformed();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 1, 0, 1);
        jPanel5.add(chooseOne, gridBagConstraints);

        choseAll.setText(">>");
        choseAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                choseAllActionPerformed();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        jPanel5.add(choseAll, gridBagConstraints);

        removeOne.setText("<");
        removeOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeOneActionPerformed();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        jPanel5.add(removeOne, gridBagConstraints);

        removeAll.setText("<<");
        removeAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllActionPerformed();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 2, 1);
        jPanel5.add(removeAll, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jPanel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(middleSeparator, gridBagConstraints);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel6.setText("Information");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jLabel6, gridBagConstraints);

        jLabel5.setLabelFor(example);
        jLabel5.setText("Example");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jLabel5, gridBagConstraints);

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane4.setPreferredSize(new java.awt.Dimension(300, 200));
        jScrollPane4.setMinimumSize(new java.awt.Dimension(150, 150));
        information.setEditable(false);
        jScrollPane4.setViewportView(information);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        jPanel6.add(jScrollPane4, gridBagConstraints);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(300, 200));
        jScrollPane2.setMinimumSize(new java.awt.Dimension(150, 150));
        example.setEditable(false);
        jScrollPane2.setViewportView(example);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
        jPanel6.add(jScrollPane2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jPanel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(bottomSeparator, gridBagConstraints);

        jLabel7.setLabelFor(jScrollPane5);
        jLabel7.setText("View properties");
        jLabel7.setToolTipText("These are readonly properties, you have to edit the rulesets properties to change them. Consult http://pmd.sf.net for more information");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jLabel7, gridBagConstraints);

        jScrollPane5.setToolTipText("These are readonly properties, you have to edit the rulesets properties to change them. Consult http://pmd.sf.net for more information");
        jScrollPane5.setPreferredSize(new java.awt.Dimension(600, 50));
        jScrollPane5.setMinimumSize(new java.awt.Dimension(300, 50));
        properties.setModel(new PropertiesModel(null));
        jScrollPane5.setViewportView(properties);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jScrollPane5, gridBagConstraints);

    }//GEN-END:initComponents

	/** Fired when the user clicks on the chosenList list
	 * @param evt the event fired
	 */
	private void chosenListMouseClicked(MouseEvent evt) {//GEN-FIRST:event_chosenListMouseClicked
		if( evt.getClickCount() >= 2 ) {
			removeOneActionPerformed();
		}
	}//GEN-LAST:event_chosenListMouseClicked

	/** Fired when the user clicks on the availableList list
	 * @param evt the event fired
	 */
	private void availableListMouseClicked(MouseEvent evt) {//GEN-FIRST:event_availableListMouseClicked
		if( evt.getClickCount() >= 2 ) {
			chooseOneActionPerformed();
		}
	}//GEN-LAST:event_availableListMouseClicked

	/** Called when the user selects a value in the chosenList
	 * @param evt the event fired
	 */
	private void chosenListValueChanged() {//GEN-FIRST:event_chosenListValueChanged
		Rule rule =  (Rule)chosenList.getSelectedValue();
		if( rule != null ) {
			example.setText( rule.getExample().trim());
			information.setText( rule.getDescription().trim() );
			properties.setModel( new PropertiesModel( rule ) );
		}
	}//GEN-LAST:event_chosenListValueChanged

	/** Called when the user selects a value in the availableList
	 * @param evt the event fired
	 */
	private void availableListValueChanged() {//GEN-FIRST:event_availableListValueChanged
		Rule rule =  (Rule)availableList.getSelectedValue();
		if( rule != null ) {
			example.setText( rule.getExample().trim() );
			information.setText( rule.getDescription().trim() );
			properties.setModel( new PropertiesModel( rule ) );
		}
	}//GEN-LAST:event_availableListValueChanged

	/** Called when the user clicks on the removeAll button
	 * @param evt the event fired
	 */
	private void removeAllActionPerformed() {//GEN-FIRST:event_removeAllActionPerformed
		AvailableListModel.getInstance().addAll( SelectedListModel.getSelectedListModelInstance().getList() );
		SelectedListModel.getSelectedListModelInstance().removeAll();
		editor.firePropertyChange();

	}//GEN-LAST:event_removeAllActionPerformed

	/** Called when the user clicks the removeOne button
	 * @param evt the event fired
	 */
	private void removeOneActionPerformed() {//GEN-FIRST:event_removeOneActionPerformed
		int index = chosenList.getSelectedIndex();
		Object object[] = chosenList.getSelectedValues();
		if( object != null ) {
			for( int i = 0; i < object.length; i++ ) {
				SelectedListModel.getSelectedListModelInstance().remove( object[i] );
				AvailableListModel.getInstance().addRule( object[i] );
			}
			editor.firePropertyChange();
			if( index >= SelectedListModel.getSelectedListModelInstance().getList().size() ) {
				index = SelectedListModel.getSelectedListModelInstance().getList().size() - 1;
			}
			if( index >= 0 ) {
				chosenList.setSelectedIndex( index );
				chosenList.requestFocus();
			}
		}

	}//GEN-LAST:event_removeOneActionPerformed

	/** Called when the user clicks on chooseAll button
	 * @param evt the event fired
	 */
	private void choseAllActionPerformed() {//GEN-FIRST:event_choseAllActionPerformed
		SelectedListModel.getSelectedListModelInstance().addAll( AvailableListModel.getInstance().getList() );
		AvailableListModel.getInstance().removeAll();
		editor.firePropertyChange();
	}//GEN-LAST:event_choseAllActionPerformed

	/** Called when the user clicks on chooseOne button
	 * @param evt the event fired
	 */
	private void chooseOneActionPerformed() {//GEN-FIRST:event_chooseOneActionPerformed
		int index = availableList.getSelectedIndex();
		Object object[] = availableList.getSelectedValues();
		if( object != null ) {
			for( int i = 0; i < object.length; i++ ) {
				AvailableListModel.getInstance().remove( object[i] );
				SelectedListModel.getSelectedListModelInstance().addRule( object[i] );
			}
			editor.firePropertyChange();
			if( index >= AvailableListModel.getInstance().getList().size() ) {
				index = AvailableListModel.getInstance().getList().size() - 1;
			}
			if( index >= 0 ) {
				availableList.setSelectedIndex( index );
				availableList.requestFocus();
			}
		}

	}//GEN-LAST:event_chooseOneActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSeparator topSeparator;
    private javax.swing.JButton removeAll;
    private javax.swing.JList availableList;
    private javax.swing.JButton chooseOne;
    private javax.swing.JTable properties;
    private javax.swing.JButton choseAll;
    private javax.swing.JEditorPane example;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JEditorPane information;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSeparator middleSeparator;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator bottomSeparator;
    private javax.swing.JButton removeOne;
    private javax.swing.JList chosenList;
    // End of variables declaration//GEN-END:variables

}
