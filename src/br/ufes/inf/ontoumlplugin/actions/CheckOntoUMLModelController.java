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

public class CheckOntoUMLModelController implements VPActionController, VPContextActionController {
	
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
        		verificator -> CommonUtils.showModelErrors(verificator.getTimingMessage(), verificator.getMap(), viewManager),
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
