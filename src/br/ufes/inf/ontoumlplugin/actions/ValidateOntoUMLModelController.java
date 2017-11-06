package br.ufes.inf.ontoumlplugin.actions;


import br.ufes.inf.ontoumlplugin.OntoUMLPlugin;

import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
        
        RefOntoUMLWrapper
        	.createObservableWrapper(project)
        	.observeOn(Schedulers.computation())
        	.flatMap(wrapper -> RefOntoUMLWrapper.getVerificator(wrapper))
        	.observeOn(Schedulers.trampoline())
        	.subscribe(
        		verificator -> {
        			JPanel container = new JPanel();
        			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        			JLabel resultLabel = new JLabel(verificator.getTimingMessage());
        			container.add(resultLabel);
        			for(RefOntoUML.Element elem: verificator.getMap().keySet()){
        				JPanel box = new JPanel();
        				box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        				String elementName = elem.toString().replaceAll("«.*»", "").trim();
        				if(elementName.isEmpty()) {
        					JLabel label = new JLabel(elem.toString());
        					box.add(label);
        				}else {
        					JButton button = new JButton(elem.toString());
	        				button.addActionListener(
	    						event -> System.out.println("-----------------Fui clicado-----------------")
							);
	        				box.add(button);
        				}
        				
						for(String message: verificator.getMap().get(elem)){		
							JLabel label = new JLabel(message);
							box.add(label);
						}
						box.setBorder(BorderFactory.createEmptyBorder(8,4,8,4));
						box.doLayout();
						container.add(box);
    				}
        			container.doLayout();
        			JScrollPane containerFather = new JScrollPane(container);
        			viewManager.showMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID, "Error log", containerFather);
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
