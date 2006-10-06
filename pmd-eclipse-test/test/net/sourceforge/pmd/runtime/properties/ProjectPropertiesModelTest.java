/*
 * Created on 6 fevr. 2005
 *
 * Copyright (c) 2004, PMD for Eclipse Development Team
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
package net.sourceforge.pmd.runtime.properties;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.core.PMDCorePlugin;
import net.sourceforge.pmd.eclipse.EclipseUtils;
import net.sourceforge.pmd.runtime.PMDRuntimePlugin;
import net.sourceforge.pmd.runtime.builder.PMDNature;
import net.sourceforge.pmd.runtime.preferences.IPreferencesManager;
import net.sourceforge.pmd.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.runtime.properties.IProjectPropertiesManager;
import net.sourceforge.pmd.runtime.properties.PropertiesException;
import net.sourceforge.pmd.runtime.properties.impl.ProjectPropertiesImpl;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkingSet;


/**
 * Test the project properties model.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2006/10/06 16:42:03  phherlin
 * Continue refactoring of rullesets management
 *
 * Revision 1.1  2006/06/18 22:29:51  phherlin
 * Begin refactoring the unit tests for the plugin
 * Revision 1.4 2005/12/30 16:29:16
 * phherlin Implement a new preferences model and review some tests
 * 
 * Revision 1.3 2005/07/02 14:31:11 phherlin Fixing equals assertion to test on
 * rules collection instead
 * 
 * Revision 1.2 2005/07/01 00:06:38 phherlin Refactoring and writing more tests
 * 
 * Revision 1.1 2005/06/15 21:14:56 phherlin Create the project for the Eclipse
 * plugin unit tests
 * 
 * 
 */
public class ProjectPropertiesModelTest extends TestCase {
    private IProject testProject;
    private RuleSet initialPluginRuleSet;

    /**
     * Test case constructor
     * 
     * @param name of the test case
     */
    public ProjectPropertiesModelTest(String name) {
        super(name);
    }

    /**
     * A property should be used to know if PMD is enabled for a project. Set to
     * TRUE
     * 
     */
    public void testPmdEnabledTRUE() throws CoreException, PropertiesException {
        IProjectPropertiesManager mgr = PMDRuntimePlugin.getDefault().getPropertiesManager();
        IProjectProperties model = mgr.loadProjectProperties(this.testProject);

        model.setPmdEnabled(true);
        model.sync();
        assertTrue("Cannot activate PMD for that project", this.testProject.hasNature(PMDNature.PMD_NATURE));
        assertTrue("PMD Property not set!", model.isPmdEnabled());
    }

    /**
     * A property should be used to know id PMD is enabled for a project. Set to
     * FALSE.
     * 
     */
    public void testPmdEnabledFALSE() throws PropertiesException, CoreException {
        IProjectPropertiesManager mgr = PMDRuntimePlugin.getDefault().getPropertiesManager();
        IProjectProperties model = mgr.loadProjectProperties(this.testProject);

        model.setPmdEnabled(true);
        model.sync();
        assertTrue("Cannot activate PMD for that project", this.testProject.hasNature(PMDNature.PMD_NATURE));

        model.setPmdEnabled(false);
        model.sync();
        assertFalse("Cannot desactivate PMD for that project", this.testProject.hasNature(PMDNature.PMD_NATURE));
        assertFalse("PMD Property not reset!", model.isPmdEnabled());

    }

    /**
     * A brand new project should be affected the Plugin ruleset in the global
     * ruleset.
     * 
     */
    public void testProjectRuleSet() throws PropertiesException {
        IProjectPropertiesManager mgr = PMDRuntimePlugin.getDefault().getPropertiesManager();
        IProjectProperties model = mgr.loadProjectProperties(this.testProject);

        IPreferencesManager pmgr = PMDRuntimePlugin.getDefault().getPreferencesManager();

        assertSame("A new project is not set the plugin ruleset", model.getProjectRuleSet(), pmgr.getRuleSet());
    }

    /**
     * Set another ruleset.
     */
    public void testProjectRuleSet1() throws PropertiesException, RuleSetNotFoundException, CoreException {
        IProjectPropertiesManager mgr = PMDRuntimePlugin.getDefault().getPropertiesManager();
        IProjectProperties model = mgr.loadProjectProperties(this.testProject);

        RuleSetFactory factory = new RuleSetFactory();

        // use the basic ruleset because it should be included in the plugin
        // ruleset.
        RuleSet basicRuleSet = factory.createSingleRuleSet("rulesets/basic.xml");

        // First set the project ruleset
        model.setProjectRuleSet(basicRuleSet);
        model.sync();

        // Test the ruleset we set is equal to the ruleset we queried
        RuleSet projectRuleSet = model.getProjectRuleSet();
        assertNotNull("Project ruleset has not been set", projectRuleSet);
        assertTrue("The project ruleset is not the basic ruleset", EclipseUtils.assertRuleSetEquals(basicRuleSet.getRules(),
                projectRuleSet.getRules()));
    }

    /**
     * When rules are removed from the plugin preferences, these rules should
     * also be removed from the project
     * euh... ben en fait non. annulé.
     */
    public void testProjectRuleSet2() throws PropertiesException, RuleSetNotFoundException, CoreException {
/*
        // First ensure that the plugin initial ruleset is equal to the project
        // ruleset
        IProjectPropertiesManager mgr = PMDRuntimePlugin.getDefault().getPropertiesManager();
        IProjectProperties model = mgr.loadProjectProperties(this.testProject);

        RuleSet projectRuleSet = model.getProjectRuleSet();
        assertEquals("The project ruleset is not equal to the plugin ruleset", this.initialPluginRuleSet.getRules(), projectRuleSet
                .getRules());

        // use the basic ruleset and set it at the only plugin ruleset
        RuleSetFactory factory = new RuleSetFactory();
        RuleSet basicRuleSet = factory.createSingleRuleSet("rulesets/basic.xml");

        IPreferencesManager pmgr = PMDRuntimePlugin.getDefault().getPreferencesManager();
        pmgr.setRuleSet(basicRuleSet);

        projectRuleSet = model.getProjectRuleSet();

        dumpRuleSet(basicRuleSet);
        dumpRuleSet(projectRuleSet);
        assertEquals("The project ruleset is not equal to the plugin ruleset", basicRuleSet.getRules(), projectRuleSet.getRules());
*/    
    }

    /**
     * When rules are added to the plugin preferences, these rules should also
     * be added to the project
     */
    public void testProjectRuleSet3() throws PropertiesException, RuleSetNotFoundException, CoreException {

        // First ensure that the plugin initial ruleset is equal to the project
        // ruleset
        IProjectPropertiesManager mgr = PMDRuntimePlugin.getDefault().getPropertiesManager();
        IProjectProperties model = mgr.loadProjectProperties(this.testProject);

        RuleSet projectRuleSet = model.getProjectRuleSet();
        assertEquals("The project ruleset is not equal to the plugin ruleset", this.initialPluginRuleSet.getRules(), projectRuleSet.getRules());

        // 2. add a rule to the plugin rule set
        Rule myRule = new AbstractRule() {
            public String getName() {
                return "MyRule";
            }
        };

        RuleSet newRuleSet = new RuleSet();
        newRuleSet.setName("foo");
        newRuleSet.addRuleSet(this.initialPluginRuleSet);
        newRuleSet.addRule(myRule);
        PMDRuntimePlugin.getDefault().getPreferencesManager().setRuleSet(newRuleSet);

        // Test that the project rule set should still be the same as the plugin
        // rule set
        model = mgr.loadProjectProperties(this.testProject);
        projectRuleSet = model.getProjectRuleSet();
        assertEquals("The project ruleset is not equal to the plugin ruleset", PMDRuntimePlugin.getDefault()
                .getPreferencesManager().getRuleSet().getRules(), projectRuleSet.getRules());
    }

    /**
     * Bug: when a user deselect a project rule it is not saved
     */
    public void testBug() throws PropertiesException, RuleSetNotFoundException, CoreException {
        RuleSetFactory factory = new RuleSetFactory();

        // First ensure that the plugin initial ruleset is equal to the project
        // ruleset
        IProjectPropertiesManager mgr = PMDRuntimePlugin.getDefault().getPropertiesManager();
        IProjectProperties model = mgr.loadProjectProperties(this.testProject);

        RuleSet projectRuleSet = model.getProjectRuleSet();
        assertEquals("The project ruleset is not equal to the plugin ruleset", this.initialPluginRuleSet.getRules(), projectRuleSet.getRules());

        // 2. remove the first rule (keep its name for assertion)
        RuleSet newRuleSet = new RuleSet();
        newRuleSet.addRuleSet(projectRuleSet);
        Iterator i = newRuleSet.getRules().iterator();
        Rule removedRule = (Rule) i.next();
        i.remove();

        model.setProjectRuleSet(newRuleSet);
        model.sync();

        // 3. test the rule has correctly been removed
        projectRuleSet = model.getProjectRuleSet();
        assertNull("The rule has not been removed!", projectRuleSet.getRuleByName(removedRule.getName()));
    }

    /**
     * It should not be possible to set to null a project ruleset
     * 
     */
    public void testProjectRuleSetNull() throws PropertiesException {
        IProjectPropertiesManager mgr = PMDRuntimePlugin.getDefault().getPropertiesManager();
        IProjectProperties model = mgr.loadProjectProperties(this.testProject);

        try {
            model.setProjectRuleSet(null);
            fail("A ModelException must be raised when setting a project ruleset to null");
        } catch (PropertiesException e) {
            // OK that's correct
        }

    }

    /**
     * A project may have its ruleset stored in the project own directory. Test
     * set to TRUE.
     */
    public void testRuleSetStoredInProjectTRUE() throws PropertiesException, RuleSetNotFoundException {
        IProjectPropertiesManager mgr = PMDRuntimePlugin.getDefault().getPropertiesManager();
        IProjectProperties model = mgr.loadProjectProperties(this.testProject);

        RuleSetFactory factory = new RuleSetFactory();
        RuleSet basicRuleSet = factory.createSingleRuleSet("rulesets/basic.xml");
        model.setPmdEnabled(true);
        model.setRuleSetStoredInProject(false);
        model.setProjectWorkingSet(null);
        model.setProjectRuleSet(basicRuleSet);
        model.sync();

        model.createDefaultRuleSetFile();
        model.setRuleSetStoredInProject(true);
        model.sync();

        boolean b = model.isRuleSetStoredInProject();
        IFile file = this.testProject.getFile(".ruleset");
        RuleSet projectRuleSet = factory.createSingleRuleSet(file.getLocation().toOSString());
        assertTrue("the ruleset should be stored in the project", b);
        assertEquals("The project ruleset must be equals to the one found in the project", model.getProjectRuleSet(),
                projectRuleSet);
    }

    /**
     * A project may have its ruleset stored in the project own directory. Test
     * set to FALSE.
     * 
     */
    public void testRuleSetStoredInProjectFALSE() throws PropertiesException, RuleSetNotFoundException {
        IProjectPropertiesManager mgr = PMDRuntimePlugin.getDefault().getPropertiesManager();
        IProjectProperties model = mgr.loadProjectProperties(this.testProject);

        RuleSetFactory factory = new RuleSetFactory();
        RuleSet basicRuleSet = factory.createSingleRuleSet("rulesets/basic.xml");
        model.setPmdEnabled(true);
        model.setRuleSetStoredInProject(false);
        model.setProjectWorkingSet(null);
        model.setProjectRuleSet(basicRuleSet);
        model.sync();

        model.createDefaultRuleSetFile();
        model.setRuleSetStoredInProject(true);
        model.sync();

        model.setRuleSetStoredInProject(false);
        model.sync();
        boolean b = model.isRuleSetStoredInProject();
        assertFalse("the ruleset should'nt be stored in the project", b);
    }

    /**
     * A project may work only on a subset of files defined by a working set
     * 
     */
    public void testProjectWorkingSetNull() throws PropertiesException {
        IProjectPropertiesManager mgr = PMDRuntimePlugin.getDefault().getPropertiesManager();
        IProjectProperties model = mgr.loadProjectProperties(this.testProject);

        model.setProjectWorkingSet(null);
        IWorkingSet w = model.getProjectWorkingSet();
        assertNull("The project should not have a working set defined", w);
    }

    /**
     * A project may know if it should be rebuilt or not
     * 
     */
    public void testRebuild1() throws PropertiesException {
        IProjectPropertiesManager mgr = PMDRuntimePlugin.getDefault().getPropertiesManager();
        IProjectProperties model = mgr.loadProjectProperties(this.testProject);

        model.setPmdEnabled(false);
        model.setProjectWorkingSet(null);
        model.setRuleSetStoredInProject(false);
        model.setNeedRebuild(false);
        assertFalse(model.isNeedRebuild());
    }

    /**
     * A project may know if it should be rebuilt or not
     * 
     */
    public void testRebuild2() throws PropertiesException {
        IProjectPropertiesManager mgr = PMDRuntimePlugin.getDefault().getPropertiesManager();
        IProjectProperties model = mgr.loadProjectProperties(this.testProject);

        model.setPmdEnabled(true);
        assertTrue(model.isNeedRebuild());
    }

    /**
     * A project may know if it should be rebuilt or not
     * 
     */
    public void testRebuild3() throws PropertiesException {
        IProjectPropertiesManager mgr = PMDRuntimePlugin.getDefault().getPropertiesManager();
        IProjectProperties model = mgr.loadProjectProperties(this.testProject);

        model.setPmdEnabled(true);

        RuleSet pmdRuleSet = PMDRuntimePlugin.getDefault().getPreferencesManager().getRuleSet();
        RuleSet fooRuleSet = new RuleSet();

        Rule rule1 = pmdRuleSet.getRuleByName("EmptyCatchBlock");

        fooRuleSet.addRule(rule1);

        model.setProjectRuleSet(fooRuleSet);
        assertTrue(model.isNeedRebuild());
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        // 1. Create a Java project
        this.testProject = EclipseUtils.createJavaProject("PMDTestProject");
        assertTrue("A test project cannot be created; the tests cannot be performed.", (this.testProject != null)
                && this.testProject.exists() && this.testProject.isAccessible());

        // 2. Keep the plugin ruleset
        this.initialPluginRuleSet = PMDRuntimePlugin.getDefault().getPreferencesManager().getRuleSet();
        this.initialPluginRuleSet.getRules().clear();
        Set defaultRuleSets = PMDCorePlugin.getDefault().getRuleSetManager().getDefaultRuleSets();
        for (Iterator i = defaultRuleSets.iterator(); i.hasNext();) {
            this.initialPluginRuleSet.addRuleSet((RuleSet) i.next());
        }
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        // 1. Delete the test project
        if (this.testProject != null) {
            if (this.testProject.exists() && this.testProject.isAccessible()) {
                this.testProject.delete(true, true, null);
                this.testProject = null;
            }
        }

        // 2. Restore the plugin initial rule set
        PMDRuntimePlugin.getDefault().getPreferencesManager().setRuleSet(this.initialPluginRuleSet);

        super.tearDown();
    }

    private void dumpRuleSet(RuleSet ruleSet) {
        System.out.println("Dumping rule set:" + ruleSet.getName());
        Iterator i = ruleSet.getRules().iterator();
        while (i.hasNext()) {
            Rule rule = (Rule) i.next();
            System.out.println(rule.getName());
        }
        System.out.println();
    }
    
}