package br.ufes.inf.ontoumlplugin.actions;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;

import RefOntoUML.util.RefOntoUMLResourceUtil;
import br.ufes.inf.ontoumlplugin.model.RefOntoUMLWrapper;
import io.reactivex.schedulers.Schedulers;

public class ConvertModel2RefOntoUMLController implements VPActionController {

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
		
		RefOntoUMLWrapper
		.createObservableWrapper(diagram)
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
				}
			}
		);
	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub
		
	}
	
	

}
