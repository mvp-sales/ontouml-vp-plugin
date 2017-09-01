package br.ufes.inf.ontoumlplugin.actions;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.emf.ecore.resource.Resource;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IProject;

import RefOntoUML.Classifier;
import RefOntoUML.Element;
import RefOntoUML.NamedElement;
import RefOntoUML.PackageableElement;
import RefOntoUML.parser.OntoUMLParser;
import RefOntoUML.util.RefOntoUMLResourceUtil;

public class LoadOntoUMLModelController implements VPActionController {

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
		
		JFileChooser fileChooser = ApplicationManager.instance().getViewManager().createJFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Reference OntoUML (*.refontouml)", "refontouml");
		fileChooser.setFileFilter(filter);
		fileChooser.setDialogTitle("Selecione o arquivo refontouml");
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		int returnValue = fileChooser.showOpenDialog(null);
		
		if(returnValue == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			try {
				Resource model = RefOntoUMLResourceUtil.loadModel(file.getAbsolutePath());
				RefOntoUML.Package ontoUmlPackage = (RefOntoUML.Package) model.getContents().get(0);
				System.out.println(ontoUmlPackage.toString());
				OntoUMLParser parser = new OntoUMLParser(ontoUmlPackage);
				for(Classifier c : parser.getRigidClasses()){
					System.out.println(c.getName());
				}
				//RefOntoUMLResourceUtil.saveModel(new File("/home/mvp-sales/Documentos/pqp.refontouml").getAbsolutePath(), ontoUmlPackage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub

	}

}
