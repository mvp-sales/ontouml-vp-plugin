package br.ufes.inf.ontoumlplugin.model;

import java.util.*;

import com.vp.plugin.model.*;

import RefOntoUML.Package;
import RefOntoUML.util.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import io.reactivex.Observable;

public class RefOntoUMLWrapper {
	
	private Map<IModelElement, RefOntoUML.Classifier> classElements;
	public final Package ontoUmlPackage;
	
	private RefOntoUMLWrapper(){
		this.ontoUmlPackage = RefOntoUMLFactoryUtil.createPackage("package1");
		this.classElements = new HashMap<>();
	}
	
	public RefOntoUML.Classifier getOntoUMLClassifier(IModelElement vpElement){
		return classElements.get(vpElement);
	}
	
	public void addOntoUMLClassifier(IModelElement vpElement, RefOntoUML.Classifier classifier){
		this.classElements.put(vpElement, classifier);
	}

	public static Observable<RefOntoUMLWrapper> createObservableWrapper(IProject vpProject){
		return Observable.fromCallable(() -> createRefOntoUMLModel(vpProject));
	}
	
	public static RefOntoUMLWrapper createRefOntoUMLModel(IProject vpProject){
		RefOntoUMLWrapper wrapper = new RefOntoUMLWrapper();		

		wrapper = addClasses(wrapper, vpProject);
		wrapper = addGeneralizations(wrapper, vpProject);
		wrapper = addGeneralizationSets(wrapper, vpProject);
		wrapper = addAssociations(wrapper, vpProject);

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
			RefOntoUMLFactoryUtil.createGeneralizationSet
							(generalizations, vpGenSet.isDisjoint(), vpGenSet.isCovering(), wrapper.ontoUmlPackage);
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
