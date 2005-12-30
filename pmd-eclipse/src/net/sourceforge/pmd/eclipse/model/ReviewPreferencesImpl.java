/*
 * Created on 27 déc. 2005
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

package net.sourceforge.pmd.eclipse.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Default implementation of the ReviewPreferences interface
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2005/12/30 16:26:30  phherlin
 * Implement a new preferences model
 *
 *
 */

public class ReviewPreferencesImpl implements ReviewPreferences {
    private static final Log log = LogFactory.getLog(ReviewPreferencesImpl.class);
    
    private String noPmdString;
    private String additionalComment;

    /**
     * @see net.sourceforge.pmd.eclipse.model.ReviewPreferences#getAdditionalComment()
     */
    public String getAdditionalComment() throws ModelException {
        if (this.additionalComment == null) {
            this.additionalComment = REVIEW_ADDITIONAL_COMMENT_DEFAULT;
        }
        
        return this.additionalComment;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ReviewPreferences#setAdditionalComment(java.lang.String)
     */
    public void setAdditionalComment(String comment) throws ModelException {
        this.additionalComment = comment;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ReviewPreferences#getNoPmdString()
     */
    public String getNoPmdString() throws ModelException {
        if (this.noPmdString == null) {
            this.noPmdString = NO_PMD_STRING_DEFAULT;
        }
        
        return this.noPmdString;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ReviewPreferences#setNoPmdString(java.lang.String)
     */
    public void setNoPmdString(String noPmdString) throws ModelException {
        this.noPmdString = noPmdString;
    }
}
