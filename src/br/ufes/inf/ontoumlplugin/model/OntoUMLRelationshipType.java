package br.ufes.inf.ontoumlplugin.model;

public enum OntoUMLRelationshipType {

	FORMAL_ASSOCIATION("FormalAssociation"), MEDIATION("Mediation"), CHARACTERIZATION("Characterization"), 
	DERIVATION("Derivation"), MATERIAL_ASSOCIATION("MaterialAssociation"),COMPONENT_OF("ComponentOf"), 
	MEMBER_OF("MemberOf"), SUBCOLLECTION_OF("SubCollectionOf"), SUBQUANTITY_OF("SubQuantityOf"),
	STRUCTURATION("Structuration");
	
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
	    return null;
	  }
}
