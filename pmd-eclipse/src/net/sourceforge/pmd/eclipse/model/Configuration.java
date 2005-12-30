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

package net.sourceforge.pmd.eclipse.model;

import net.sourceforge.pmd.RuleSet;

/**
 * RuleSets configuration.
 * This data holds a sets of rulesets that can be used to
 * check a particular project.
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2005/12/30 16:26:30  phherlin
 * Implement a new preferences model
 *
 * Revision 1.1  2005/10/24 22:41:57  phherlin
 * Refactor preferences management
 *
 *
 */

public interface Configuration {
    /**
     * @return the configuration name
     * @throws ModelException if an error occurs
     */
    String getName() throws ModelException;
    
    /**
     * Sets the configuration name
     * @param name a configuration name
     * @throws ModelException if an error occurs
     */
    void setName(String name) throws ModelException;
    
    /**
     * @return all rulesets contained in that configuration
     * @throws ModelException if an error occurs
     */
    RuleSetProxy[] getRuleSets() throws ModelException;
    
    /**
     * Convinient method to allow to set all configuration rulesets
     * @param ruleSets an array of rulesets
     * @throws ModelException if an error occurs
     */
    void setRuleSets(RuleSetProxy[] ruleSets) throws ModelException;
    
    /**
     * Add a ruleset to that configuration. If the ruleSet already exists,
     * it is replaced.
     * @param ruleSet a rule set
     * @throws ModelException if an error occurs
     */
    void addRuleSet(RuleSetProxy ruleSet) throws ModelException;
    
    /**
     * Remove a ruleset from that configuration.
     * This is not an error to remove a ruleset that does
     * not exist.
     * @param ruleSet the ruleset to remove
     * @throws ModelException if an error occurs
     */
    void removeRuleSet(RuleSetProxy ruleSet) throws ModelException;
    
    /**
     * This is a convenient method that returns all rules from
     * all configuration rulesets in a single ruleset
     * @return a ruleset
     * @throws ModelException if an error occurs
     */
    RuleSet getMergedRuleSet() throws ModelException;
    
    /**
     * @return if this configuration is read only
     * @throws ModelException if an error occurs
     */
    boolean isReadOnly() throws ModelException;
    
    /**
     * Set if that configuration can be modified
     * @param readOnly true to make this configuration unmodifiable
     * @throws ModelException if an error occurs
     */
    void setReadOnly(boolean readOnly) throws ModelException;
}
