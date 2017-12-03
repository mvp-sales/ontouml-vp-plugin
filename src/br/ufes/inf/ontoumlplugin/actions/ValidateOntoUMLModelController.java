package br.ufes.inf.ontoumlplugin.actions;


import RefOntoUML.Element;
import br.ufes.inf.ontoumlplugin.OntoUMLPlugin;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import br.ufes.inf.ontoumlplugin.utils.CommonUtils;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;

import br.ufes.inf.ontoumlplugin.model.RefOntoUMLWrapper;
import com.vp.plugin.model.IProject;
import io.reactivex.schedulers.Schedulers;

public class ValidateOntoUMLModelController implements VPActionController, VPContextActionController {
	
	public static void validateModel() {
		IProject project = ApplicationManager.instance().getProjectManager().getProject();
        
        ViewManager viewManager = ApplicationManager.instance().getViewManager();
        viewManager.clearMessages(OntoUMLPlugin.PLUGIN_ID);
        viewManager.removeMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID);     
        
        RefOntoUMLWrapper
        	.createObservableWrapper(project)
        	.subscribeOn(Schedulers.computation())
        	.flatMap(RefOntoUMLWrapper::getVerificator)
        	.observeOn(Schedulers.trampoline())
        	.subscribe(
        		verificator -> {
        			Map<String, ArrayList<String>> erroredElements = new HashMap<>();
					for(Map.Entry<Element, ArrayList<String>> entry: verificator.getMap().entrySet()){
						erroredElements.put(entry.getKey().toString(), entry.getValue());
					}
					CommonUtils.showModelErrors(verificator.getTimingMessage(), erroredElements, viewManager);
				},
        		err -> viewManager.showMessage(err.getMessage(), OntoUMLPlugin.PLUGIN_ID)
			);
	}

	@Override
	public void performAction(VPAction arg0) {
		validateModel();
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performAction(VPAction arg0, VPContext arg1, ActionEvent arg2) {
		validateModel();
	}

	@Override
	public void update(VPAction arg0, VPContext arg1) {
		// TODO Auto-generated method stub
		
	}
	
}
