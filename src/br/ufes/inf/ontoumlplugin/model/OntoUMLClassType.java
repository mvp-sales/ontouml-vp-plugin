package br.ufes.inf.ontoumlplugin.model;

import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;

public enum OntoUMLClassType {
	KIND("Kind"), COLLECTIVE("Collective"), QUANTITY("Quantity"), 
	SUBKIND("SubKind"), ROLE("Role"), PHASE("Phase"), 
	ROLEMIXIN("RoleMixin"), CATEGORY("Category"), MIXIN("Mixin"), 
	RELATOR("Relator"), MODE("Mode"), QUALITY("Quality"),
	
	DATA_TYPE("DataType"), PERCEIVABLE_QUALITY("PerceivableQuality"),
	NON_PERCEIVABLE_QUALITY("NonPerceivableQuality"), NOMINAL_QUALITY("NominalQuality"), ENUMERATION("enumeration"), PRIMITIVE_TYPE("primitive");
	
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
	    return SUBKIND;
	}

	public static IStereotype getStereotypeFromString(IProject project, String text){
		IModelElement[] stereotypes = project.toModelElementArray(IModelElementFactory.MODEL_TYPE_STEREOTYPE);
		for(IModelElement e : stereotypes){
			IStereotype s = (IStereotype) e;
			if(s.getBaseType().equals(IModelElementFactory.MODEL_TYPE_CLASS) && s.getName().equalsIgnoreCase(text)){
				return s;
			}
		}
		return null;
	}
}
