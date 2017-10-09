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
			addStereotypeClass(vpClass, "Kind", project);
		}else if(ontoUmlElement instanceof RefOntoUML.SubKind){
			addStereotypeClass(vpClass, "SubKind", project);
		}else if(ontoUmlElement instanceof RefOntoUML.Role){
			addStereotypeClass(vpClass, "Role", project);
		}else if(ontoUmlElement instanceof RefOntoUML.Phase){
			addStereotypeClass(vpClass, "Phase", project);
		}else if(ontoUmlElement instanceof RefOntoUML.Relator){
			addStereotypeClass(vpClass, "Relator", project);
		}else if(ontoUmlElement instanceof RefOntoUML.RoleMixin){
			addStereotypeClass(vpClass, "RoleMixin", project);
		}else if(ontoUmlElement instanceof RefOntoUML.Category){
			addStereotypeClass(vpClass, "Category", project);
		}else if(ontoUmlElement instanceof RefOntoUML.Quantity){
			addStereotypeClass(vpClass, "Quantity", project);
		}else if(ontoUmlElement instanceof RefOntoUML.Collective){
			addStereotypeClass(vpClass, "Collective", project);
		}else if(ontoUmlElement instanceof RefOntoUML.Mixin){
			addStereotypeClass(vpClass, "Mixin", project);
		}else if(ontoUmlElement instanceof RefOntoUML.Mode){
			addStereotypeClass(vpClass, "Mode", project);
		}else if(ontoUmlElement instanceof RefOntoUML.Quality){
			addStereotypeClass(vpClass, "Quality", project);
		}else if(ontoUmlElement instanceof RefOntoUML.DataType){
			addStereotypeClass(vpClass, "DataType", project);
		}else if(ontoUmlElement instanceof RefOntoUML.PrimitiveType){
			addStereotypeClass(vpClass, "PrimitiveType", project);
		}else if(ontoUmlElement instanceof RefOntoUML.PerceivableQuality){
			addStereotypeClass(vpClass, "PerceivableQuality", project);
		}else if(ontoUmlElement instanceof RefOntoUML.NonPerceivableQuality){
			addStereotypeClass(vpClass, "NonPerceivableQuality", project);
		}else if(ontoUmlElement instanceof RefOntoUML.NominalQuality){
			addStereotypeClass(vpClass, "NominalQuality", project);
		}else if(ontoUmlElement instanceof RefOntoUML.Enumeration){
			addStereotypeClass(vpClass, "Enumeration", project);
		}else if(ontoUmlElement instanceof RefOntoUML.MeasurementDomain){
			addStereotypeClass(vpClass, "MeasurementDomain", project);
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
			stereotype = addStereotypeAssociation(vpAssociation, "MemberOf", project);
		}else if(ontoUmlAssociation instanceof RefOntoUML.componentOf){
			stereotype = addStereotypeAssociation(vpAssociation, "ComponentOf", project);
		}else if(ontoUmlAssociation instanceof RefOntoUML.subQuantityOf){
			stereotype = addStereotypeAssociation(vpAssociation, "subQuantityOf", project);
		}else{
			stereotype = addStereotypeAssociation(vpAssociation, "subCollectionOf", project);
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
			addStereotypeAssociation(vpAssociation, "FormalAssociation", project);
		}else if(ontoUmlAssociation instanceof RefOntoUML.Mediation){
			addStereotypeAssociation(vpAssociation, "Mediation", project);
		}else if(ontoUmlAssociation instanceof RefOntoUML.Characterization){
			addStereotypeAssociation(vpAssociation, "Characterization", project);
		}else if(ontoUmlAssociation instanceof RefOntoUML.Derivation){
			addStereotypeAssociation(vpAssociation, "Derivation", project);
		}else if(ontoUmlAssociation instanceof RefOntoUML.Structuration){
			addStereotypeAssociation(vpAssociation, "Structuration", project);
		}

		return vpAssociation;
	}

}
