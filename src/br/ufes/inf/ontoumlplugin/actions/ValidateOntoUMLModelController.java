package br.ufes.inf.ontoumlplugin.actions;


import br.ufes.inf.ontoumlplugin.OntoUMLPlugin;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import br.ufes.inf.ontoumlplugin.model.RefOntoUMLWrapper;
import com.vp.plugin.model.IProject;
import io.reactivex.schedulers.Schedulers;

public class ValidateOntoUMLModelController implements VPActionController {

	@Override
	public void performAction(VPAction arg0) {

		IProject project = ApplicationManager.instance().getProjectManager().getProject();
        
        ViewManager viewManager = ApplicationManager.instance().getViewManager();
        viewManager.clearMessages(OntoUMLPlugin.PLUGIN_ID);
        
        RefOntoUMLWrapper
        	.createObservableWrapper(project)
        	.observeOn(Schedulers.computation())
        	.flatMap(wrapper -> RefOntoUMLWrapper.getVerificator(wrapper))
        	.observeOn(Schedulers.trampoline())
        	.subscribe(
        		verificator -> {
        			viewManager.showMessage(verificator.getResult(), OntoUMLPlugin.PLUGIN_ID);
        			for(RefOntoUML.Element elem: verificator.getMap().keySet()){
						viewManager.showMessage(elem.toString(), OntoUMLPlugin.PLUGIN_ID);
						for(String message: verificator.getMap().get(elem)){		
							viewManager.showMessage(message, OntoUMLPlugin.PLUGIN_ID);
						}
    				}
         		},
        		err -> viewManager.showMessage(err.getMessage(), OntoUMLPlugin.PLUGIN_ID)
			);
        	
	
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub

	}
	
}
