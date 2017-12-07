package br.ufes.inf.ontoumlplugin.actions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import RefOntoUML.*;
import RefOntoUML.Package;
import br.ufes.inf.ontoumlplugin.OntoUMLPlugin;
import br.ufes.inf.ontoumlplugin.model.OntoUMLRelationshipType;
import com.vp.plugin.model.*;
import org.eclipse.emf.ecore.resource.Resource;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.factory.IModelElementFactory;

import RefOntoUML.parser.OntoUMLParser;
import RefOntoUML.util.RefOntoUMLResourceUtil;
import br.ufes.inf.ontoumlplugin.model.VPModelFactory;

public class LoadOntoUMLModelController implements VPActionController {

	private Map<RefOntoUML.Package, IPackage> ontoUml2VpPackage;
	private Map<RefOntoUML.Classifier, IModelElement> ontoUml2VpClasses;
	private final IProject project = ApplicationManager.instance().getProjectManager().getProject();
	
	public LoadOntoUMLModelController(){
		this.ontoUml2VpPackage = new HashMap<>();
		this.ontoUml2VpClasses = new HashMap<>();
	}

	@Override
	public void performAction(VPAction arg0) {
		ViewManager viewManager = ApplicationManager.instance().getViewManager();
		viewManager.clearMessages(OntoUMLPlugin.PLUGIN_ID);
		viewManager.removeMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID);  
		
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
				viewManager.showMessage("Model loaded successfully", OntoUMLPlugin.PLUGIN_ID);
			} catch (Exception e) {
				viewManager.showMessage(e.getMessage(), OntoUMLPlugin.PLUGIN_ID);
			}
		}

	}

	@Override
	public void update(VPAction arg0) {
		// TODO Auto-generated method stub

	}

	private void buildClassDiagram(RefOntoUML.Package ontoUmlPackage){
				
		OntoUMLParser parser = new OntoUMLParser(ontoUmlPackage);

		for (Package modelPackage : parser.getAllInstances(Package.class)) {
		    IPackage modelVpPackage = getPackage(modelPackage);

			if (modelPackage.eContainer() != null) {
				Package container = (Package) modelPackage.eContainer();
				IPackage containerPackage = getPackage(container);
				containerPackage.addChild(modelVpPackage);
			}
		}

		for (RefOntoUML.Class ontoUmlClass : parser.getAllInstances(RefOntoUML.Class.class)) {
		    IPackage classPackage = this.ontoUml2VpPackage.get((Package) ontoUmlClass.eContainer());
			createClass(ontoUmlClass, classPackage);
		}
		
		for(DataType dataType : parser.getAllInstances(DataType.class)) {
            IPackage dataTypePackage = this.ontoUml2VpPackage.get((Package) dataType.eContainer());
			createClass(dataType, dataTypePackage);
		}
		
		for(Classifier c : parser.getAssociations()){
            IPackage associationPackage = this.ontoUml2VpPackage.get((Package) c.eContainer());
			RefOntoUML.Association association = (RefOntoUML.Association) c;
			if(association instanceof RefOntoUML.Meronymic){
				RefOntoUML.Meronymic meronymicAssociation = (RefOntoUML.Meronymic) association;
				createMeronymicAssociation(meronymicAssociation, associationPackage);
			}else if(!(association instanceof RefOntoUML.Derivation) ){
				createAssociation(association, associationPackage);
			}
		}

		for (Derivation d : parser.getAllInstances(Derivation.class)) {
            IPackage associationPackage = this.ontoUml2VpPackage.get((Package) d.eContainer());
            createDerivation(d, associationPackage);
        }

		for(RefOntoUML.GeneralizationSet genSet : parser.getAllInstances(RefOntoUML.GeneralizationSet.class)){
            IPackage genSetPackage = this.ontoUml2VpPackage.get((Package) genSet.eContainer());
			createGeneralizationSet(genSet, genSetPackage);
		}

		for(Generalization gen : parser.getAllInstances(Generalization.class)){
			if(gen.getGeneralizationSet().isEmpty()){
				createGeneralization(gen);
			}
		}

		/*for(IPackage pg : this.ontoUml2VpPackage.values()) {
		    for (IModelElement child : pg.toChildArray()) {
                System.out.println(child.getModelType() + " - " + child.getName());
            }
        }*/
	}

	private IPackage getPackage(Package ontoUmlPackage) {
	    IPackage vpPackage;
        if (!this.ontoUml2VpPackage.containsKey(ontoUmlPackage)){
            vpPackage = IModelElementFactory.instance().createPackage();
            vpPackage.setName(ontoUmlPackage.getName());
            this.ontoUml2VpPackage.put(ontoUmlPackage, vpPackage);
        }else{
            vpPackage = this.ontoUml2VpPackage.get(ontoUmlPackage);
        }
        return vpPackage;
    }

	private void createClass(Classifier c, IPackage vpPackage){
		IClass vpClass = IModelElementFactory.instance().createClass();
		vpClass.setName(c.getName());
		vpClass = VPModelFactory.setClassStereotype(vpClass, c, this.project);
        this.ontoUml2VpClasses.put(c, vpClass);
		vpPackage.addChild(vpClass);
	}


	private void createMeronymicAssociation(Meronymic association, IPackage vpPackage){
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
		associationModel.setName(association.getName());
		associationModel.setFrom(partVp);
		associationModel.setTo(wholeVp);
		// specify multiplicity for from & to end
		IAssociationEnd associationFromEnd = (IAssociationEnd) associationModel.getFromEnd();
		associationFromEnd.setMultiplicity(getMultiplicityFromValues(lowerPart, upperC1));
		IAssociationEnd associationToEnd = (IAssociationEnd) associationModel.getToEnd();
		associationToEnd.setMultiplicity(getMultiplicityFromValues(lowerWhole, upperC2));
		associationToEnd.setAggregationKind(
				association.isIsShareable() ?
				IAssociationEnd.AGGREGATION_KIND_AGGREGATION : IAssociationEnd.AGGREGATION_KIND_COMPOSITED
		);
		
		associationModel = VPModelFactory.setMeronymicAssociation(associationModel, association, this.project);
        this.ontoUml2VpClasses.put(association, associationModel);
	    vpPackage.addChild(associationModel);
	}

	private void createAssociation(Association association, IPackage vpPackage){
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
		associationModel.setName(association.getName());
		associationModel.setFrom(from1);
		associationModel.setTo(to1);
		// specify multiplicity for from & to end
		IAssociationEnd associationFromEnd = (IAssociationEnd) associationModel.getFromEnd();
		associationFromEnd.setMultiplicity(getMultiplicityFromValues(lowerC1, upperC1));
		IAssociationEnd associationToEnd = (IAssociationEnd) associationModel.getToEnd();
		associationToEnd.setMultiplicity(getMultiplicityFromValues(lowerC2, upperC2));
		
		associationModel = VPModelFactory.setAssociationStereotype(associationModel, association, this.project);
	    this.ontoUml2VpClasses.put(association, associationModel);
		vpPackage.addChild(associationModel);
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

	private void createDerivation(Derivation derivation, IPackage vpPackage) {
		IAssociationClass vpAssClass = IModelElementFactory.instance().createAssociationClass();
		vpAssClass.setFrom(this.ontoUml2VpClasses.get(derivation.relator()));
		vpAssClass.setTo(this.ontoUml2VpClasses.get(derivation.material()));
		vpAssClass.addStereotype(OntoUMLRelationshipType.DERIVATION.getText());
		vpPackage.addChild(vpAssClass);
	}

	private void createGeneralizationSet(GeneralizationSet genSet, IPackage vpPackage){
		IGeneralizationSet vpGenSet = IModelElementFactory.instance().createGeneralizationSet();
		vpGenSet.setName(genSet.getName());
		vpGenSet.setDisjoint(genSet.isIsDisjoint()); vpGenSet.setCovering(genSet.isIsCovering());

		for(Generalization gen : genSet.getGeneralization()){
			vpGenSet.addGeneralization(createGeneralization(gen));
		}

		vpPackage.addChild(vpGenSet);
	}

	private IGeneralization createGeneralization(Generalization gen){
		IGeneralization generalizationModel = IModelElementFactory.instance().createGeneralization();
		IModelElement specific = this.ontoUml2VpClasses.get(gen.getSpecific()),
						general = this.ontoUml2VpClasses.get(gen.getGeneral());
		generalizationModel.setFrom(general);
		generalizationModel.setTo(specific);

		return generalizationModel;
	}

}
