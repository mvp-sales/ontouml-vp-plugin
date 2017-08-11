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
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IGeneralizationSet;
import com.vp.plugin.model.IModelElement;

import RefOntoUML.Classifier;
import RefOntoUML.Package;
import RefOntoUML.util.*;
public class RefOntoUMLWrapper {
	
	private Map<IModelElement, RefOntoUML.Classifier> classifiers; 
	Package ontoUmlPackage;
	
	private RefOntoUMLWrapper(){
		this.ontoUmlPackage = RefOntoUMLFactoryUtil.createPackage("package1");
		this.classifiers = new HashMap<>();
	}
	
	public RefOntoUML.Classifier getOntoUMLClassifier(IModelElement vpElement){
		return classifiers.get(vpElement);
	}
	
	public void addOntoUMLClassifier(IModelElement vpElement, RefOntoUML.Classifier classifier){
		this.classifiers.put(vpElement, classifier);
	}
	
	public static RefOntoUMLWrapper createRefOntoUMLModel(IDiagramUIModel vpDiagram){
		RefOntoUMLWrapper wrapper = new RefOntoUMLWrapper();		

		for(IDiagramElement classElement :
				vpDiagram.toDiagramElementArray(IShapeTypeConstants.SHAPE_TYPE_CLASS))
		{
			IModelElement vpClass = classElement.getMetaModelElement();
			String vpStereotype = vpClass.toStereotypeModelArray().length > 0 ?
									vpClass.toStereotypeModelArray()[0].getName() :
									"Subkind";
			wrapper.addOntoUMLClassifier(vpClass, createOntoUMLElement(wrapper, vpClass, vpStereotype));
		}
		
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
		

		for(IDiagramElement associationElement :
				vpDiagram.toDiagramElementArray(IShapeTypeConstants.SHAPE_TYPE_ASSOCIATION))
		{
			IAssociation vpAssociation = (IAssociation) associationElement.getMetaModelElement();
			RefOntoUML.Classifier from = wrapper.getOntoUMLClassifier(vpAssociation.getFrom()),
							to = wrapper.getOntoUMLClassifier(vpAssociation.getTo());

			IAssociationEnd assEndFrom = (IAssociationEnd) vpAssociation.getFromEnd();
			IAssociationEnd assEndTo = (IAssociationEnd) vpAssociation.getToEnd();
			
			AssociationMultiplicity multFrom = new AssociationMultiplicity(assEndFrom.getMultiplicity());
			AssociationMultiplicity multTo = new AssociationMultiplicity(assEndTo.getMultiplicity());
			
			RefOntoUMLFactoryUtil.createAssociation
				(from, 
					multFrom.getMinMultiplicity(), 
					multFrom.getMaxMultiplicity(), 
					vpAssociation.getName(), 
					to, 
					multTo.getMinMultiplicity(), 
					multTo.getMaxMultiplicity(), 
					wrapper.ontoUmlPackage);

		}

		
		File file = new File("/home/mvp-sales/Documentos/teste.refontouml");
		RefOntoUMLResourceUtil.saveModel(file.getAbsolutePath(), wrapper.ontoUmlPackage);
		
		return wrapper;
	}
	
	private static Classifier createOntoUMLElement
				(RefOntoUMLWrapper wrapper, IModelElement vpElement, String stereotype)
	{
		RefOntoUML.Class classifier;
		OntoUMLClassType classType = OntoUMLClassType.fromString(stereotype);
		
		switch(classType){
			default:
			case KIND:
				classifier = RefOntoUMLFactoryUtil.createKind(vpElement.getName(), wrapper.ontoUmlPackage);
				break;
			case SUBKIND:
				classifier = RefOntoUMLFactoryUtil.createSubKind(vpElement.getName(), wrapper.ontoUmlPackage);
				break;
			case ROLE:
				classifier = RefOntoUMLFactoryUtil.createRole(vpElement.getName(), wrapper.ontoUmlPackage);
				break;
			case PHASE:
				classifier = RefOntoUMLFactoryUtil.createPhase(vpElement.getName(), wrapper.ontoUmlPackage);		
				break;
			case COLLECTIVE:
				classifier = RefOntoUMLFactoryUtil.createCollective(vpElement.getName(), wrapper.ontoUmlPackage);
				break;
			case QUANTITY:
				classifier = RefOntoUMLFactoryUtil.createQuantity(vpElement.getName(), wrapper.ontoUmlPackage);
				break;
			case CATEGORY:
				classifier = RefOntoUMLFactoryUtil.createCategory(vpElement.getName(), wrapper.ontoUmlPackage);
				break;
			case ROLEMIXIN:
				classifier = RefOntoUMLFactoryUtil.createRoleMixin(vpElement.getName(), wrapper.ontoUmlPackage);
				break;
			case RELATOR:
				classifier = RefOntoUMLFactoryUtil.createRelator(vpElement.getName(), wrapper.ontoUmlPackage);
				break;
			case MIXIN:
				classifier = RefOntoUMLFactoryUtil.createMixin(vpElement.getName(), wrapper.ontoUmlPackage);
				break;
			case MODE:
				classifier = RefOntoUMLFactoryUtil.createMode(vpElement.getName(), wrapper.ontoUmlPackage);
				break;
		}
		
		return classifier;
	}

	

}
