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
	NON_PERCEIVABLE_QUALITY("NonPerceivableQuality"), NOMINAL_QUALITY("NominalQuality"),
	MEASUREMENT_DOMAIN("MeasurementDomain"), ENUMERATION("Enumeration"),
	STRING_NOMINAL_STRUCTURE("StringNominalStructure"), DECIMAL_INTERVAL_DIMENSION("DecimalIntervalDimension"),
	DECIMAL_ORDINAL_DIMENSION("DecimalOrdinalDimension"), DECIMAL_RATIONAL_DIMENSION("DecimalRationalDimension"),
	INTEGER_INTERVAL_DIMENSION("IntegerIntervalDimension"), INTEGER_ORDINAL_DIMENSION("IntegerOrdinalDimension"),
	INTEGER_RATIONAL_DIMENSION("IntegerRationalDimension"), PRIMITIVE_TYPE("PrimitiveType");
	
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

	public static IStereotype getStereotypeFromString(IProject project, String text){
		IModelElement[] stereotypes = project.toModelElementArray(IModelElementFactory.MODEL_TYPE_STEREOTYPE);
		for(IModelElement e : stereotypes){
			IStereotype s = (IStereotype) e;
			if(s.getName().equalsIgnoreCase(text)){
				return s;
			}
		}
		return null;
	}
}
