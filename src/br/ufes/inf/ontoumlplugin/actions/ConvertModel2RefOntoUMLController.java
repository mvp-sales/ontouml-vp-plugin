package br.ufes.inf.ontoumlplugin.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import RefOntoUML.Element;
import RefOntoUML.Package;
import RefOntoUML.parser.SyntacticVerificator;
import br.ufes.inf.ontoumlplugin.OntoUMLPlugin;
import br.ufes.inf.ontoumlplugin.model.Vp2OntoUmlConverter;
import br.ufes.inf.ontoumlplugin.utils.CommonUtils;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import RefOntoUML.util.RefOntoUMLResourceUtil;
import com.vp.plugin.model.IProject;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ConvertModel2RefOntoUMLController implements VPActionController {

	private Package refontoumlPackage;

	@Override
	public void performAction(VPAction arg0) {

		IProject project = ApplicationManager.instance().getProjectManager().getProject();

		ViewManager viewManager = ApplicationManager.instance().getViewManager();
		viewManager.clearMessages(OntoUMLPlugin.PLUGIN_ID);
		viewManager.removeMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID);

		Vp2OntoUmlConverter vp2OntoUmlConverter = new Vp2OntoUmlConverter(project);

		viewManager.showMessage("Loading, please wait...", OntoUMLPlugin.PLUGIN_ID);

		Observable.fromCallable(vp2OntoUmlConverter::transform)
			.subscribeOn(Schedulers.computation())
			.map(
				ontoUmlPackage -> {
					this.refontoumlPackage = ontoUmlPackage;
					SyntacticVerificator verificator = new SyntacticVerificator();
					verificator.run(ontoUmlPackage);
					return verificator;
				}
		)
		.observeOn(Schedulers.trampoline())
		.subscribe(
			syntacticVerificator -> {
				if (!syntacticVerificator.getMap().isEmpty()){
					CommonUtils.showModelErrors(
						syntacticVerificator.getTimingMessage(),
						syntacticVerificator.getMap(), viewManager
					);
				}else {
					showSaveDialog(viewManager);
				}
			},
			err -> viewManager.showMessage(err.getMessage(), OntoUMLPlugin.PLUGIN_ID)
		);
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub
		
	}

	private void showSaveDialog(ViewManager viewManager) {
		JFileChooser fileChooser = ApplicationManager.instance().getViewManager().createJFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Reference OntoUML (*.refontouml)", "refontouml");
		fileChooser.setFileFilter(filter);
		fileChooser.setDialogTitle("Selecione o diretório de destino");
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		int returnValue = fileChooser.showSaveDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File outputFolder = fileChooser.getCurrentDirectory();
			String fileName = fileChooser.getSelectedFile().getName();
			if (!fileName.contains(".refontouml")) {
				fileName += ".refontouml";
			}
			File file = new File(outputFolder.getAbsolutePath() + File.separator + fileName);
			if (file.exists()) {
				int result = JOptionPane.showConfirmDialog(null, "The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
				switch (result) {
					case JOptionPane.YES_OPTION:
						RefOntoUMLResourceUtil.saveModel(file.getAbsolutePath(), refontoumlPackage);
						return;
					case JOptionPane.NO_OPTION:
					case JOptionPane.CLOSED_OPTION:
					case JOptionPane.CANCEL_OPTION:
						return;
				}
			} else {
				RefOntoUMLResourceUtil.saveModel(file.getAbsolutePath(), refontoumlPackage);
			}
			viewManager.showMessage("Model saved at " + file.getAbsolutePath(), OntoUMLPlugin.PLUGIN_ID);
		}
	}
}
