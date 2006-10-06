/*
 * Created on 18 juin 2006
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
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

package net.sourceforge.pmd.core.rulesets.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class is a value objet which composes the structure of a rulesets object.
 * It holds the definition of a rule set which is actually a named collection of
 * rules.
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2006/10/06 16:42:46  phherlin
 * Continue refactoring of rullesets management
 *
 * Revision 1.1  2006/06/21 23:06:41  phherlin
 * Move the new rule sets management to the core plugin instead of the runtime.
 * Continue the development.
 *
 * Revision 1.2  2006/06/20 21:26:42  phherlin
 * Fix/review PMD violations
 *
 * Revision 1.1  2006/06/18 22:33:02  phherlin
 * Begin to implement a new model for the plugin to handle rules and rulesets.
 *
 * 
 */

public class RuleSet {
    /**
     * Constant to specify the rule set is for Java source files. Value is JAVA.
     */
    public static final String LANGUAGE_JAVA = "JAVA";

    /**
     * Constant to specify the rule set is for JSP source files. Value is JSP.
     */
    public static final String LANGUAGE_JSP = "JSP";

    private String name = "";
    private String description = "";
    private String language = LANGUAGE_JAVA;
    final private net.sourceforge.pmd.RuleSet pmdRuleSet = new net.sourceforge.pmd.RuleSet();
    final private Collection rules = new ArrayList();

    /**
     * Getter for the description attribute. May be empty but never null.
     * 
     * @return Returns the description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Setter of the description attribute. Cannot be null but can be empty.
     * 
     * @param description The description to set.
     */
    public void setDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("description cannot be null");
        }

        this.description = description;
    }

    /**
     * Getter for the name attribute. Cannot be null. May be empty if the object
     * has not been initialized.
     * 
     * @return Returns the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter for the name attribute. Cannot be null nor empty
     * 
     * @param name The name to set.
     */
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (name.trim().length() == 0) { // NOPMD by Herlin on 20/06/06 23:25
            throw new IllegalArgumentException("name cannot be empty");
        }

        this.name = name;
    }

    /**
     * Getter for the rules collection attribute. Cannot be null, but may be
     * empty.
     * 
     * @return Returns the rules.
     */
    public Collection getRules() {
        return this.rules;
    }

    /**
     * Add a rule to the rule set.
     * 
     * @param rule The rule to add. Cannot be null
     */
    public void addRule(Rule rule) {
        if (rule == null) {
            throw new IllegalArgumentException("rule cannot be null");
        }

        this.rules.add(rule);
        this.pmdRuleSet.addRule(rule.getPmdRule());
    }

    /**
     * Getter of the language attribute. Is one of the LANGUAGE_xxx constants.
     * 
     * @return Returns the language.
     */
    public String getLanguage() {
        return this.language;
    }

    /**
     * Setter of the language constant. Must be one of the LANGUAGE_xxx
     * constant.
     * 
     * @param language The language to set.
     */
    public void setLanguage(String language) {
        if (!LANGUAGE_JAVA.equals(language) && !LANGUAGE_JSP.equals(language)) {
            throw new IllegalArgumentException("language must be one of the LANGUAGE_xxx constant and found " + language);
        }

        this.language = language;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object arg0) {
        boolean equal = false;

        if (arg0 instanceof RuleSet) {
            final RuleSet rs = (RuleSet) arg0;
            equal = this.name.equals(rs.name) && this.rules.equals(rs.rules) && this.language.equals(rs.language);
        }

        return equal;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return this.name.hashCode() + this.rules.hashCode() * 21 * 21 + this.language.hashCode() * 13 * 13;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        final StringBuffer buffer = new StringBuffer("RuleSet name=" + this.name + " description=" + " language=" + this.language
                + " rules=");
        for (final Iterator i = this.rules.iterator(); i.hasNext();) {
            buffer.append(' ').append(i.next());
        }

        return buffer.toString();
    }

    /**
     * Getter for a PMD RuleSet object.
     * This object is a native PMD Rule Set composed of all rules of this
     * rule set.
     * 
     * @return Returns the pmdRuleSet.
     */
    public net.sourceforge.pmd.RuleSet getPmdRuleSet() {
        return this.pmdRuleSet;
    }
}
