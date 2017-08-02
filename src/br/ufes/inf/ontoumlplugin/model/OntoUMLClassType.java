package br.ufes.inf.ontoumlplugin.model;

public enum OntoUMLClassType {
	KIND("Kind"), COLLECTIVE("Collective"), QUANTITY("Quantity"), 
	SUBKIND("SubKind"), ROLE("Role"), PHASE("Phase"), 
	ROLEMIXIN("RoleMixin"), CATEGORY("Category"), MIXIN("Mixin"), 
	RELATOR("Relator"), MODE("Mode"), QUALITY("Quality");
	
	private String text;
	
	OntoUMLClassType(String text){
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
	
	public static OntoUMLClassType fromString(String text) {
	    for (OntoUMLClassType b : OntoUMLClassType.values()) {
	      if (b.text.equalsIgnoreCase(text)) {
	        return b;
	      }
	    }
	    return null;
	  }
}
