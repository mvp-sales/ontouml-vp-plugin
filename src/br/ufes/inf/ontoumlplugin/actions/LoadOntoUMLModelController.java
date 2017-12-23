package br.ufes.inf.ontoumlplugin.actions;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;


import br.ufes.inf.ontoumlplugin.OntoUMLPlugin;
import br.ufes.inf.ontoumlplugin.model.OntoUml2VpConverter;
import com.vp.plugin.model.*;
import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import org.eclipse.emf.ecore.resource.Resource;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import RefOntoUML.util.RefOntoUMLResourceUtil;

public class LoadOntoUMLModelController implements VPActionController {

	private final IProject project = ApplicationManager.instance().getProjectManager().getProject();
	private final ViewManager viewManager = ApplicationManager.instance().getViewManager();

	@Override
	public void performAction(VPAction arg0) {
		viewManager.clearMessages(OntoUMLPlugin.PLUGIN_ID);
		viewManager.removeMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID);  
		
		JFileChooser fileChooser = ApplicationManager.instance().getViewManager().createJFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Reference OntoUML (*.refontouml)", "refontouml");
		fileChooser.setFileFilter(filter);
		fileChooser.setDialogTitle("Select the RefOntoUML file");
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		int returnValue = fileChooser.showOpenDialog(null);
		
		if(returnValue == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			try {
				viewManager.showMessage("Loading, please wait...", OntoUMLPlugin.PLUGIN_ID);
				Resource model = RefOntoUMLResourceUtil.loadModel(file.getAbsolutePath());
				RefOntoUML.Package ontoUmlPackage = (RefOntoUML.Package) model.getContents().get(0);

				buildClassDiagram(ontoUmlPackage);
			} catch (Exception e) {
				viewManager.showMessage(e.getMessage(), OntoUMLPlugin.PLUGIN_ID);
			}
		}

	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub

	}

	private void buildClassDiagram(RefOntoUML.Package ontoUmlPackage) {
		OntoUml2VpConverter ontoUml2VpConverter = new OntoUml2VpConverter(project);
		Completable.fromCallable(
			() -> {
				ontoUml2VpConverter.transform(ontoUmlPackage);
				return Completable.complete();
			}
		)
		.subscribeOn(Schedulers.computation())
		.observeOn(Schedulers.trampoline())
		.subscribe(
			() -> viewManager.showMessage("Model loaded successfully", OntoUMLPlugin.PLUGIN_ID),
			err -> {
				viewManager.showMessage(err.getMessage(), OntoUMLPlugin.PLUGIN_ID);
				err.printStackTrace();
			}
		);
	}
}
