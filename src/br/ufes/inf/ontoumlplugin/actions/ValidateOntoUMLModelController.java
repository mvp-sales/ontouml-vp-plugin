package br.ufes.inf.ontoumlplugin.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;

import RefOntoUML.util.RefOntoUMLResourceUtil;
import br.ufes.inf.ontoumlplugin.model.RefOntoUMLWrapper;
import io.reactivex.schedulers.Schedulers;

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
        
        //RefOntoUMLWrapper wrapper = RefOntoUMLWrapper.createRefOntoUMLModel(diagram);
        
        RefOntoUMLWrapper
        	.createObservableWrapper(diagram)
        	.observeOn(Schedulers.computation())
        	.flatMap(wrapper -> RefOntoUMLWrapper.getVerificator(wrapper))
        	.observeOn(Schedulers.trampoline())
        	.subscribe(
        		verificator -> {
        			ViewManager viewManager = ApplicationManager.instance().getViewManager();
        			viewManager.showMessage(verificator.getResult());
         		}
			);
        	
	
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub

	}
	
}
