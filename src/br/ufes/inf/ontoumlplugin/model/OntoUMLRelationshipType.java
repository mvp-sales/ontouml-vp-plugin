package br.ufes.inf.ontoumlplugin.model;

import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;

public enum OntoUMLRelationshipType {

	COMMON_ASSOCIATION(""), FORMAL("Formal"), MEDIATION("Mediation"), CHARACTERIZATION("Characterization"),
	DERIVATION("Derivation"), MATERIAL("Material"),COMPONENT_OF("ComponentOf"),
	MEMBER_OF("MemberOf"), SUBCOLLECTION_OF("SubCollectionOf"), SUBQUANTITY_OF("SubQuantityOf");
	
	private String text;
	
	OntoUMLRelationshipType(String text){
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
	
	public static OntoUMLRelationshipType fromString(String text) {
	    for (OntoUMLRelationshipType b : OntoUMLRelationshipType.values()) {
			if (b.text.equalsIgnoreCase(text)) {
				return b;
			}
	    }
	    return COMMON_ASSOCIATION;
	}

	public static IStereotype getStereotypeFromString(IProject project, String text){
		IModelElement[] stereotypes = project.toModelElementArray(IModelElementFactory.MODEL_TYPE_STEREOTYPE);
		for(IModelElement e : stereotypes){
			IStereotype s = (IStereotype) e;
			if(s.getBaseType().equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION) && s.getName().equalsIgnoreCase(text)){
				return s;
			}
		}
		return null;
	}
}
