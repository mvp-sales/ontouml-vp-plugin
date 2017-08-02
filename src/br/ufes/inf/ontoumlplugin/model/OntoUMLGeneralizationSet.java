package br.ufes.inf.ontoumlplugin.model;

import java.util.Iterator;
import java.util.List;

public class OntoUMLGeneralizationSet {
	
	private OntoUMLClassType childrenStereotype;
	private OntoUMLClass parent;
	private List<RefOntoUML.Generalization> generalizations;
	
	public OntoUMLGeneralizationSet
		(OntoUMLClass parent, OntoUMLClassType stereotype, List<RefOntoUML.Generalization> generalizations)
	{
		this.parent = parent;
		this.childrenStereotype = stereotype;
		this.generalizations = generalizations;
	}
	
	public OntoUMLClass getParent(){
        return this.parent;
    }
	
	public Iterator<RefOntoUML.Generalization> getGeneralizations(){
		return this.generalizations.iterator();
	}
	
	public OntoUMLClassType getChildrenStereotype(){
		return this.childrenStereotype;
	}

}
