package br.ufes.inf.ontoumlplugin.model;

import java.util.*;

import RefOntoUML.Association;
import RefOntoUML.Derivation;
import com.vp.plugin.model.*;

import RefOntoUML.Package;
import RefOntoUML.util.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import io.reactivex.Observable;

public class RefOntoUMLWrapper {
	
	private Map<IModelElement, RefOntoUML.Classifier> classifierElements;
	private Map<IModelElement, RefOntoUML.Package> modelPackages;
	public Package ontoUmlPackage;
	
	private RefOntoUMLWrapper(){
		this.ontoUmlPackage = RefOntoUMLFactoryUtil.createPackage("OntoUMLModel");
		this.modelPackages = new HashMap<>();
		this.classifierElements = new HashMap<>();
	}
	
	public RefOntoUML.Classifier getOntoUMLClassifier(IModelElement vpElement){
		return classifierElements.get(vpElement);
	}

	public RefOntoUML.Package getOntoUMLPackage(IModelElement vpElement) {
		return this.modelPackages.get(vpElement);
	}
	
	private void addOntoUMLClassifier(IModelElement vpElement, RefOntoUML.Classifier classifier){
		this.classifierElements.put(vpElement, classifier);
	}

	public static Observable<RefOntoUMLWrapper> createObservableWrapper(IProject vpProject){
		return Observable.fromCallable(() -> createRefOntoUMLModel(vpProject));
	}
	
	private static RefOntoUMLWrapper createRefOntoUMLModel(IProject vpProject){
		RefOntoUMLWrapper wrapper = new RefOntoUMLWrapper();		

		wrapper = addPackages(wrapper, vpProject);
		wrapper = addClasses(wrapper, vpProject);
		wrapper = addAssociations(wrapper, vpProject);
		wrapper = addGeneralizations(wrapper, vpProject);
		wrapper = addGeneralizationSets(wrapper, vpProject);
		wrapper = addComments(wrapper, vpProject);

		return wrapper;
	}

	private static RefOntoUMLWrapper addPackages(RefOntoUMLWrapper wrapper, IProject vpProject) {
		for(IModelElement packageElement : vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_PACKAGE))
		{
			IPackage vpPackage = (IPackage) packageElement;
			RefOntoUML.Package ontoUmlPackage = wrapper.getOrCreatePackage(vpPackage);
			if (vpPackage.getParent() != null){
				IPackage vpParentPackage = (IPackage) vpPackage.getParent();
				Package parentPackage = wrapper.getOrCreatePackage(vpParentPackage);
				parentPackage.getPackagedElement().add(ontoUmlPackage);
			} else {
				wrapper.ontoUmlPackage = ontoUmlPackage;
			}
			wrapper.modelPackages.put(vpPackage, ontoUmlPackage);
		}

		return wrapper;
	}

	private Package getOrCreatePackage(IPackage vpPackage) {
		Package ontoUmlPackage;
		if (!this.modelPackages.containsKey(vpPackage)){
			ontoUmlPackage = RefOntoUMLFactoryUtil.createPackage(vpPackage.getName());
			ontoUmlPackage.setName(vpPackage.getName());
			this.modelPackages.put(vpPackage, ontoUmlPackage);
		}else{
			ontoUmlPackage = this.modelPackages.get(vpPackage);
		}
		return ontoUmlPackage;
	}

	private static RefOntoUMLWrapper addClasses(RefOntoUMLWrapper wrapper, IProject vpProject){
		for(IModelElement classElement : vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_CLASS))
		{
			IClass vpClass = (IClass) classElement;
			String vpStereotype = vpClass.toStereotypeArray().length != 0?
									vpClass.toStereotypeArray()[0] :
									OntoUMLClassType.SUBKIND.getText();
			RefOntoUML.Classifier ontoUmlClass = RefOntoUMLFactory.createOntoUmlClass(wrapper, vpClass, vpStereotype);
			wrapper.addOntoUMLClassifier(vpClass, ontoUmlClass);
		}

		return wrapper;
	}

	private static RefOntoUMLWrapper addGeneralizations(RefOntoUMLWrapper wrapper, IProject vpProject){
		for(IModelElement generalizationElement :
				vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_GENERALIZATION))
		{ 
			IGeneralization vpGeneralization = (IGeneralization) generalizationElement;
			if(vpGeneralization.getGeneralizationSet() == null){
				RefOntoUML.Classifier parent = wrapper.getOntoUMLClassifier(vpGeneralization.getFrom()), 
										child = wrapper.getOntoUMLClassifier(vpGeneralization.getTo()); 
				RefOntoUMLFactoryUtil.createGeneralization(child, parent); 
			}
		} 

		return wrapper;
	}

	private static RefOntoUMLWrapper addGeneralizationSets(RefOntoUMLWrapper wrapper, IProject vpProject){
		for(IModelElement genSetElement :
				vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET))
		{
			IGeneralizationSet vpGenSet = (IGeneralizationSet) genSetElement;
			Iterator genIterator = vpGenSet.generalizationIterator();
			List<RefOntoUML.Generalization> generalizations = new ArrayList<>();
			while(genIterator.hasNext()){
				IGeneralization gen = (IGeneralization) genIterator.next();
				RefOntoUML.Generalization generalization = 
					RefOntoUMLFactoryUtil.createGeneralization
						(wrapper.getOntoUMLClassifier(gen.getTo()),
						wrapper.getOntoUMLClassifier(gen.getFrom()));
				
				generalizations.add(generalization);
			}

			RefOntoUML.Package ontoUmlPackage = vpGenSet.getParent() == null ?
					wrapper.ontoUmlPackage :
					wrapper.modelPackages.get(vpGenSet.getParent());

			RefOntoUMLFactoryUtil.createGeneralizationSet
			(
				generalizations,
				vpGenSet.getName(),
				vpGenSet.isDisjoint(),
				vpGenSet.isCovering(),
				ontoUmlPackage
			);
		} 

		return wrapper;
	}

	private static RefOntoUMLWrapper addAssociations(RefOntoUMLWrapper wrapper, IProject vpProject){
		for(IModelElement associationElement :
				vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_ASSOCIATION))
		{
			IAssociation vpAssociation = (IAssociation) associationElement;
			String vpStereotype = vpAssociation.toStereotypeArray().length > 0 ?
					vpAssociation.toStereotypeArray()[0] : "";

			Association association = RefOntoUMLFactory.createOntoUMLAssociation(wrapper, vpAssociation, vpStereotype);
			wrapper.classifierElements.put(vpAssociation, association);
		}

		for(IModelElement associationElement :
				vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS))
		{
			IAssociationClass vpAssociationClass = (IAssociationClass) associationElement;
			Derivation derivation = RefOntoUMLFactory.createOntoUMLDerivation(wrapper, vpAssociationClass);
			wrapper.classifierElements.put(vpAssociationClass, derivation);
		}

		/*for (IModelElement element : wrapper.modelPackages.keySet()) {
		    IPackage pkg = (IPackage) element;
		    for (IModelElement child : pkg.toChildArray()) {
                System.out.println(child.getModelType() + " - " + child.getName());
            }
        }*/

		return wrapper;
	}

	private static RefOntoUMLWrapper addComments(RefOntoUMLWrapper wrapper, IProject vpProject) {
		for(IModelElement anchorElement :
				vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_ANCHOR))
		{
			IModelElement container;
			INOTE note;
			IAnchor vpAnchor = (IAnchor) anchorElement;
			if (vpAnchor.getFrom() instanceof INOTE){
				container = vpAnchor.getTo();
				note = (INOTE) vpAnchor.getFrom();
			} else {
				container = vpAnchor.getFrom();
				note = (INOTE) vpAnchor.getTo();
			}
			if (wrapper.classifierElements.containsKey(container)) {
				RefOntoUMLFactoryUtil.createComment(note.toString(), wrapper.classifierElements.get(container));
			}
		}

		return wrapper;
	}
	
	RefOntoUML.Classifier getOntoUMLClassFromName(String className){
		for(Map.Entry<IModelElement, RefOntoUML.Classifier> entry : this.classifierElements.entrySet()){
			IModelElement elem = entry.getKey();
			if(elem.getName().equals(className)){
				return entry.getValue();
			}
		}
		return null;
	}

	
	public static Observable<RefOntoUML.parser.SyntacticVerificator> getVerificator(RefOntoUMLWrapper wrapper){
		return Observable.fromCallable(
			() -> {
				RefOntoUML.parser.SyntacticVerificator verificator = new RefOntoUML.parser.SyntacticVerificator();
				verificator.run(wrapper.ontoUmlPackage);
				return verificator;
			}
		);
	}
}
