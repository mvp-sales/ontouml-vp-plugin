package br.ufes.inf.ontoumlplugin.model;



import RefOntoUML.Classifier;
import RefOntoUML.Collective;
import RefOntoUML.Package;
import com.vp.plugin.model.*;

import RefOntoUML.Association;
import RefOntoUML.util.RefOntoUMLFactoryUtil;
import com.vp.plugin.model.factory.IModelElementFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class RefOntoUMLFactory {

	public static RefOntoUML.Classifier createOntoUmlClass
			(Package container, IClass vpClass, String stereotype)
	{
		RefOntoUML.Classifier classifier;
		OntoUMLClassType classType = OntoUMLClassType.fromString(stereotype);

		switch(classType){
			default:
			case SUBKIND:
				classifier = RefOntoUMLFactoryUtil.createSubKind(vpClass.getName(), container);
				break;
			case KIND:
				classifier = RefOntoUMLFactoryUtil.createKind(vpClass.getName(), container);
				break;
			case ROLE:
				classifier = RefOntoUMLFactoryUtil.createRole(vpClass.getName(), container);
				break;
			case PHASE:
				classifier = RefOntoUMLFactoryUtil.createPhase(vpClass.getName(), container);
				break;
			case COLLECTIVE:
				classifier = RefOntoUMLFactoryUtil.createCollective(vpClass.getName(), container);
				ITaggedValueContainer taggedValueContainer = vpClass.getTaggedValues();
				ITaggedValue isExtensional = taggedValueContainer.getTaggedValueByName("isExtensional");
                ((Collective)classifier).setIsExtensional(Boolean.valueOf(isExtensional.getValueAsString().toLowerCase()));
				break;
			case QUANTITY:
				classifier = RefOntoUMLFactoryUtil.createQuantity(vpClass.getName(), container);
				break;
			case CATEGORY:
				classifier = RefOntoUMLFactoryUtil.createCategory(vpClass.getName(), container);
				break;
			case ROLEMIXIN:
				classifier = RefOntoUMLFactoryUtil.createRoleMixin(vpClass.getName(), container);
				break;
			case RELATOR:
				classifier = RefOntoUMLFactoryUtil.createRelator(vpClass.getName(), container);
				break;
			case MIXIN:
				classifier = RefOntoUMLFactoryUtil.createMixin(vpClass.getName(), container);
				break;
			case MODE:
				classifier = RefOntoUMLFactoryUtil.createMode(vpClass.getName(), container);
				break;
			case PERCEIVABLE_QUALITY:
				classifier = RefOntoUMLFactoryUtil.createPerceivableQuality(vpClass.getName(), container);
				break;
			case NON_PERCEIVABLE_QUALITY:
				classifier = RefOntoUMLFactoryUtil.createNonPerceivableQuality(vpClass.getName(), container);
				break;
			case NOMINAL_QUALITY:
				classifier = RefOntoUMLFactoryUtil.createNominalQuality(vpClass.getName(), container);
				break;
			case DATA_TYPE:
				classifier = RefOntoUMLFactoryUtil.createDataType(vpClass.getName(), container);
				break;
			case PRIMITIVE_TYPE:
				classifier = RefOntoUMLFactoryUtil.createPrimitiveType(vpClass.getName(), container);
				break;
			case ENUMERATION:
				Collection<String> enumerationLiterals = getEnumerationLiterals(vpClass);
				classifier = RefOntoUMLFactoryUtil.createEnumeration(vpClass.getName(), enumerationLiterals, container);
				break;
		}

		classifier.setIsAbstract(vpClass.isAbstract());

		return classifier;
	}

	private static Collection<String> getEnumerationLiterals(IClass vpClass) {
		List<String> literals = new ArrayList<>();
		for (IModelElement child : vpClass.toChildArray(IModelElementFactory.MODEL_TYPE_ENUMERATION_LITERAL)) {
			literals.add(child.getName());
		}
		return literals;
	}

	public static Association createCommonAssociation
			(Classifier source, Classifier target, Package container,
			 IAssociation vpAssociation, OntoUMLRelationshipType type)
	{
		Association association;

		IAssociationEnd assEndFrom = (IAssociationEnd) vpAssociation.getFromEnd();
		IAssociationEnd assEndTo = (IAssociationEnd) vpAssociation.getToEnd();

		AssociationMultiplicity multFrom = new AssociationMultiplicity(assEndFrom.getMultiplicity());
		AssociationMultiplicity multTo = new AssociationMultiplicity(assEndTo.getMultiplicity());

		switch(type){
			default:
			case COMMON_ASSOCIATION:
				association = RefOntoUMLFactoryUtil.createAssociation
						(source,
								multFrom.getMinMultiplicity(),
								multFrom.getMaxMultiplicity(),
								vpAssociation.getName(),
								target,
								multTo.getMinMultiplicity(),
								multTo.getMaxMultiplicity(),
								container);
				break;
			case CHARACTERIZATION:
				if(target instanceof RefOntoUML.Mode){
					association = RefOntoUMLFactoryUtil.createCharacterization
							(target,
									multTo.getMinMultiplicity(),
									multTo.getMaxMultiplicity(),
									vpAssociation.getName(),
									source,
									multFrom.getMinMultiplicity(),
									multFrom.getMaxMultiplicity(),
									container);
				}else{
					association = RefOntoUMLFactoryUtil.createCharacterization
							(source,
									multFrom.getMinMultiplicity(),
									multFrom.getMaxMultiplicity(),
									vpAssociation.getName(),
									target,
									multTo.getMinMultiplicity(),
									multTo.getMaxMultiplicity(),
									container);
				}
				break;
			case MEDIATION:
				if(target instanceof RefOntoUML.Relator){
					association = RefOntoUMLFactoryUtil.createMediation
							(target,
									multTo.getMinMultiplicity(),
									multTo.getMaxMultiplicity(),
									vpAssociation.getName(),
									source,
									multFrom.getMinMultiplicity(),
									multFrom.getMaxMultiplicity(),
									container);
				}else{
					association = RefOntoUMLFactoryUtil.createMediation
							(source,
									multFrom.getMinMultiplicity(),
									multFrom.getMaxMultiplicity(),
									vpAssociation.getName(),
									target,
									multTo.getMinMultiplicity(),
									multTo.getMaxMultiplicity(),
									container);
				}
				break;
			case FORMAL:
				association = RefOntoUMLFactoryUtil.createFormalAssociation
						(source,
								multFrom.getMinMultiplicity(),
								multFrom.getMaxMultiplicity(),
								vpAssociation.getName(),
								target,
								multTo.getMinMultiplicity(),
								multTo.getMaxMultiplicity(),
								container);
				break;
			case MATERIAL:
				association = RefOntoUMLFactoryUtil.createMaterialAssociation
						(source,
								multFrom.getMinMultiplicity(),
								multFrom.getMaxMultiplicity(),
								vpAssociation.getName(),
								target,
								multTo.getMinMultiplicity(),
								multTo.getMaxMultiplicity(),
								container);
				break;
		}

		return association;
	}

	public static Association createMeronymicAssociation
			(Classifier whole, Classifier part, Package container,
			 IAssociation vpAssociation, String aggregationKind,OntoUMLRelationshipType type)
	{
		RefOntoUML.Meronymic association;
		AssociationMultiplicity multWhole, multPart;

		IAssociationEnd assEndFrom = (IAssociationEnd) vpAssociation.getFromEnd();
		IAssociationEnd assEndTo = (IAssociationEnd) vpAssociation.getToEnd();

		if(vpAssociation.getFrom().getName().equalsIgnoreCase(whole.getName())){
			multWhole = new AssociationMultiplicity(assEndFrom.getMultiplicity());
			multPart = new AssociationMultiplicity(assEndTo.getMultiplicity());
		}else{
			multWhole = new AssociationMultiplicity(assEndTo.getMultiplicity());
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
								container);
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
								container);
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
								container);
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
								container);
				break;
		}

		if(association instanceof RefOntoUML.subQuantityOf || aggregationKind.equals(IAssociationEnd.AGGREGATION_KIND_COMPOSITED)){
			association.setIsShareable(false);
		}else{
			association.setIsShareable(true);
		}

		association = setTaggedValues(vpAssociation, association);

		return association;
	}
    
	private static RefOntoUML.Meronymic setTaggedValues(IAssociation vpAssociation, RefOntoUML.Meronymic ontoUmlAssociation){
		ITaggedValueContainer taggedValuesContainer = vpAssociation.getTaggedValues();
		
		ITaggedValue essential = taggedValuesContainer.getTaggedValueByName("essential");
		ITaggedValue inseparable = taggedValuesContainer.getTaggedValueByName("inseparable");
		ITaggedValue immutablePart = taggedValuesContainer.getTaggedValueByName("immutablePart");
		ITaggedValue immutableWhole = taggedValuesContainer.getTaggedValueByName("immutableWhole");

		ontoUmlAssociation.setIsEssential(Boolean.valueOf(essential.getValueAsString().toLowerCase()));
		ontoUmlAssociation.setIsImmutablePart(ontoUmlAssociation.isIsEssential() || Boolean.valueOf(immutablePart.getValueAsString().toLowerCase()));
		
		ontoUmlAssociation.setIsInseparable(Boolean.valueOf(inseparable.getValueAsString().toLowerCase()));
		ontoUmlAssociation.setIsImmutableWhole(ontoUmlAssociation.isIsInseparable() || Boolean.valueOf(immutableWhole.getValueAsString().toLowerCase()));

		return ontoUmlAssociation;
	}

	public static RefOntoUML.Derivation createOntoUMLDerivation
			(Classifier relator, Classifier material, Package container, IAssociationClass vpAssClass)
	{
		return RefOntoUMLFactoryUtil.createDerivation((Association) material, relator, container);
	}
}