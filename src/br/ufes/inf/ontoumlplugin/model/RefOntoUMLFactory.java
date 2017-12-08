package br.ufes.inf.ontoumlplugin.model;



import RefOntoUML.Classifier;
import RefOntoUML.Derivation;
import RefOntoUML.Package;
import com.vp.plugin.model.*;

import RefOntoUML.Association;
import RefOntoUML.util.RefOntoUMLFactoryUtil;

import java.util.ArrayList;


public class RefOntoUMLFactory {

	public static RefOntoUML.Classifier createOntoUmlClass
			(Package container, IModelElement vpClass, String stereotype)
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
				classifier = RefOntoUMLFactoryUtil.createEnumeration(vpClass.getName(), new ArrayList<String>(), container);
				break;
		}

		return classifier;
	}

    private static RefOntoUML.Classifier addOntoUMLAtributes
			(RefOntoUML.Classifier classifier, RefOntoUMLWrapper wrapper, IModelElement vpElement)
    {
        IClass vpClass = (IClass) vpElement;
		for(IAttribute attribute : vpClass.toAttributeArray()){
			String className = attribute.getTypeAsString();
			RefOntoUML.Classifier attributeClassifier = wrapper.getOntoUMLClassFromName(className);
			AssociationMultiplicity multiplicity = new AssociationMultiplicity(attribute.getMultiplicity());
			RefOntoUMLFactoryUtil.createAttribute
									(classifier, 
									attributeClassifier, 
									multiplicity.getMinMultiplicity(), 
									multiplicity.getMaxMultiplicity(), 
									attribute.getName(), 
									false);
        }
        
        return classifier;
    }

	public static Association createOntoUMLAssociation
			(Classifier source, Classifier target, Package container, IAssociation vpAssociation, String stereotype)
	{
		Association association;
		OntoUMLRelationshipType relationType = OntoUMLRelationshipType.fromString(stereotype);

		switch(relationType){
			default:
			case COMMON_ASSOCIATION:
			case CHARACTERIZATION:
			case MEDIATION:
			case FORMAL_ASSOCIATION:
			case MATERIAL_ASSOCIATION:
			case STRUCTURATION:
				association = createCommonAssociation(source, target, container, vpAssociation, relationType);
				break;
			case COMPONENT_OF:
			case MEMBER_OF:
			case SUBQUANTITY_OF:
			case SUBCOLLECTION_OF:
				association = createMeronymicAssociation(wrapper, vpAssociation, relationType);
				break;

		}

		return association;
	}

	private static Association createCommonAssociation
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
			case FORMAL_ASSOCIATION:
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
			case MATERIAL_ASSOCIATION:
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
			case STRUCTURATION:
				association = RefOntoUMLFactoryUtil.createStructuration
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

	private static Association createMeronymicAssociation
			(Classifier source, Classifier target, Package container,
			 IAssociation vpAssociation, OntoUMLRelationshipType type)
	{
		RefOntoUML.Meronymic association;

		String aggregationKind;
		RefOntoUML.Classifier whole,part;
		AssociationMultiplicity multWhole, multPart;

		IAssociationEnd assEndFrom = (IAssociationEnd) vpAssociation.getFromEnd();
		String aggrTypeFrom = assEndFrom.getAggregationKind();
		IAssociationEnd assEndTo = (IAssociationEnd) vpAssociation.getToEnd();
		String aggrTypeTo = assEndTo.getAggregationKind();

		if(aggrTypeFrom.equals(IAssociationEnd.AGGREGATION_KIND_COMPOSITED) ||
				aggrTypeFrom.equals(IAssociationEnd.AGGREGATION_KIND_SHARED)){
			aggregationKind = aggrTypeFrom;
			whole = wrapper.getOntoUMLClassifier(vpAssociation.getFrom());
			multWhole = new AssociationMultiplicity(assEndFrom.getMultiplicity());
			part = wrapper.getOntoUMLClassifier(vpAssociation.getTo());
			multPart = new AssociationMultiplicity(assEndTo.getMultiplicity());
		}else{
			aggregationKind = aggrTypeTo;
			whole = wrapper.getOntoUMLClassifier(vpAssociation.getTo());
			multWhole = new AssociationMultiplicity(assEndTo.getMultiplicity());
			part = wrapper.getOntoUMLClassifier(vpAssociation.getFrom());
			multPart = new AssociationMultiplicity(assEndFrom.getMultiplicity());
		}

		RefOntoUML.Package elementPackage = vpAssociation.getParent() == null || wrapper.getOntoUMLPackage(vpAssociation.getParent()) == null ?
				wrapper.ontoUmlPackage :
				wrapper.getOntoUMLPackage(vpAssociation.getParent());

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
								elementPackage);
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
								elementPackage);
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
								elementPackage);
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
								elementPackage);
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
    
    private static Association createMeronymicAssociation
		(RefOntoUMLWrapper wrapper, IAssociation vpAssociation, OntoUMLRelationshipType type)
	{
		RefOntoUML.Meronymic association;
		
		String aggregationKind;
		RefOntoUML.Classifier whole,part;
		AssociationMultiplicity multWhole, multPart;
		
		IAssociationEnd assEndFrom = (IAssociationEnd) vpAssociation.getFromEnd();
		String aggrTypeFrom = assEndFrom.getAggregationKind();
		IAssociationEnd assEndTo = (IAssociationEnd) vpAssociation.getToEnd();
		String aggrTypeTo = assEndTo.getAggregationKind();
		
		if(aggrTypeFrom.equals(IAssociationEnd.AGGREGATION_KIND_COMPOSITED) || 
			aggrTypeFrom.equals(IAssociationEnd.AGGREGATION_KIND_SHARED)){
			aggregationKind = aggrTypeFrom;
			whole = wrapper.getOntoUMLClassifier(vpAssociation.getFrom());
			multWhole = new AssociationMultiplicity(assEndFrom.getMultiplicity());
			part = wrapper.getOntoUMLClassifier(vpAssociation.getTo());
			multPart = new AssociationMultiplicity(assEndTo.getMultiplicity());
		}else{
			aggregationKind = aggrTypeTo;
			whole = wrapper.getOntoUMLClassifier(vpAssociation.getTo());
			multWhole = new AssociationMultiplicity(assEndTo.getMultiplicity());
			part = wrapper.getOntoUMLClassifier(vpAssociation.getFrom());
			multPart = new AssociationMultiplicity(assEndFrom.getMultiplicity());
		}

		RefOntoUML.Package elementPackage = vpAssociation.getParent() == null || wrapper.getOntoUMLPackage(vpAssociation.getParent()) == null ?
				wrapper.ontoUmlPackage :
				wrapper.getOntoUMLPackage(vpAssociation.getParent());
		
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
						elementPackage);
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
					elementPackage);
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
					elementPackage);
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
					elementPackage);
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
			(RefOntoUMLWrapper wrapper, IAssociationClass vpAssClass)
	{
		//Derivation ontoUmlDerivation;
		RefOntoUML.Classifier relator, material;
		if (vpAssClass.getFrom() instanceof IClass) {
			relator = wrapper.getOntoUMLClassifier(vpAssClass.getFrom());
			material = wrapper.getOntoUMLClassifier(vpAssClass.getTo());
		}else {
			relator = wrapper.getOntoUMLClassifier(vpAssClass.getTo());
			material = wrapper.getOntoUMLClassifier(vpAssClass.getFrom());
		}

		RefOntoUML.Package elementPackage = vpAssClass.getParent() == null || wrapper.getOntoUMLPackage(vpAssClass.getParent()) == null ?
				wrapper.ontoUmlPackage :
				wrapper.getOntoUMLPackage(vpAssClass.getParent());

		return RefOntoUMLFactoryUtil.createDerivation((Association) material, relator, elementPackage);
	}
}