package br.ufes.inf.ontoumlplugin.actions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.emf.common.util.EList;
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
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import com.vp.plugin.model.factory.IModelElementFactory;

import RefOntoUML.Classifier;
import RefOntoUML.Element;
import RefOntoUML.FormalAssociation;
import RefOntoUML.Generalization;
import RefOntoUML.GeneralizationSet;
import RefOntoUML.NamedElement;
import RefOntoUML.PackageableElement;
import RefOntoUML.PerceivableQuality;
import RefOntoUML.SubKind;
import RefOntoUML.subQuantityOf;
import RefOntoUML.parser.OntoUMLParser;
import RefOntoUML.util.RefOntoUMLResourceUtil;
import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;
import br.ufes.inf.ontoumlplugin.model.OntoUMLRelationshipType;
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
			if(association instanceof RefOntoUML.Meronymic){
				RefOntoUML.Meronymic meronymicAssociation = (RefOntoUML.Meronymic) association;
				createMeronymicAssociation(diagramManager, diagram, meronymicAssociation);
			}else{
				createAssociation(diagramManager, diagram, association);
			}
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

		vpClass = setClassStereotype(vpClass, c);
		
		// create superclass shape
		IClassUIModel vpClassUi = (IClassUIModel) diagramManager.createDiagramElement(diagram, vpClass);
		vpClassUi.setRequestResetCaption(true);
		
		this.ontoUml2VpShapes.put(c, vpClassUi);
	}

	private IClass setClassStereotype(IClass vpClass, Classifier ontoUmlElement){
		if(ontoUmlElement instanceof RefOntoUML.Kind){
			addStereotypeClass(vpClass, "Kind");
		}else if(ontoUmlElement instanceof RefOntoUML.SubKind){
			addStereotypeClass(vpClass, "SubKind");
		}else if(ontoUmlElement instanceof RefOntoUML.Role){
			addStereotypeClass(vpClass, "Role");
		}else if(ontoUmlElement instanceof RefOntoUML.Phase){
			addStereotypeClass(vpClass, "Phase");
		}else if(ontoUmlElement instanceof RefOntoUML.Relator){
			addStereotypeClass(vpClass, "Relator");
		}else if(ontoUmlElement instanceof RefOntoUML.RoleMixin){
			addStereotypeClass(vpClass, "RoleMixin");
		}else if(ontoUmlElement instanceof RefOntoUML.Category){
			addStereotypeClass(vpClass, "Category");
		}else if(ontoUmlElement instanceof RefOntoUML.Quantity){
			addStereotypeClass(vpClass, "Quantity");
		}else if(ontoUmlElement instanceof RefOntoUML.Collective){
			addStereotypeClass(vpClass, "Collective");
		}else if(ontoUmlElement instanceof RefOntoUML.Mixin){
			addStereotypeClass(vpClass, "Mixin");
		}else if(ontoUmlElement instanceof RefOntoUML.Mode){
			addStereotypeClass(vpClass, "Mode");
		}else if(ontoUmlElement instanceof RefOntoUML.Quality){
			addStereotypeClass(vpClass, "Quality");
		}else if(ontoUmlElement instanceof RefOntoUML.DataType){
			addStereotypeClass(vpClass, "DataType");
		}else if(ontoUmlElement instanceof RefOntoUML.PrimitiveType){
			addStereotypeClass(vpClass, "PrimitiveType");
		}else if(ontoUmlElement instanceof RefOntoUML.PerceivableQuality){
			addStereotypeClass(vpClass, "PerceivableQuality");
		}else if(ontoUmlElement instanceof RefOntoUML.NonPerceivableQuality){
			addStereotypeClass(vpClass, "NonPerceivableQuality");
		}else if(ontoUmlElement instanceof RefOntoUML.NominalQuality){
			addStereotypeClass(vpClass, "NominalQuality");
		}else if(ontoUmlElement instanceof RefOntoUML.Enumeration){
			addStereotypeClass(vpClass, "Enumeration");
		}else if(ontoUmlElement instanceof RefOntoUML.MeasurementDomain){
			addStereotypeClass(vpClass, "MeasurementDomain");
		}

		return vpClass;
	}

	private void addStereotypeClass(IClass vpClass, String stereotypeStr){
		IStereotype stereotype = OntoUMLClassType.getStereotypeFromString(project, stereotypeStr);
		if(stereotype != null){
			vpClass.addStereotype(stereotype);
		}
	}

	private void createMeronymicAssociation(DiagramManager diagramManager, IClassDiagramUIModel diagram, RefOntoUML.Meronymic association){
		RefOntoUML.Property wholeEnd = association.wholeEnd();
		RefOntoUML.Property partEnd = association.partEnd();
		
		RefOntoUML.Classifier whole = association.whole();
		RefOntoUML.Classifier part = association.part();
		int lowerPart = partEnd.getLower(), upperC1 = partEnd.getUpper();
		int lowerWhole = wholeEnd.getLower(), upperC2 = wholeEnd.getUpper();
		
		IModelElement wholeVp = this.ontoUml2VpClasses.get(whole),
						partVp = this.ontoUml2VpClasses.get(part);
		
		// create normal association between subclass to "ClassWithAssociation"
		IAssociation associationModel = IModelElementFactory.instance().createAssociation();
		associationModel.setFrom(partVp);
		associationModel.setTo(wholeVp);
		// specify multiplicity for from & to end
		IAssociationEnd associationFromEnd = (IAssociationEnd) associationModel.getFromEnd();
		associationFromEnd.setMultiplicity(getMultiplicityFromValues(lowerPart, upperC1));
		IAssociationEnd associationToEnd = (IAssociationEnd) associationModel.getToEnd();
		associationToEnd.setMultiplicity(getMultiplicityFromValues(lowerWhole, upperC2));
		associationToEnd.setAggregationKind(association.isIsShareable() ? IAssociationEnd.AGGREGATION_KIND_AGGREGATION :
																			IAssociationEnd.AGGREGATION_KIND_COMPOSITED);
		
		associationModel = setMeronymicAssociation(associationModel, association);

		// create association connector on diagram
		IDiagramElement from = this.ontoUml2VpShapes.get(part),
						to = this.ontoUml2VpShapes.get(whole);
		IAssociationUIModel associationConnector = (IAssociationUIModel) diagramManager.createConnector(diagram, associationModel, from, to, null);
		// set to automatic calculate the initial caption position
		associationConnector.setRequestResetCaption(true);
	}

	private IAssociation setMeronymicAssociation(IAssociation vpAssociation, RefOntoUML.Meronymic ontoUmlAssociation){

		IStereotype stereotype;
		
		if(ontoUmlAssociation instanceof RefOntoUML.memberOf){
			stereotype = addStereotypeAssociation(vpAssociation, "MemberOf");
		}else if(ontoUmlAssociation instanceof RefOntoUML.componentOf){
			stereotype = addStereotypeAssociation(vpAssociation, "ComponentOf");
		}else if(ontoUmlAssociation instanceof RefOntoUML.subQuantityOf){
			stereotype = addStereotypeAssociation(vpAssociation, "subQuantityOf");
		}else{
			stereotype = addStereotypeAssociation(vpAssociation, "subCollectionOf");
		}

		if(stereotype != null){
			ITaggedValueContainer container = vpAssociation.getTaggedValues();
			ITaggedValue inseparable = container.getTaggedValueByName("inseparable");
			ITaggedValue immutableWhole = container.getTaggedValueByName("immutableWhole");
			ITaggedValue immutablePart = container.getTaggedValueByName("immutablePart");
			ITaggedValue essential = container.getTaggedValueByName("essential");
			

			inseparable.setValue(ontoUmlAssociation.isIsInseparable() ? "True" : "False");
			immutableWhole.setValue(ontoUmlAssociation.isIsImmutableWhole() ? "True" : "False");
			immutablePart.setValue(ontoUmlAssociation.isIsImmutablePart() ? "True" : "False");

			if(essential != null){
				essential.setValue(ontoUmlAssociation.isIsEssential() ? "True" : "False");
			}
		}

		return vpAssociation;
	}

	private IStereotype addStereotypeAssociation(IAssociation vpAssociation, String stereotypeStr){
		IStereotype stereotype = OntoUMLRelationshipType.getStereotypeFromString(project, stereotypeStr);
		if(stereotype != null){
			vpAssociation.addStereotype(stereotype);
		}

		return stereotype;
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
		
		associationModel = setAssociationStereotype(associationModel, association);
		
		// create association connector on diagram
		IDiagramElement from = this.ontoUml2VpShapes.get(c1),
						to = this.ontoUml2VpShapes.get(c2);
		IAssociationUIModel associationConnector = (IAssociationUIModel) diagramManager.createConnector(diagram, associationModel, from, to, null);
		// set to automatic calculate the initial caption position
		associationConnector.setRequestResetCaption(true);
	}

	private IAssociation setAssociationStereotype(IAssociation vpAssociation, RefOntoUML.Association ontoUmlAssociation){

		if(ontoUmlAssociation instanceof RefOntoUML.FormalAssociation){
			addStereotypeAssociation(vpAssociation, "FormalAssociation");
		}else if(ontoUmlAssociation instanceof RefOntoUML.Mediation){
			addStereotypeAssociation(vpAssociation, "Mediation");
		}else if(ontoUmlAssociation instanceof RefOntoUML.Characterization){
			addStereotypeAssociation(vpAssociation, "Characterization");
		}else if(ontoUmlAssociation instanceof RefOntoUML.Derivation){
			addStereotypeAssociation(vpAssociation, "Derivation");
		}else if(ontoUmlAssociation instanceof RefOntoUML.Structuration){
			addStereotypeAssociation(vpAssociation, "Structuration");
		}

		return vpAssociation;
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
