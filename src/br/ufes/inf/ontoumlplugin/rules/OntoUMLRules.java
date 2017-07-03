package br.ufes.inf.ontoumlplugin.rules;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IStereotype;

public class OntoUMLRules {
	
	public static void validateOntoUMLClass(IClass element) {
		IStereotype stereotype = element.toStereotypesModelArray()[0];
		switch(stereotype.getName()) {
			case "Kind":
			case "Collective":
			case "Quantity":
				OntoUMLRigidSortalRules.validateRigidSortal(element);
				break;
			case "Role":
			case "Phase":
				break;
			case "RoleMixin":
			case "Category":
			case "Mixin":
				break;
		}
	}

}
