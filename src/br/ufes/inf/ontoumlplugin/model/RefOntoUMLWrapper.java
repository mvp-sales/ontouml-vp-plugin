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
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IGeneralizationSet;
import com.vp.plugin.model.IModelElement;

import RefOntoUML.Package;
import RefOntoUML.util.*;
public class RefOntoUMLWrapper {
	
	
	private Map<IModelElement, OntoUMLClass> ontoUmlClasses;
	private Set<OntoUMLGeneralizationSet> generalizationSets;
	Package ontoUmlPackage;
	
	private RefOntoUMLWrapper(){
		this.ontoUmlPackage = RefOntoUMLFactoryUtil.createPackage("package1");
		this.ontoUmlClasses = new HashMap<>();
		this.generalizationSets = new HashSet<>();
	}
	
	public void addOntoUmlClass(IModelElement vpElement, OntoUMLClass ontoUmlClass){
		this.ontoUmlClasses.put(vpElement, ontoUmlClass);
	}
	
	public OntoUMLClass getOntoUMLClass(IModelElement vpElement){
		return ontoUmlClasses.get(vpElement);
	}
	
	public void addGeneralizationSet(OntoUMLGeneralizationSet genSet){
		this.generalizationSets.add(genSet);
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
			wrapper = addClassElement(wrapper, vpClass, vpStereotype);
		}

		for(IDiagramElement generalizationElement :
			vpDiagram.toDiagramElementArray(IShapeTypeConstants.SHAPE_TYPE_GENERALIZATION))
		{
			IGeneralization vpGeneralization = (IGeneralization) generalizationElement.getMetaModelElement();
			OntoUMLClass parent = wrapper.getOntoUMLClass(vpGeneralization.getFrom()),
							child = wrapper.getOntoUMLClass(vpGeneralization.getTo());
			RefOntoUMLFactoryUtil.createGeneralization(child.getMetaElement(), parent.getMetaElement());
		}

		/*for(IDiagramElement genSetElement :
				vpDiagram.toDiagramElementArray(IShapeTypeConstants.SHAPE_TYPE_GENERALIZATION_SET))
		{
			IGeneralizationSet vpGenSet = (IGeneralizationSet) genSetElement.getMetaModelElement();
			Iterator genIterator = vpGenSet.generalizationIterator();
			OntoUMLClass parent = null;
			OntoUMLClassType genSetType = null;
			List<RefOntoUML.Generalization> generalizations = new ArrayList<>();
			while(genIterator.hasNext()){
				IGeneralization gen = (IGeneralization) genIterator.next();
				RefOntoUML.Generalization generalization = 
					RefOntoUMLFactoryUtil.createGeneralization
						(wrapper.getOntoUMLClass(gen.getFrom()).getMetaElement(), 
						 wrapper.getOntoUMLClass(gen.getTo()).getMetaElement());
				
				generalizations.add(generalization);
				
				if(parent == null){
					parent = wrapper.getOntoUMLClass(gen.getFrom());
				}
				
				if(genSetType == null){
					genSetType = wrapper.getOntoUMLClass(gen.getTo()).getStereotype();
				}
			}
			RefOntoUML.GeneralizationSet genSet = RefOntoUMLFactoryUtil.createGeneralizationSet
							(generalizations, vpGenSet.isDisjoint(), vpGenSet.isCovering(), wrapper.ontoUmlPackage);
			OntoUMLGeneralizationSet ontoUmlGenSet = new OntoUMLGeneralizationSet(parent,genSetType, generalizations);
			wrapper.addGeneralizationSet(ontoUmlGenSet);
		}*/

		/*for(IDiagramElement associationElement :
				vpDiagram.toDiagramElementArray(IShapeTypeConstants.SHAPE_TYPE_ASSOCIATION))
		{
			IAssociation vpAssociation = (IAssociation) associationElement.getMetaModelElement();
			OntoUMLElement from = model.getOntoUMLElement(vpAssociation.getFrom()),
							to = model.getOntoUMLElement(vpAssociation.getTo());

			IAssociationEnd assEndFrom = (IAssociationEnd) vpAssociation.getFromEnd();
			IAssociationEnd assEndTo = (IAssociationEnd) vpAssociation.getToEnd();

			AssociationMultiplicity multFrom =
					new AssociationMultiplicity(assEndTo.getMultiplicity());
			OntoUMLAssociationEnd assEnd =
					new OntoUMLAssociationEnd(assEndTo.getName(),
												multFrom,
												to);
			from.addAssociation(assEnd);

			AssociationMultiplicity multTo =
					new AssociationMultiplicity(assEndFrom.getMultiplicity());
			OntoUMLAssociationEnd assEndOpposite =
					new OntoUMLAssociationEnd(assEndFrom.getName(),
							multTo,
							from);
			to.addAssociation(assEndOpposite);

			assEnd.setOpposite(assEndOpposite);
			assEndOpposite.setOpposite(assEnd);

			OntoUMLAssociation association = new OntoUMLAssociation("", assEnd, assEndOpposite);
			model.addOntoUMLAssociation(vpAssociation, association);

		}
*/
		
		File file = new File("/home/mvp-sales/Documentos/teste.refontouml");
		RefOntoUMLResourceUtil.saveModel(file.getAbsolutePath(), wrapper.ontoUmlPackage);
		
		return wrapper;
	}
	
	private static RefOntoUMLWrapper addClassElement
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
		
		OntoUMLClass ontoUmlClass = new OntoUMLClass(vpElement, classifier, classType);
		wrapper.addOntoUmlClass(vpElement, ontoUmlClass);
		
		return wrapper;
	}
	
	private static RefOntoUMLWrapper addGeneralizationElement
			(RefOntoUMLWrapper wrapper, IGeneralization vpGeneralization)
	{
		OntoUMLClass parent = wrapper.getOntoUMLClass(vpGeneralization.getFrom()),
					child = wrapper.getOntoUMLClass(vpGeneralization.getTo());
		RefOntoUML.Generalization gen = 
				RefOntoUMLFactoryUtil.createGeneralization(parent.getMetaElement(), child.getMetaElement());
		return wrapper;
	}
	
	private static RefOntoUMLWrapper addGeneralizationSets
			(RefOntoUMLWrapper wrapper, IGeneralizationSet vpGenSet)
	{
		return wrapper;
	}
	

}
