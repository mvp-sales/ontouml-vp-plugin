package br.ufes.inf.ontoumlplugin.actions;


import RefOntoUML.Element;
import RefOntoUML.Package;
import RefOntoUML.parser.OntoUMLParser;
import RefOntoUML.parser.SyntacticVerificator;
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

import br.ufes.inf.ontoumlplugin.model.Vp2OntoUmlConverter;
import br.ufes.inf.ontoumlplugin.utils.CommonUtils;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;

import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;
import com.vp.plugin.view.IDialogHandler;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import org.apache.batik.apps.svgbrowser.Application;

public class CheckOntoUMLModelController implements VPActionController, VPContextActionController {
	
	public static void validateModel() {
		IProject project = ApplicationManager.instance().getProjectManager().getProject();
        ViewManager viewManager = ApplicationManager.instance().getViewManager();
        viewManager.clearMessages(OntoUMLPlugin.PLUGIN_ID);
        viewManager.removeMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID);

		Vp2OntoUmlConverter vp2OntoUmlConverter = new Vp2OntoUmlConverter(project);

		Observable.fromCallable(vp2OntoUmlConverter::transform)
            .subscribeOn(Schedulers.computation())
            .map(
                ontoUmlPackage -> {
                    SyntacticVerificator verificator = new SyntacticVerificator();
                    verificator.run(ontoUmlPackage);
                    return verificator;
                }
            )
            .observeOn(Schedulers.trampoline())
            .subscribe(
                syntacticVerificator -> CommonUtils.showModelErrors(syntacticVerificator.getTimingMessage(), syntacticVerificator.getMap(), viewManager),
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
