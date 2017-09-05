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
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramTypeConstants;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.diagram.connector.IGeneralizationUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.diagram.shape.IGeneralizationSetUIModel;
import com.vp.plugin.diagram.shape.IPackageUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IGeneralizationSet;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IMultiplicity;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.factory.IModelElementFactory;

import RefOntoUML.Classifier;
import RefOntoUML.Element;
import RefOntoUML.Generalization;
import RefOntoUML.GeneralizationSet;
import RefOntoUML.NamedElement;
import RefOntoUML.PackageableElement;
import RefOntoUML.parser.OntoUMLParser;
import RefOntoUML.util.RefOntoUMLResourceUtil;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class LoadOntoUMLModelController implements VPActionController {
	
	private Map<RefOntoUML.Classifier, IModelElement> ontoUml2VpClasses;
	private Map<RefOntoUML.Classifier, IDiagramElement> ontoUml2VpShapes;
	private final IProject project = ApplicationManager.instance().getProjectManager().getProject();
	
	public LoadOntoUMLModelController(){
		this.ontoUml2VpClasses = new HashMap<>();
		this.ontoUml2VpShapes = new HashMap<>();
	}

	@Override
	public void performAction(VPAction arg0) {
		// TODO Auto-generated method stub
		
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

				RefOntoUML.Package ontoUmlPackage = (RefOntoUML.Package) model.getContents().get(0);

				buildClassDiagram(ontoUmlPackage);

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

	private void buildClassDiagram(RefOntoUML.Package ontoUmlPackage){
		DiagramManager diagramManager = ApplicationManager
										.instance()
										.getDiagramManager();

		IClassDiagramUIModel diagram = 
						(IClassDiagramUIModel) diagramManager.createDiagram(IDiagramTypeConstants.DIAGRAM_TYPE_CLASS_DIAGRAM);
				
		OntoUMLParser parser = new OntoUMLParser(ontoUmlPackage);
		
		for(Classifier c : parser.getRigidClasses()){
			createClass(diagramManager, diagram, c);
		}
		
		for(Classifier c : parser.getAntiRigidClasses()){
			createClass(diagramManager, diagram, c);
		}
		
		for(Classifier c : parser.getAssociations()){
			RefOntoUML.Association association = (RefOntoUML.Association) c;
			createAssociation(diagramManager, diagram, association);
		}

		for(RefOntoUML.GeneralizationSet genSet : parser.getAllInstances(RefOntoUML.GeneralizationSet.class)){
			createGeneralizationSet(diagramManager, diagram, genSet);
		}

		for(Generalization gen : parser.getAllInstances(Generalization.class)){
			if(!isGeneralizationInsideGenSet(parser, gen)){
				createGeneralization(diagramManager, diagram, gen);
			}
		}
		diagramManager.layout(diagram, DiagramManager.LAYOUT_AUTO);
		diagramManager.openDiagram(diagram);
	}

	private void createClass(DiagramManager diagramManager, IClassDiagramUIModel diagram, Classifier c){
		IClass vpClass = IModelElementFactory.instance().createClass();
		vpClass.setName(c.getName());
		this.ontoUml2VpClasses.put(c, vpClass);

		if(c instanceof RefOntoUML.Kind){
			IModelElement[] stereotypes = project.toModelElementArray(IModelElementFactory.MODEL_TYPE_STEREOTYPE);
			for(IModelElement e : stereotypes){
				IStereotype s = (IStereotype) e;
				if(s.getName().equals("Kind")){
					vpClass.addStereotype(s);
					break;
				}
			}
		}
		
		// create superclass shape
		IClassUIModel vpClassUi = (IClassUIModel) diagramManager.createDiagramElement(diagram, vpClass);
		vpClassUi.setRequestResetCaption(true);
		
		this.ontoUml2VpShapes.put(c, vpClassUi);
	}

	private void createAssociation(DiagramManager diagramManager, IClassDiagramUIModel diagram, RefOntoUML.Association association){
		RefOntoUML.Property p1 = association.getOwnedEnd().get(0);
		RefOntoUML.Property p2 = association.getOwnedEnd().get(1);
		
		RefOntoUML.Classifier c1 = (RefOntoUML.Classifier) p1.getType();
		RefOntoUML.Classifier c2 = (RefOntoUML.Classifier) p2.getType();
		int lowerC1 = p1.getLower(), upperC1 = p1.getUpper();
		int lowerC2 = p2.getLower(), upperC2 = p2.getUpper();
		
		IModelElement from1 = this.ontoUml2VpClasses.get(c1),
						to1 = this.ontoUml2VpClasses.get(c2);
		
		// create normal association between subclass to "ClassWithAssociation"
		IAssociation associationModel = IModelElementFactory.instance().createAssociation();
		associationModel.setFrom(from1);
		associationModel.setTo(to1);
		// specify multiplicity for from & to end
		IAssociationEnd associationFromEnd = (IAssociationEnd) associationModel.getFromEnd();
		associationFromEnd.setMultiplicity(getMultiplicityFromValues(lowerC1, upperC1));
		IAssociationEnd associationToEnd = (IAssociationEnd) associationModel.getToEnd();
		associationToEnd.setMultiplicity(getMultiplicityFromValues(lowerC2, upperC2));
		// create association connector on diagram
		IDiagramElement from = this.ontoUml2VpShapes.get(c1),
						to = this.ontoUml2VpShapes.get(c2);
		IAssociationUIModel associationConnector = (IAssociationUIModel) diagramManager.createConnector(diagram, associationModel, from, to, null);
		// set to automatic calculate the initial caption position
		associationConnector.setRequestResetCaption(true);
	}

	private String getMultiplicityFromValues(int lower, int upper){
		String result = (lower == -1 ? "*" : lower) + ".." + (upper == -1 ? "*" : upper);
		if(lower == 0){
			if(upper == 1){
				result = IAssociationEnd.MULTIPLICITY_ZERO_TO_ONE;
			}else if(upper == -1){
				result = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
			}
		}else if(lower == 1){
			if(upper == 1){
				result = IAssociationEnd.MULTIPLICITY_ONE;
			}else if(upper == -1){
				result = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
			}
		}else if(lower == upper){
			result = lower == -1 ? "*" : Integer.toString(lower);
		}
		return result;
	}

	private void createGeneralizationSet(DiagramManager diagramManager, IClassDiagramUIModel diagram, RefOntoUML.GeneralizationSet genSet){
		IGeneralizationSet vpGenSet = IModelElementFactory.instance().createGeneralizationSet();
		vpGenSet.setDisjoint(genSet.isIsDisjoint()); vpGenSet.setCovering(genSet.isIsCovering());

		for(Generalization gen : genSet.getGeneralization()){
			vpGenSet.addGeneralization(createGeneralization(diagramManager, diagram, gen));
		}

		IGeneralizationSetUIModel vpGenSetUi = 
			(IGeneralizationSetUIModel) diagramManager.createDiagramElement(diagram, vpGenSet);

		vpGenSetUi.setRequestResetCaption(true);
	}

	private IGeneralization createGeneralization(DiagramManager diagramManager, IClassDiagramUIModel diagram, Generalization gen){
		// create generalization relationship from superclass to subclass
		IGeneralization generalizationModel = IModelElementFactory.instance().createGeneralization();
		IModelElement specific = this.ontoUml2VpClasses.get(gen.getSpecific()),
						general = this.ontoUml2VpClasses.get(gen.getGeneral());
		IDiagramElement specificShape = this.ontoUml2VpShapes.get(gen.getSpecific()),
						generalShape = this.ontoUml2VpShapes.get(gen.getGeneral());
		generalizationModel.setFrom(general);
		generalizationModel.setTo(specific);
		// create generalization connector on diagram
		IGeneralizationUIModel generalizationConnector = 
			(IGeneralizationUIModel) diagramManager.createConnector
										(diagram, generalizationModel, generalShape, specificShape, null);

		return generalizationModel;
	}

	private boolean isGeneralizationInsideGenSet(OntoUMLParser parser, Generalization gen){
		for(GeneralizationSet genSet : parser.getAllInstances(GeneralizationSet.class)){
			if(genSet.getGeneralization().contains(gen))
				return true;
		}
		return false;
	}

}
