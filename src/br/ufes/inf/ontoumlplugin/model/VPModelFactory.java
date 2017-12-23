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
            vpClass.addStereotype(OntoUMLClassType.KIND.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.SubKind){
            vpClass.addStereotype(OntoUMLClassType.SUBKIND.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Role){
            vpClass.addStereotype(OntoUMLClassType.ROLE.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Phase){
            vpClass.addStereotype(OntoUMLClassType.PHASE.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Relator){
            vpClass.addStereotype(OntoUMLClassType.RELATOR.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.RoleMixin){
            vpClass.addStereotype(OntoUMLClassType.ROLEMIXIN.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Category){
            vpClass.addStereotype(OntoUMLClassType.CATEGORY.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Quantity){
            vpClass.addStereotype(OntoUMLClassType.QUANTITY.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Collective){
            vpClass.addStereotype(OntoUMLClassType.COLLECTIVE.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Mixin){
            vpClass.addStereotype(OntoUMLClassType.MIXIN.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Mode){
            vpClass.addStereotype(OntoUMLClassType.MODE.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.PerceivableQuality){
            vpClass.addStereotype(OntoUMLClassType.PERCEIVABLE_QUALITY.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.NonPerceivableQuality){
            vpClass.addStereotype(OntoUMLClassType.NON_PERCEIVABLE_QUALITY.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.NominalQuality){
            vpClass.addStereotype(OntoUMLClassType.NOMINAL_QUALITY.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.PrimitiveType){
			vpClass.addStereotype(OntoUMLClassType.PRIMITIVE_TYPE.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.Enumeration){
			vpClass.addStereotype(OntoUMLClassType.ENUMERATION.getText());
		}else if(ontoUmlElement instanceof RefOntoUML.DataType){
            vpClass.addStereotype(OntoUMLClassType.DATA_TYPE.getText());
		}

		vpClass.setAbstract(ontoUmlElement.isIsAbstract());

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
			vpAssociation.addStereotype(OntoUMLRelationshipType.FORMAL.getText());
		}else if(ontoUmlAssociation instanceof RefOntoUML.Mediation){
			vpAssociation.addStereotype(OntoUMLRelationshipType.MEDIATION.getText());
		}else if(ontoUmlAssociation instanceof RefOntoUML.Characterization){
			vpAssociation.addStereotype(OntoUMLRelationshipType.CHARACTERIZATION.getText());
		}else if(ontoUmlAssociation instanceof RefOntoUML.MaterialAssociation){
			vpAssociation.addStereotype(OntoUMLRelationshipType.MATERIAL.getText());
		}

		return vpAssociation;
	}

}
