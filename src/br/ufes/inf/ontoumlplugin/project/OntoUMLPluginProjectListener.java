package br.ufes.inf.ontoumlplugin.project;

import br.ufes.inf.ontoumlplugin.actions.CheckOntoUMLModelController;

import br.ufes.inf.ontoumlplugin.utils.CommonUtils;
import com.vp.plugin.model.*;

/**
 * Created by mvp-sales on 15/06/17.
 */
public class OntoUMLPluginProjectListener implements IProjectListener {

    private int stateProjectListener = UNSPECIFIED;
    private static final int UNSPECIFIED = 0;
    private static final int NEWED = 1;
    private static final int PRESAVE = 2;
    private static final int SAVED = 3;
    private static final int RENAMED = 4;
    private static final int OPENED = 5;
    private static final int AFTER_OPENED = 6;

    @Override
    public void projectNewed(IProject iProject) {
    	stateProjectListener = NEWED;
    }

    @Override
    public void projectOpened(IProject iProject) {
    	stateProjectListener = OPENED;
    }

    @Override
    public void projectAfterOpened(IProject iProject) {
    	stateProjectListener = AFTER_OPENED;
    }

    @Override
    public void projectPreSave(IProject iProject) {
    	stateProjectListener = PRESAVE;
    }

    @Override
    public void projectSaved(IProject iProject) {
    	stateProjectListener = SAVED;
    	CheckOntoUMLModelController.validateModel();
    }

    @Override
    public void projectRenamed(IProject iProject) {
    	if(stateProjectListener == NEWED) {
            CommonUtils.addOntoUMLStereotypes(iProject);
    	}
    	stateProjectListener = RENAMED;
    }
}
