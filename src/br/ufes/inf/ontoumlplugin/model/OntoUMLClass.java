package br.ufes.inf.ontoumlplugin.model;

import com.vp.plugin.model.IModelElement;

public class OntoUMLClass {
	
	private String name;
	private RefOntoUML.Class metaElement;
	private OntoUMLClassType stereotype;
	
	public OntoUMLClass(IModelElement vpElement, RefOntoUML.Class metaElement, OntoUMLClassType stereotype){
		this.name = vpElement.getName();
		this.metaElement = metaElement;
		this.stereotype = stereotype;
	}
	
	public RefOntoUML.Class getMetaElement(){
		return metaElement;
	}
	
	public OntoUMLClassType getStereotype(){
		return stereotype;
	}
	
	public String getName(){
		return name;
	}

}
