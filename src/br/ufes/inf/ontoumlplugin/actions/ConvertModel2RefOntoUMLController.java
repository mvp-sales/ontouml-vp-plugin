package br.ufes.inf.ontoumlplugin.actions;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import br.ufes.inf.ontoumlplugin.OntoUMLPlugin;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import RefOntoUML.util.RefOntoUMLResourceUtil;
import br.ufes.inf.ontoumlplugin.model.RefOntoUMLWrapper;
import com.vp.plugin.model.IProject;
import io.reactivex.schedulers.Schedulers;

public class ConvertModel2RefOntoUMLController implements VPActionController {

	@Override
	public void performAction(VPAction arg0) {

		IProject project = ApplicationManager.instance().getProjectManager().getProject();

		ViewManager viewManager = ApplicationManager.instance().getViewManager();
		viewManager.clearMessages(OntoUMLPlugin.PLUGIN_ID);
		viewManager.removeMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID);  
		
		RefOntoUMLWrapper
		.createObservableWrapper(project)
		.subscribeOn(Schedulers.computation())
		.observeOn(Schedulers.trampoline())
		.subscribe(
			wrapper -> {
				JFileChooser fileChooser = ApplicationManager.instance().getViewManager().createJFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Reference OntoUML (*.refontouml)", "refontouml");
				fileChooser.setFileFilter(filter);
				fileChooser.setDialogTitle("Selecione o diretório de destino");
				fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
				int returnValue = fileChooser.showSaveDialog(null);
				
				if(returnValue == JFileChooser.APPROVE_OPTION){
					File outputFolder = fileChooser.getCurrentDirectory();
					String fileName = fileChooser.getSelectedFile().getName();
					if(!fileName.contains(".refontouml")){
						fileName += ".refontouml";
					}
					File file = new File(outputFolder.getAbsolutePath() + File.separator + fileName);
					if(file.exists()){
						int result = JOptionPane.showConfirmDialog(null,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
						switch(result){
							case JOptionPane.YES_OPTION:
								RefOntoUMLResourceUtil.saveModel(file.getAbsolutePath(), wrapper.ontoUmlPackage);
								return;
							case JOptionPane.NO_OPTION:
							case JOptionPane.CLOSED_OPTION:
							case JOptionPane.CANCEL_OPTION:
								return;
						}
					}else{
						RefOntoUMLResourceUtil.saveModel(file.getAbsolutePath(), wrapper.ontoUmlPackage);
					}
					viewManager.showMessage("Model saved at " + file.getAbsolutePath(), OntoUMLPlugin.PLUGIN_ID);
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
