package br.ufes.inf.ontoumlplugin.actions;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;

import br.ufes.inf.ontoumlplugin.model.RefOntoUMLWrapper;

public class ValidateOntoUMLModelController implements VPActionController {

	@Override
	public void performAction(VPAction arg0) {
		// TODO Auto-generated method stub
		IProject project = ApplicationManager
							.instance()
							.getProjectManager()
							.getProject();
		

        IDiagramUIModel diagram = ApplicationManager
                                    .instance()
                                    .getDiagramManager()
                                    .getActiveDiagram();
        
        RefOntoUMLWrapper wrapper = RefOntoUMLWrapper.createRefOntoUMLModel(diagram);
		
		/*RefOntoUML.Kind k = RefOntoUMLFactory.eINSTANCE.createKind();
		k.setName("Person");
		RefOntoUML.Role r = RefOntoUMLFactory.eINSTANCE.createRole();
		r.setName("Student");*/
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub

	}
	
}
