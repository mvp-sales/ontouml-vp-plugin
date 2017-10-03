package br.ufes.inf.ontoumlplugin.model;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.IShapeTypeConstants;
import com.vp.plugin.model.*;
import com.vp.plugin.model.property.IModelProperty;

import RefOntoUML.Association;
import RefOntoUML.Classifier;
import RefOntoUML.Meronymic;
import RefOntoUML.Package;
import RefOntoUML.util.*;
import io.reactivex.Observable;
import org.jfree.util.Log;

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

	public static Observable<RefOntoUMLWrapper> createObservableWrapper(IDiagramUIModel vpDiagram){
		return Observable.fromCallable(
			() -> {
				return createRefOntoUMLModel(vpDiagram);
			}
		);
	}
	
	public static RefOntoUMLWrapper createRefOntoUMLModel(IDiagramUIModel vpDiagram){
		RefOntoUMLWrapper wrapper = new RefOntoUMLWrapper();		

		wrapper = addClasses(wrapper, vpDiagram);
		wrapper = addGeneralizations(wrapper, vpDiagram);
		wrapper = addGeneralizationSets(wrapper, vpDiagram);
		wrapper = addAssociations(wrapper, vpDiagram);

		return wrapper;
	}

	private static RefOntoUMLWrapper addClasses(RefOntoUMLWrapper wrapper, IDiagramUIModel vpDiagram){
		for(IDiagramElement classElement : vpDiagram.toDiagramElementArray(IShapeTypeConstants.SHAPE_TYPE_CLASS))
		{
			IModelElement vpClass = classElement.getMetaModelElement();
			String vpStereotype = vpClass.toStereotypeModelArray().length > 0 ?
									vpClass.toStereotypeModelArray()[0].getName() :
									"Subkind";
			RefOntoUML.Classifier ontoUmlClass = RefOntoUMLFactory.createOntoUmlClass(wrapper, vpClass, vpStereotype);
			wrapper.addOntoUMLClassifier(vpClass, ontoUmlClass);
		}

		return wrapper;
	}

	private static RefOntoUMLWrapper addGeneralizations(RefOntoUMLWrapper wrapper, IDiagramUIModel vpDiagram){
		for(IDiagramElement generalizationElement : 
				vpDiagram.toDiagramElementArray(IShapeTypeConstants.SHAPE_TYPE_GENERALIZATION)) 
		{ 
			IGeneralization vpGeneralization = (IGeneralization) generalizationElement.getMetaModelElement();
			if(vpGeneralization.getGeneralizationSet() == null){
				RefOntoUML.Classifier parent = wrapper.getOntoUMLClassifier(vpGeneralization.getFrom()), 
										child = wrapper.getOntoUMLClassifier(vpGeneralization.getTo()); 
				RefOntoUMLFactoryUtil.createGeneralization(child, parent); 
			}
		} 

		return wrapper;
	}

	private static RefOntoUMLWrapper addGeneralizationSets(RefOntoUMLWrapper wrapper, IDiagramUIModel vpDiagram){
		for(IDiagramElement genSetElement :
				vpDiagram.toDiagramElementArray(IShapeTypeConstants.SHAPE_TYPE_GENERALIZATION_SET))
		{
			IGeneralizationSet vpGenSet = (IGeneralizationSet) genSetElement.getMetaModelElement();
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

	private static RefOntoUMLWrapper addAssociations(RefOntoUMLWrapper wrapper, IDiagramUIModel vpDiagram){
		for(IDiagramElement associationElement :
				vpDiagram.toDiagramElementArray(IShapeTypeConstants.SHAPE_TYPE_ASSOCIATION))
		{
			IAssociation vpAssociation = (IAssociation) associationElement.getMetaModelElement();
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
