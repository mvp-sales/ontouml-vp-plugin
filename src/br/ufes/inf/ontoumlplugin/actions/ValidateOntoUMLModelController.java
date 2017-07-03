package br.ufes.inf.ontoumlplugin.actions;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.ISimpleRelationship;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.factory.IModelElementFactory;

import br.ufes.inf.ontoumlplugin.rules.OntoUMLRules;

public class ValidateOntoUMLModelController implements VPActionController {

	@Override
	public void performAction(VPAction arg0) {
		// TODO Auto-generated method stub
		IProject project = ApplicationManager
							.instance()
							.getProjectManager()
							.getProject();
		
		for(IModelElement element : 
				project.toAllLevelModelElementArray
							(IModelElementFactory.MODEL_TYPE_CLASS))
		{
			OntoUMLRules.validateOntoUMLClass((IClass)element);
		}

	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub

	}
	
}
