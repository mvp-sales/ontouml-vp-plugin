package br.ufes.inf.ontoumlplugin.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vp.plugin.model.*;

import RefOntoUML.Package;
import RefOntoUML.util.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import io.reactivex.Observable;

public class RefOntoUMLWrapper {
	
	private Map<IModelElement, RefOntoUML.Classifier> classElements;
	private Map<IModelElement, RefOntoUML.Package> modelPackages;
	public Package ontoUmlPackage;
	
	private RefOntoUMLWrapper(){
		this.classElements = new HashMap<>();
		this.modelPackages = new HashMap<>();
	}
	
	public RefOntoUML.Classifier getOntoUMLClassifier(IModelElement vpElement){
		return classElements.get(vpElement);
	}
	
	public void addOntoUMLClassifier(IModelElement vpElement, RefOntoUML.Classifier classifier){
		this.classElements.put(vpElement, classifier);
	}
	
	public RefOntoUML.Package getOntoUMLPackage(IModelElement vpElement) {
		return this.modelPackages.get(vpElement);
	}

	public static Observable<RefOntoUMLWrapper> createObservableWrapper(IProject vpProject){
		return Observable.fromCallable(
			() -> {
				return createRefOntoUMLModel(vpProject);
			}
		);
	}
	
	public static RefOntoUMLWrapper createRefOntoUMLModel(IProject vpProject){
		RefOntoUMLWrapper wrapper = new RefOntoUMLWrapper();
		
/*		wrapper = addPackages(wrapper, vpProject);
		wrapper = addClasses(wrapper, vpProject);*/
		wrapper = addGeneralizations(wrapper, vpProject);
/*		wrapper = addGeneralizationSets(wrapper, vpProject);
		wrapper = addAssociations(wrapper, vpProject);*/

		return wrapper;
	}
	
	private static RefOntoUMLWrapper addPackages(RefOntoUMLWrapper wrapper, IProject vpProject) {
		for(IModelElement packageElement : vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_PACKAGE))
		{
			IPackage vpPackage = (IPackage) packageElement;
			RefOntoUML.Package ontoUmlPackage = RefOntoUMLFactoryUtil.createPackage(vpPackage.getName());
			wrapper.modelPackages.put(vpPackage, ontoUmlPackage);
		}
		
		for(Map.Entry<IModelElement, RefOntoUML.Package> entry : wrapper.modelPackages.entrySet()) {
			IModelElement key = entry.getKey();
			RefOntoUML.Package value = entry.getValue();
			if(key.getParent() != null) {
				RefOntoUML.Package ppp = wrapper.modelPackages.get(key.getParent());
				ppp.getPackagedElement().add(value);
			}else {
				wrapper.ontoUmlPackage = value;
			}
		}
		
		return wrapper;
	}
	
	private static RefOntoUMLWrapper addClasses(RefOntoUMLWrapper wrapper, IProject vpProject){
		for(IModelElement classElement : vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_CLASS))
		{
			IClass vpClass = (IClass) classElement;
			String vpStereotype = vpClass.toStereotypeModelArray() != null ?
									vpClass.toStereotypeModelArray()[0].getName() :
									"Subkind";
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
				/*if (vpGeneralization.getParent() != null) {
					wrapper.modelPackages.get(vpGeneralization.getParent()).eContents().add(gen);
				}*/
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
							(generalizations, vpGenSet.isDisjoint(), vpGenSet.isCovering(), ontoUmlPackage);
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

			RefOntoUMLFactory.createOntoUMLAssociation(wrapper, vpAssociation, vpStereotype);
		}

		return wrapper;
	}
	
	RefOntoUML.Classifier getOntoUMLClassFromName(String className){
		for(Map.Entry<IModelElement, RefOntoUML.Classifier> entry : this.classElements.entrySet()){
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
