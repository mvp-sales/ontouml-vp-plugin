package br.ufes.inf.ontoumlplugin.actions;

import java.util.HashMap;
import java.util.Map;

import br.ufes.inf.ontoumlplugin.utils.CommonUtils;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValueDefinition;
import com.vp.plugin.model.ITaggedValueDefinitionContainer;
import com.vp.plugin.model.factory.IModelElementFactory;

import br.ufes.inf.ontoumlplugin.OntoUMLPlugin;

public class AddOntoUMLStereotypesController implements VPActionController {

	@Override
	public void performAction(VPAction arg0) {
		// TODO Auto-generated method stub
		IProject project = ApplicationManager.instance().getProjectManager().getProject();
		if(project != null) {
            CommonUtils.addOntoUMLStereotypes(project);
        }
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub
		
	}

}
