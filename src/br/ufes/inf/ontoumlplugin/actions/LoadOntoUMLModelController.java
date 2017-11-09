package br.ufes.inf.ontoumlplugin.actions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import RefOntoUML.*;
import br.ufes.inf.ontoumlplugin.OntoUMLPlugin;
import com.vp.plugin.model.*;
import org.eclipse.emf.ecore.EObject;
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
	
	private Map<RefOntoUML.Classifier, IModelElement> ontoUml2VpClasses;
	private Map<RefOntoUML.Package, IPackage> ontoUml2VpPackage;
	private final IProject project = ApplicationManager.instance().getProjectManager().getProject();
	private OntoUMLParser ontoUmlParser;
	
	public LoadOntoUMLModelController(){
		this.ontoUml2VpClasses = new HashMap<>();
		this.ontoUml2VpPackage = new HashMap<>();
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

	private void iteratePackages(RefOntoUML.Package ontoUmlPackage, IPackage vpPackage) {
	    for (EObject obj : ontoUmlPackage.eContents()) {
	        if (obj instanceof RefOntoUML.Package) {
	            RefOntoUML.Package oumlPackage = (RefOntoUML.Package) obj;
	            IPackage childPackage = IModelElementFactory.instance().createPackage();
	            childPackage.setName(oumlPackage.getName());
	            this.ontoUml2VpPackage.put(oumlPackage, childPackage);
	            vpPackage.addChild(childPackage);
	            iteratePackages(oumlPackage, childPackage);
            }
        }
    }

    private void iterateClasses() {
	    for (Map.Entry<RefOntoUML.Package, IPackage> entry : ontoUml2VpPackage.entrySet()) {
	        RefOntoUML.Package key = entry.getKey();
	        IPackage value = entry.getValue();
	        for (EObject obj : key.eContents()) {
	            if (obj instanceof RefOntoUML.Class || obj instanceof RefOntoUML.DataType ) {
                    createClass((Classifier)obj, value);
                }
            }
        }
    }

    private void iterateAssociations() {
        for (Map.Entry<RefOntoUML.Package, IPackage> entry : ontoUml2VpPackage.entrySet()) {
            RefOntoUML.Package key = entry.getKey();
            IPackage value = entry.getValue();
            for (EObject obj : key.eContents()) {
                if (obj instanceof RefOntoUML.Association) {
                    RefOntoUML.Association association = (RefOntoUML.Association) obj;
                    if(association instanceof RefOntoUML.Meronymic){
                        RefOntoUML.Meronymic meronymicAssociation = (RefOntoUML.Meronymic) association;
                        createMeronymicAssociation(meronymicAssociation, value);
                    }else if(!(association instanceof RefOntoUML.Derivation) ){
                        createAssociation(association, value);
                    }
                }
            }
        }
    }

    private void iterateGeneralizationSets() {
        for (Map.Entry<RefOntoUML.Package, IPackage> entry : ontoUml2VpPackage.entrySet()) {
            RefOntoUML.Package key = entry.getKey();
            IPackage value = entry.getValue();
            for (EObject obj : key.eContents()) {
                if (obj instanceof RefOntoUML.GeneralizationSet) {
                   createGeneralizationSet((GeneralizationSet)obj, value);
                }
            }
        }
    }

    private void iterateGeneralizations() {
        for (Map.Entry<RefOntoUML.Package, IPackage> entry : ontoUml2VpPackage.entrySet()) {
            RefOntoUML.Package key = entry.getKey();
            IPackage value = entry.getValue();
            for (EObject obj : key.eContents()) {
                if (obj instanceof RefOntoUML.Generalization) {
                    RefOntoUML.Generalization gen = (RefOntoUML.Generalization) obj;
                    if(!isGeneralizationInsideGenSet(ontoUmlParser, gen)) {
                        value.addChild(createGeneralization(gen));
                    }
                }
            }
        }
    }

	private void buildClassDiagram(RefOntoUML.Package ontoUmlPackage){
	    ontoUmlParser = new OntoUMLParser(ontoUmlPackage);
        RefOntoUML.Package model = ontoUmlParser.getModel();
        IPackage modelVpPackage = IModelElementFactory.instance().createPackage();
        modelVpPackage.setName(model.getName());
        this.ontoUml2VpPackage.put(model, modelVpPackage);
        iteratePackages(model, modelVpPackage);
        iterateClasses();
        iterateAssociations();
        iterateGeneralizationSets();
        iterateGeneralizations();
	}

	private void createClass(Classifier c, IPackage vpPackage){
		IClass vpClass = IModelElementFactory.instance().createClass();
		vpClass.setName(c.getName());
		this.ontoUml2VpClasses.put(c, vpClass);

		vpClass = VPModelFactory.setClassStereotype(vpClass, c, this.project);
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
		associationModel.setFrom(partVp);
		associationModel.setTo(wholeVp);
		// specify multiplicity for from & to end
		IAssociationEnd associationFromEnd = (IAssociationEnd) associationModel.getFromEnd();
		associationFromEnd.setMultiplicity(getMultiplicityFromValues(lowerPart, upperC1));
		IAssociationEnd associationToEnd = (IAssociationEnd) associationModel.getToEnd();
		associationToEnd.setMultiplicity(getMultiplicityFromValues(lowerWhole, upperC2));
		associationToEnd.setAggregationKind(association.isIsShareable() ? IAssociationEnd.AGGREGATION_KIND_AGGREGATION :
																			IAssociationEnd.AGGREGATION_KIND_COMPOSITED);
		
		associationModel = VPModelFactory.setMeronymicAssociation(associationModel, association, this.project);
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
		associationModel.setFrom(from1);
		associationModel.setTo(to1);
		// specify multiplicity for from & to end
		IAssociationEnd associationFromEnd = (IAssociationEnd) associationModel.getFromEnd();
		associationFromEnd.setMultiplicity(getMultiplicityFromValues(lowerC1, upperC1));
		IAssociationEnd associationToEnd = (IAssociationEnd) associationModel.getToEnd();
		associationToEnd.setMultiplicity(getMultiplicityFromValues(lowerC2, upperC2));
		
		associationModel = VPModelFactory.setAssociationStereotype(associationModel, association, this.project);
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

	private void createGeneralizationSet(GeneralizationSet genSet, IPackage vpPackage){
		IGeneralizationSet vpGenSet = IModelElementFactory.instance().createGeneralizationSet();
		vpGenSet.setDisjoint(genSet.isIsDisjoint()); vpGenSet.setCovering(genSet.isIsCovering());

		for(Generalization gen : genSet.getGeneralization()){
			vpGenSet.addGeneralization(createGeneralization(gen));
		}
		vpPackage.addChild(vpGenSet);
	}

	private IGeneralization createGeneralization(Generalization gen){
		// create generalization relationship from superclass to subclass
		IGeneralization generalizationModel = IModelElementFactory.instance().createGeneralization();
		IModelElement specific = this.ontoUml2VpClasses.get(gen.getSpecific()),
						general = this.ontoUml2VpClasses.get(gen.getGeneral());
		generalizationModel.setFrom(general);
		generalizationModel.setTo(specific);

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
