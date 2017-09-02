package br.ufes.inf.ontoumlplugin.actions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.emf.ecore.resource.Resource;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramTypeConstants;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.diagram.shape.IPackageUIModel;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

import RefOntoUML.Classifier;
import RefOntoUML.Element;
import RefOntoUML.NamedElement;
import RefOntoUML.PackageableElement;
import RefOntoUML.parser.OntoUMLParser;
import RefOntoUML.util.RefOntoUMLResourceUtil;

public class LoadOntoUMLModelController implements VPActionController {
	
	private Map<RefOntoUML.Classifier, IModelElement> ontoUml2VpClasses;
	
	public LoadOntoUMLModelController(){
		this.ontoUml2VpClasses = new HashMap<>();
	}

	@Override
	public void performAction(VPAction arg0) {
		// TODO Auto-generated method stub
		
		IProject project = ApplicationManager
				.instance()
				.getProjectManager()
				.getProject();


		DiagramManager diagramManager = ApplicationManager
				                        .instance()
				                        .getDiagramManager();
		
		JFileChooser fileChooser = ApplicationManager.instance().getViewManager().createJFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Reference OntoUML (*.refontouml)", "refontouml");
		fileChooser.setFileFilter(filter);
		fileChooser.setDialogTitle("Selecione o arquivo RefOntoUML");
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		int returnValue = fileChooser.showOpenDialog(null);
		
		if(returnValue == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			try {
				Resource model = RefOntoUMLResourceUtil.loadModel(file.getAbsolutePath());
				IClassDiagramUIModel diagram = 
						(IClassDiagramUIModel) diagramManager.createDiagram(IDiagramTypeConstants.DIAGRAM_TYPE_CLASS_DIAGRAM);
				
				RefOntoUML.Package ontoUmlPackage = (RefOntoUML.Package) model.getContents().get(0);
				OntoUMLParser parser = new OntoUMLParser(ontoUmlPackage);
				
				for(Classifier c : parser.getRigidClasses()){
					IClass vpClass = IModelElementFactory.instance().createClass();
					vpClass.setName(c.getName());
					this.ontoUml2VpClasses.put(c, vpClass);
					
					// create superclass shape
					IClassUIModel vpClassUi = (IClassUIModel) diagramManager.createDiagramElement(diagram, vpClass);
					vpClassUi.setRequestResetCaption(true);
				}
				
				for(Classifier c : parser.getAntiRigidClasses()){
					IClass vpClass = IModelElementFactory.instance().createClass();
					vpClass.setName(c.getName());
					this.ontoUml2VpClasses.put(c, vpClass);
					
					// create superclass shape
					IClassUIModel vpClassUi = (IClassUIModel) diagramManager.createDiagramElement(diagram, vpClass);
					vpClassUi.setRequestResetCaption(true);
				}
				
				for(Classifier c : parser.getAssociations()){
					RefOntoUML.Association a = (RefOntoUML.Association) c;
				}
				
				diagramManager.layout(diagram, DiagramManager.LAYOUT_AUTO);
				diagramManager.openDiagram(diagram);
				
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
