/*
 * Created on 8 juil. 2005
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

package net.sourceforge.pmd.eclipse.preferences;

import java.util.Comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Sorter for the rule table in the PMD Preference page
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2005/07/12 16:34:25  phherlin
 * RFE#1231112-Make the rule table columns sortable (thanks to Brian R)
 *
 *
 */

public class RuleTableViewerSorter extends ViewerSorter {
    private Comparator comparator;
    private boolean sortDescending = false;
    
    /**
     * Constructor
     * @param comparator the initial comparator
     */
    public RuleTableViewerSorter(Comparator comparator) {
        this.comparator = comparator;
    }
    
    /**
     * @return Returns the sortDescending.
     */
    public boolean isSortDescending() {
        return this.sortDescending;
    }
    
    /**
     * @param sortDescending The sortDescending to set.
     */
    public void setSortDescending(boolean sortDescending) {
        this.sortDescending = sortDescending;
    }
    
    /**
     * @return Returns the comparator.
     */
    public Comparator getComparator() {
        return this.comparator;
    }
    
    /**
     * Set a comparator. If the same comparator is already set, then change
     * the sorting order.
     * @param comparator The comparator to set.
     */
    public void setComparator(Comparator comparator) {
        if (this.comparator != comparator) {
            this.comparator = comparator;
        } else {
            this.sortDescending = !sortDescending;
        }
    }

    /**
     * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public int compare(Viewer viewer, Object e1, Object e2) {
        int result = this.comparator.compare(e1,e2);
        return this.sortDescending ? 0 - result : result;
    }

    
}
