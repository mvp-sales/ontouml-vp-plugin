package br.ufes.inf.ontoumlplugin.model;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import com.vp.plugin.model.factory.IModelElementFactory;

import RefOntoUML.Classifier;

public class VPModelFactory {
	
	public static IClass setClassStereotype(IClass vpClass, Classifier ontoUmlElement, IProject project){
		if(ontoUmlElement instanceof RefOntoUML.Kind){
			//addStereotypeClass(vpClass, "Kind", project);
            vpClass.addStereotype(OntoUMLClassType.KIND.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.SubKind){
			//addStereotypeClass(vpClass, "SubKind", project);
            vpClass.addStereotype(OntoUMLClassType.SUBKIND.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Role){
			//addStereotypeClass(vpClass, "Role", project);
            vpClass.addStereotype(OntoUMLClassType.ROLE.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Phase){
			//addStereotypeClass(vpClass, "Phase", project);
            vpClass.addStereotype(OntoUMLClassType.PHASE.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Relator){
			//addStereotypeClass(vpClass, "Relator", project);
            vpClass.addStereotype(OntoUMLClassType.RELATOR.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.RoleMixin){
			//addStereotypeClass(vpClass, "RoleMixin", project);
            vpClass.addStereotype(OntoUMLClassType.ROLEMIXIN.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Category){
			//addStereotypeClass(vpClass, "Category", project);
            vpClass.addStereotype(OntoUMLClassType.CATEGORY.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Quantity){
			//addStereotypeClass(vpClass, "Quantity", project);
            vpClass.addStereotype(OntoUMLClassType.QUANTITY.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Collective){
			//addStereotypeClass(vpClass, "Collective", project);
            vpClass.addStereotype(OntoUMLClassType.COLLECTIVE.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Mixin){
			//addStereotypeClass(vpClass, "Mixin", project);
            vpClass.addStereotype(OntoUMLClassType.MIXIN.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Mode){
			//addStereotypeClass(vpClass, "Mode", project);
            vpClass.addStereotype(OntoUMLClassType.MODE.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.PerceivableQuality){
			//addStereotypeClass(vpClass, "PerceivableQuality", project);
            vpClass.addStereotype(OntoUMLClassType.PERCEIVABLE_QUALITY.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.NonPerceivableQuality){
			//addStereotypeClass(vpClass, "NonPerceivableQuality", project);
            vpClass.addStereotype(OntoUMLClassType.NON_PERCEIVABLE_QUALITY.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.NominalQuality){
			//addStereotypeClass(vpClass, "NominalQuality", project);
            vpClass.addStereotype(OntoUMLClassType.NOMINAL_QUALITY.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.PrimitiveType){
			vpClass.addStereotype(OntoUMLClassType.PRIMITIVE_TYPE.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Enumeration){
			vpClass.addStereotype(OntoUMLClassType.ENUMERATION.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.DataType){
			//addStereotypeClass(vpClass, "DataType", project);
            vpClass.addStereotype(OntoUMLClassType.DATA_TYPE.getText());
		}

		vpClass = setVPAttributes(vpClass, ontoUmlElement);

		return vpClass;
	}
	
	private static void addStereotypeClass(IClass vpClass, String stereotypeStr, IProject project){
		IStereotype stereotype = OntoUMLClassType.getStereotypeFromString(project, stereotypeStr);
		if(stereotype != null){
			vpClass.addStereotype(stereotype);
		}
	}
	
	private static IClass setVPAttributes(IClass vpClass, RefOntoUML.Classifier ontoUmlElement){
		for(RefOntoUML.Property attribute : ontoUmlElement.getAttribute()){
			IAttribute vpAttribute = IModelElementFactory.instance().createAttribute();
			vpAttribute.setName(attribute.getName());
			vpAttribute.setType(attribute.getType().getName());
			AssociationMultiplicity multiplicity = new AssociationMultiplicity(attribute.getLower(), attribute.getUpper());
			vpAttribute.setMultiplicity(multiplicity.getMultiplicityString());
			vpClass.addAttribute(vpAttribute);
		}
		
		return vpClass;
	}
	
	public static IAssociation setMeronymicAssociation
			(IAssociation vpAssociation, RefOntoUML.Meronymic ontoUmlAssociation, IProject project)
	{

		IStereotype stereotype;
		
		if(ontoUmlAssociation instanceof RefOntoUML.memberOf){
			stereotype = addStereotypeAssociation(vpAssociation, OntoUMLRelationshipType.MEMBER_OF.getText(), project);
		}else if(ontoUmlAssociation instanceof RefOntoUML.componentOf){
			stereotype = addStereotypeAssociation(vpAssociation, OntoUMLRelationshipType.COMPONENT_OF.getText(), project);
		}else if(ontoUmlAssociation instanceof RefOntoUML.subQuantityOf){
			stereotype = addStereotypeAssociation(vpAssociation, OntoUMLRelationshipType.SUBQUANTITY_OF.getText(), project);
		}else{
			stereotype = addStereotypeAssociation(vpAssociation, OntoUMLRelationshipType.SUBCOLLECTION_OF.getText(), project);
		}

		if(stereotype != null){
			ITaggedValueContainer container = vpAssociation.getTaggedValues();
			ITaggedValue inseparable = container.getTaggedValueByName("inseparable");
			ITaggedValue immutableWhole = container.getTaggedValueByName("immutableWhole");
			ITaggedValue immutablePart = container.getTaggedValueByName("immutablePart");
			ITaggedValue essential = container.getTaggedValueByName("essential");
			ITaggedValue shareable = container.getTaggedValueByName("shareable");
			
			inseparable.setValue(ontoUmlAssociation.isIsInseparable() ? "True" : "False");
			immutableWhole.setValue(ontoUmlAssociation.isIsImmutableWhole() ? "True" : "False");
			immutablePart.setValue(ontoUmlAssociation.isIsImmutablePart() ? "True" : "False");

			if(essential != null){
				essential.setValue(ontoUmlAssociation.isIsEssential() ? "True" : "False");
			}
			
			shareable.setValue(ontoUmlAssociation.isIsShareable() ? "True" : "False");
		}

		return vpAssociation;
	}

	private static IStereotype addStereotypeAssociation
		(IAssociation vpAssociation, String stereotypeStr, IProject project)
	{
		IStereotype stereotype = OntoUMLRelationshipType.getStereotypeFromString(project, stereotypeStr);
		if(stereotype != null){
			vpAssociation.addStereotype(stereotype);
		}

		return stereotype;
	}
	
	public static IAssociation setAssociationStereotype
			(IAssociation vpAssociation, RefOntoUML.Association ontoUmlAssociation, IProject project)
	{

		if(ontoUmlAssociation instanceof RefOntoUML.FormalAssociation){
			//addStereotypeAssociation(vpAssociation, "Formal", project);
			vpAssociation.addStereotype(OntoUMLRelationshipType.FORMAL_ASSOCIATION.getText());
		}else if(ontoUmlAssociation instanceof RefOntoUML.Mediation){
			//addStereotypeAssociation(vpAssociation, "Mediation", project);
			vpAssociation.addStereotype(OntoUMLRelationshipType.MEDIATION.getText());
		}else if(ontoUmlAssociation instanceof RefOntoUML.Characterization){
			//addStereotypeAssociation(vpAssociation, "Characterization", project);
			vpAssociation.addStereotype(OntoUMLRelationshipType.CHARACTERIZATION.getText());
		}else if(ontoUmlAssociation instanceof RefOntoUML.MaterialAssociation){
			//addStereotypeAssociation(vpAssociation, "Material", project);
			vpAssociation.addStereotype(OntoUMLRelationshipType.MATERIAL_ASSOCIATION.getText());
		}else if (ontoUmlAssociation instanceof RefOntoUML.Structuration) {
			//addStereotypeAssociation(vpAssociation, "Structuration", project);
			vpAssociation.addStereotype(OntoUMLRelationshipType.STRUCTURATION.getText());
		}

		return vpAssociation;
	}

}
