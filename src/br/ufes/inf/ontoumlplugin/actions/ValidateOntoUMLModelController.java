package br.ufes.inf.ontoumlplugin.actions;


import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramUIModel;

import br.ufes.inf.ontoumlplugin.model.RefOntoUMLWrapper;
import io.reactivex.schedulers.Schedulers;

public class ValidateOntoUMLModelController implements VPActionController {

	@Override
	public void performAction(VPAction arg0) {
		

        IDiagramUIModel diagram = ApplicationManager
                                    .instance()
                                    .getDiagramManager()
                                    .getActiveDiagram();
        
        ViewManager viewManager = ApplicationManager.instance().getViewManager();
        
        //RefOntoUMLWrapper wrapper = RefOntoUMLWrapper.createRefOntoUMLModel(diagram);
        
        RefOntoUMLWrapper
        	.createObservableWrapper(diagram)
        	.observeOn(Schedulers.computation())
        	.flatMap(wrapper -> RefOntoUMLWrapper.getVerificator(wrapper))
        	.observeOn(Schedulers.trampoline())
        	.subscribe(
        		verificator -> {
        			for(RefOntoUML.Element elem: verificator.getMap().keySet()){
						viewManager.showMessage(elem.toString());
						for(String message: verificator.getMap().get(elem)){		
							viewManager.showMessage(message);
						}
    				}
         		},
        		err -> {
        			viewManager.showMessage(err.getMessage());
        		}
			);
        	
	
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub

	}
	
}
