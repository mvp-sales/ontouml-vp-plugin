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
import com.vp.plugin.model.property.IModelProperty;

import RefOntoUML.Association;
import RefOntoUML.Classifier;
import RefOntoUML.Package;
import RefOntoUML.util.*;
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

	public static Observable<RefOntoUMLWrapper> createObservableWrapper(IDiagramUIModel vpDiagram){
		return Observable.fromCallable(
			() -> {
				return createRefOntoUMLModel(vpDiagram);
			}
		);
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
			String vpStereotype = vpAssociation.toStereotypeArray().length > 0 ?
					vpAssociation.toStereotypeArray()[0] : "";
			createOntoUMLAssociation(wrapper, vpAssociation, vpStereotype);

		}

		return wrapper;
	}
	
	private static Classifier createOntoUMLElement
				(RefOntoUMLWrapper wrapper, IModelElement vpElement, String stereotype)
	{
		RefOntoUML.Class classifier;
		OntoUMLClassType classType = OntoUMLClassType.fromString(stereotype);
		
		if(classType == null){
			classifier = RefOntoUMLFactoryUtil.createSubKind(vpElement.getName(), wrapper.ontoUmlPackage);
		}else{
		
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
		}
		
		for(IModelProperty property : vpElement.toModelPropertyArray()){
			//RefOntoUMLFactoryUtil.createP
		}
		
		return classifier;
	}
	
	private static Association createOntoUMLAssociation
				(RefOntoUMLWrapper wrapper, IAssociation vpAssociation, String stereotype){
		Association association;
		OntoUMLRelationshipType relationType = OntoUMLRelationshipType.fromString(stereotype);
		
		if(relationType == null){
			association = createCommonAssociation(wrapper, vpAssociation, null);
		}else{
			switch(relationType){
				default:
				case CHARACTERIZATION:
				case MEDIATION:
				case FORMAL_ASSOCIATION:
				case MATERIAL_ASSOCIATION:
					association = createCommonAssociation(wrapper, vpAssociation, relationType);
					break;
				case COMPONENT_OF:
				case MEMBER_OF:
				case SUBQUANTITY_OF:
				case SUBCOLLECTION_OF:
					association = createMeronymicAssociation(wrapper, vpAssociation, relationType);
					break;
				
			}
		}
		
		return association;
	}
	
	private static Association createCommonAssociation
				(RefOntoUMLWrapper wrapper, IAssociation vpAssociation, OntoUMLRelationshipType type)
	{
		Association association;

		RefOntoUML.Classifier source = wrapper.getOntoUMLClassifier(vpAssociation.getFrom()),
				target = wrapper.getOntoUMLClassifier(vpAssociation.getTo());

		IAssociationEnd assEndFrom = (IAssociationEnd) vpAssociation.getFromEnd();
		IAssociationEnd assEndTo = (IAssociationEnd) vpAssociation.getToEnd();
		
		AssociationMultiplicity multFrom = new AssociationMultiplicity(assEndFrom.getMultiplicity());
		AssociationMultiplicity multTo = new AssociationMultiplicity(assEndTo.getMultiplicity());
		
		if(type == null){
			association = RefOntoUMLFactoryUtil.createAssociation
					(source, 
						multFrom.getMinMultiplicity(), 
						multFrom.getMaxMultiplicity(), 
						vpAssociation.getName(), 
						target, 
						multTo.getMinMultiplicity(), 
						multTo.getMaxMultiplicity(), 
						wrapper.ontoUmlPackage);
		}else{
			switch(type){
				default:
					association = RefOntoUMLFactoryUtil.createAssociation
					(source, 
						multFrom.getMinMultiplicity(), 
						multFrom.getMaxMultiplicity(), 
						vpAssociation.getName(), 
						target, 
						multTo.getMinMultiplicity(), 
						multTo.getMaxMultiplicity(), 
						wrapper.ontoUmlPackage);
				case CHARACTERIZATION:
					association = RefOntoUMLFactoryUtil.createCharacterization
									(source, 
										multFrom.getMinMultiplicity(), 
										multFrom.getMaxMultiplicity(), 
										vpAssociation.getName(), 
										target, 
										multTo.getMinMultiplicity(), 
										multTo.getMaxMultiplicity(), 
										wrapper.ontoUmlPackage);
					break;
				case MEDIATION:
					association = RefOntoUMLFactoryUtil.createCharacterization
									(source, 
										multFrom.getMinMultiplicity(), 
										multFrom.getMaxMultiplicity(), 
										vpAssociation.getName(), 
										target, 
										multTo.getMinMultiplicity(), 
										multTo.getMaxMultiplicity(), 
										wrapper.ontoUmlPackage);
					break;
				case FORMAL_ASSOCIATION:
					association = RefOntoUMLFactoryUtil.createFormalAssociation
									(source, 
										multFrom.getMinMultiplicity(), 
										multFrom.getMaxMultiplicity(), 
										vpAssociation.getName(), 
										target, 
										multTo.getMinMultiplicity(), 
										multTo.getMaxMultiplicity(), 
										wrapper.ontoUmlPackage);
					break;
				case MATERIAL_ASSOCIATION:
					association = RefOntoUMLFactoryUtil.createMaterialAssociation
					(source, 
						multFrom.getMinMultiplicity(), 
						multFrom.getMaxMultiplicity(), 
						vpAssociation.getName(), 
						target, 
						multTo.getMinMultiplicity(), 
						multTo.getMaxMultiplicity(), 
						wrapper.ontoUmlPackage);
					break;
			}
		}
		
		return association;
	}
	
	private static Association createMeronymicAssociation
				(RefOntoUMLWrapper wrapper, IAssociation vpAssociation, OntoUMLRelationshipType type)
	{
		Association association;
		
		RefOntoUML.Classifier whole,part;
		AssociationMultiplicity multWhole, multPart;

		IAssociationEnd assEndFrom = (IAssociationEnd) vpAssociation.getFromEnd();
		String aggrTypeFrom = assEndFrom.getAggregationKind();
		IAssociationEnd assEndTo = (IAssociationEnd) vpAssociation.getToEnd();
		String aggrTypeTo = assEndTo.getAggregationKind();
		
		if(aggrTypeFrom.equals(IAssociationEnd.AGGREGATION_KIND_COMPOSITED) || 
				aggrTypeFrom.equals(IAssociationEnd.AGGREGATION_KIND_SHARED)){
			whole = wrapper.getOntoUMLClassifier(vpAssociation.getFrom());
			multWhole = new AssociationMultiplicity(assEndFrom.getMultiplicity());
			part = wrapper.getOntoUMLClassifier(vpAssociation.getTo());
			multPart = new AssociationMultiplicity(assEndTo.getMultiplicity());
		}else{
			whole = wrapper.getOntoUMLClassifier(vpAssociation.getTo());
			multWhole = new AssociationMultiplicity(assEndTo.getMultiplicity());
			part = wrapper.getOntoUMLClassifier(vpAssociation.getFrom());
			multPart = new AssociationMultiplicity(assEndFrom.getMultiplicity());
		}
		
		switch(type){
			default:
			case COMPONENT_OF:
				association = RefOntoUMLFactoryUtil.createComponentOf
					(whole, 
						multWhole.getMinMultiplicity(), 
						multWhole.getMaxMultiplicity(), 
						vpAssociation.getName(), 
						part, 
						multPart.getMinMultiplicity(), 
						multPart.getMaxMultiplicity(), 
						wrapper.ontoUmlPackage);
				break;
			case MEMBER_OF:
				association = RefOntoUMLFactoryUtil.createMemberOf
				(whole, 
					multWhole.getMinMultiplicity(), 
					multWhole.getMaxMultiplicity(), 
					vpAssociation.getName(), 
					part, 
					multPart.getMinMultiplicity(), 
					multPart.getMaxMultiplicity(), 
					wrapper.ontoUmlPackage);
				break;
			case SUBQUANTITY_OF:
				association = RefOntoUMLFactoryUtil.createSubQuantityOf
				(whole, 
					multWhole.getMinMultiplicity(), 
					multWhole.getMaxMultiplicity(), 
					vpAssociation.getName(), 
					part, 
					multPart.getMinMultiplicity(), 
					multPart.getMaxMultiplicity(), 
					wrapper.ontoUmlPackage);
				break;
			case SUBCOLLECTION_OF:
				association = RefOntoUMLFactoryUtil.createSubCollectionOf
				(whole, 
					multWhole.getMinMultiplicity(), 
					multWhole.getMaxMultiplicity(), 
					vpAssociation.getName(), 
					part, 
					multPart.getMinMultiplicity(), 
					multPart.getMaxMultiplicity(), 
					wrapper.ontoUmlPackage);
				break;
		}
		
		return association;
		
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
