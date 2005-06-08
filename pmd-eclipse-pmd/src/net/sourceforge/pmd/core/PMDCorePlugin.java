/*
 * Created on 7 juin 2005
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
package net.sourceforge.pmd.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.core.impl.RuleSetManagerImpl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

/**
 * The plugin class for the Core PMD Plugin.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2005/06/07 22:39:57  phherlin
 * Implementing extra ruleset declaration
 *
 *
 */
public class PMDCorePlugin extends Plugin {
    private static PMDCorePlugin pluginInstance; // NOPMD:AssignmentToNonFinalStatic
    private final IRuleSetManager ruleSetManager = new RuleSetManagerImpl(); // NOPMD:SingularField

    /**
     * Default constructor
     */
    public PMDCorePlugin() {
        super();
        pluginInstance = this;
        this.registerStandardRuleSets();
        this.registerAdditionalRuleSets();
    }
    
    /**
     * Get the plugin instance
     * @return the plugin instance
     */
    public static PMDCorePlugin getDefault() {
        return pluginInstance;
    }
    
    /**
     * @return the ruleset manager instance
     */
    public IRuleSetManager getRuleSetManager() {
        return this.ruleSetManager;
    }
    
    /**
     * Logs inside the Eclipse environment
     * @param severity the severity of the log (IStatus code)
     * @param message the message to log
     * @param t a possible throwable, may be null
     */
    public final void log(final int severity, final String message, final Throwable t) {
        this.getLog().log(new Status(severity, getBundle().getSymbolicName(), 0, message, t));
    }
    
    /**
     * Registering the standard rulesets
     *
     */
    private void registerStandardRuleSets() {
        final RuleSetFactory factory = new RuleSetFactory();
        for (int i = 0; i < PluginConstants.PMD_RULESETS.length; i++) {
            try {
                final RuleSet ruleSet = factory.createRuleSet(PluginConstants.PMD_RULESETS[i]);
                this.getRuleSetManager().registerRuleSet(ruleSet);
            } catch (RuleSetNotFoundException e) {
                this.log(IStatus.WARNING, "The RuleSet \"" + PluginConstants.PMD_RULESETS[i] + "\" cannot be found", e);
            }
        }
    }

    /**
     * Register additional rulesets that may be provided by a fragment.
     * First look for a resource "rulesets/additional". This resource should
     * contains a list of rulesets to load.
     *
     */
    private void registerAdditionalRuleSets() {
        final InputStream is = this.getClass().getResourceAsStream(PluginConstants.ADDITIONAL_RULESETS_LIST_FILE);
        if (is != null) {
            String ruleSetFile = null;
            try {
                final RuleSetFactory factory = new RuleSetFactory();
                final BufferedReader br = new BufferedReader(new InputStreamReader(is));
                while (br.ready()) {
                    ruleSetFile = br.readLine();
                    final RuleSet ruleSet = factory.createRuleSet(ruleSetFile);
                    this.getRuleSetManager().registerRuleSet(ruleSet);
                }
                br.close();
            } catch (IOException e) {
                this.log(IStatus.ERROR, "Error while reading the additional rulesets declaration file", e);
            } catch (RuleSetNotFoundException e) {
                this.log(IStatus.ERROR, "The additional ruleset \"" + ruleSetFile + "\" cannot be found", e);
            }
        }
    }
}