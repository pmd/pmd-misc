/*
 * Created on 24 nov. 2004
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
package net.sourceforge.pmd.eclipse.model;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.cmd.QueryPmdEnabledPropertyCmd;
import net.sourceforge.pmd.eclipse.cmd.QueryProjectRuleSetCmd;
import net.sourceforge.pmd.eclipse.cmd.QueryProjectWorkingSetCmd;
import net.sourceforge.pmd.eclipse.cmd.QueryRuleSetStoredInProjectPropertyCmd;
import net.sourceforge.pmd.eclipse.cmd.UpdatePmdEnabledPropertyCmd;
import net.sourceforge.pmd.eclipse.cmd.UpdateProjectRuleSetCmd;
import net.sourceforge.pmd.eclipse.cmd.UpdateProjectWorkingSetCmd;
import net.sourceforge.pmd.eclipse.cmd.UpdateRuleSetStoredInProjectPropertyCmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkingSet;

/**
 * Default implementation of a ProjectPropertiesModel 
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2004/12/03 00:22:43  phherlin
 * Continuing the refactoring experiment.
 * Implement the Command framework.
 * Refine the MVC pattern usage.
 *
 * Revision 1.1  2004/11/28 20:31:38  phherlin
 * Continuing the refactoring experiment
 *
 *
 */
public class ProjectPropertiesModelImpl implements ProjectPropertiesModel {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.model.ProjectPropertiesModelImpl");
    private IProject project;
    private boolean needRebuild;
    private boolean ruleSetFileExist;
    
    private boolean pmdEnabled;
    private boolean pmdEnabledInit;
    private boolean ruleSetStoredInProject;
    private boolean ruleSetStoredInProjectInit;
    private RuleSet projectRuleSet;
    private boolean projectRuleSetInit;
    private IWorkingSet projectWorkingSet;
    private boolean projectWorkingSetInit;

    /**
     * The default constructor takes a project as an argument
     */
    public ProjectPropertiesModelImpl(IProject project) {
        this.project = project;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#getProject()
     */
    public IProject getProject() {
        return this.project;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#isPmdEnabled()
     */
    public boolean isPmdEnabled() throws ModelException {
        if (!this.pmdEnabledInit) {
            try {
                QueryPmdEnabledPropertyCmd cmd = new QueryPmdEnabledPropertyCmd();
                cmd.setProject(this.project);
                cmd.performExecute();
                this.pmdEnabled = cmd.isPmdEnabled();
                this.pmdEnabledInit = true;
            } catch (CommandException e) {
                throw new ModelException(e.getMessage(), e);
            }                    
        }
        
        return this.pmdEnabled;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#setPmdEnabled(boolean)
     */
    public void setPmdEnabled(boolean pmdEnabled) throws ModelException {
        log.info("Enable PMD for project " + this.project.getName() + " : " + pmdEnabled);
        try {
            UpdatePmdEnabledPropertyCmd cmd = new UpdatePmdEnabledPropertyCmd();
            cmd.setPmdEnabled(pmdEnabled);
            cmd.setProject(this.project);
            cmd.performExecute();
            this.pmdEnabled = pmdEnabled;
            this.pmdEnabledInit = true;
            this.needRebuild |= cmd.isNeedRebuild();
            log.debug("   Project need rebuild : " + this.needRebuild);
        } catch (CommandException e) {
            throw new ModelException(e.getMessage(), e);
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#getProjectRuleSet()
     */
    public RuleSet getProjectRuleSet() throws ModelException {
        if (!this.projectRuleSetInit) {        
            try {
                QueryProjectRuleSetCmd cmd = new QueryProjectRuleSetCmd();
                cmd.setProject(this.project);
                cmd.performExecute();
                this.projectRuleSet = cmd.getProjectRuleSet();
                this.projectRuleSetInit = true;
            } catch (CommandException e) {
                throw new ModelException(e.getMessage(), e);
            }
        }
        
        return this.projectRuleSet;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#setProjectRuleSet(net.sourceforge.pmd.RuleSet)
     */
    public void setProjectRuleSet(RuleSet projectRuleSet) throws ModelException {
        log.info("Set rule set for project " + this.project.getName());
        try {
            UpdateProjectRuleSetCmd cmd = new UpdateProjectRuleSetCmd();
            cmd.setProject(this.project);
            cmd.setProjectRuleSet(projectRuleSet);
            cmd.performExecute();
            this.projectRuleSet = projectRuleSet;
            this.projectRuleSetInit = true;
            this.needRebuild |= cmd.isNeedRebuild();
            log.debug("   Project need rebuild : " + this.needRebuild);
        } catch (CommandException e) {
            throw new ModelException(e.getMessage(), e);
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#isRuleSetStoredInProject()
     */
    public boolean isRuleSetStoredInProject() throws ModelException {
        if (!this.ruleSetStoredInProjectInit) {
            try {
                QueryRuleSetStoredInProjectPropertyCmd cmd = new QueryRuleSetStoredInProjectPropertyCmd();
                cmd.setProject(this.project);
                cmd.performExecute();
                this.ruleSetStoredInProject = cmd.isRuleSetStoredInProject();
                this.ruleSetStoredInProjectInit = true;
            } catch (CommandException e) {
                throw new ModelException(e.getMessage(), e);
            }
        }
        
        return this.ruleSetStoredInProject;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#setRuleSetStoredInProject(boolean)
     */
    public void setRuleSetStoredInProject(boolean ruleSetStoredInProject) throws ModelException {
        log.info("Set rule set stored in project for project " + this.project.getName() + " : " + ruleSetStoredInProject);
        try {
            UpdateRuleSetStoredInProjectPropertyCmd cmd = new UpdateRuleSetStoredInProjectPropertyCmd();
            cmd.setProject(this.project);
            cmd.setRuleSetStoredInProject(ruleSetStoredInProject);
            cmd.performExecute();
            this.ruleSetStoredInProject = ruleSetStoredInProject;
            this.ruleSetStoredInProjectInit = true;
            this.needRebuild |= cmd.isNeedRebuild();
            this.ruleSetFileExist = cmd.isRuleSetFileExists();
            log.debug("   Project need rebuild : " + this.needRebuild);
        } catch (CommandException e) {
            throw new ModelException(e.getMessage(), e);
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#getProjectWorkingSet()
     */
    public IWorkingSet getProjectWorkingSet() throws ModelException {
        if (!this.projectWorkingSetInit) {
            try {
                QueryProjectWorkingSetCmd cmd = new QueryProjectWorkingSetCmd();
                cmd.setProject(this.project);
                cmd.performExecute();
                this.projectWorkingSet = cmd.getProjectWorkingSet();
                this.projectWorkingSetInit = true;
            } catch (CommandException e) {
                throw new ModelException(e.getMessage(), e);
            }
        }
        
        return this.projectWorkingSet;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#setProjectWorkingSet(org.eclipse.ui.IWorkingSet)
     */
    public void setProjectWorkingSet(IWorkingSet projectWorkingSet) throws ModelException {
        log.info("Set working set for project " + this.project.getName() + " : " + (projectWorkingSet == null ? "none" : projectWorkingSet.getName()));
        try {
            UpdateProjectWorkingSetCmd cmd = new UpdateProjectWorkingSetCmd();
            cmd.setProject(this.project);
            cmd.setProjectWorkingSet(projectWorkingSet);
            cmd.performExecute();
            this.projectWorkingSet = projectWorkingSet;
            this.projectWorkingSetInit = true;
            this.needRebuild |= cmd.isNeedRebuild();
            log.debug("   Project need rebuild : " + this.needRebuild);
        } catch (CommandException e) {
            throw new ModelException(e.getMessage(), e);
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#isNeedRebuild()
     */
    public boolean isNeedRebuild() {
        log.debug("Query if project " + this.project.getName() + " need rebuild : " + (this.pmdEnabled && this.needRebuild));
        log.debug("   PMD Enabled = " + this.pmdEnabled);
        log.debug("   Project need rebuild = " + this.needRebuild);
        return this.pmdEnabled && this.needRebuild;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#setNeedRebuild()
     */
    public void setNeedRebuild(boolean needRebuild) {
        this.needRebuild = needRebuild;
    }
    
    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#isRuleSetFileExist()
     */
    public boolean isRuleSetFileExist() {
        return this.ruleSetFileExist;
    }
}