package br.ufes.inf.ontoumlplugin.actions;

import java.io.File;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;

import RefOntoUML.util.RefOntoUMLResourceUtil;
import br.ufes.inf.ontoumlplugin.model.RefOntoUMLWrapper;
import io.reactivex.schedulers.Schedulers;

public class ConvertModel2RefOntoUMLController implements VPActionController {

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
		
		RefOntoUMLWrapper
		.createObservableWrapper(diagram)
		.subscribeOn(Schedulers.computation())
		.observeOn(Schedulers.trampoline())
		.subscribe(
			wrapper -> {
				File file = new File("/home/mvp-sales/Documentos/teste.refontouml"); 
                RefOntoUMLResourceUtil.saveModel(file.getAbsolutePath(), wrapper.ontoUmlPackage);
			}
		);
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub
		
	}
	
	

}
