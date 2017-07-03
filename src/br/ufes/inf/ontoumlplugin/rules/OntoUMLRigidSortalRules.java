package br.ufes.inf.ontoumlplugin.rules;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.ISimpleRelationship;
import com.vp.plugin.model.IStereotype;

public class OntoUMLRigidSortalRules {
	
	public static void validateRigidSortal(IClass rigidSortal) {
		validateRigidSortalSupertypes(rigidSortal);
	}
	
	private static void validateRigidSortalSupertypes(IClass rigidSortal) {
		for(ISimpleRelationship relationship :
			rigidSortal.toToRelationshipArray())
		{
			if(relationship instanceof IGeneralization){
				IGeneralization generalization = (IGeneralization) relationship;
				IClass parent = (IClass) generalization.getFrom();
				
				IStereotype stereotype = parent.toStereotypeModelArray()[0];
				switch(stereotype.getName()){
					case "Kind":
					case "Quantity":
					case "Collective":
					case "Subkind":
					case "Role":
					case "Phase":
					case "RoleMixin":
						ApplicationManager
							.instance()
							.getViewManager()
							.showMessage("Can't have " + rigidSortal.getName() + " with stereotype Kind" +
										" as supertype of " + parent.getName() + " with stereotype " +
										stereotype.getName());	
						break;
					default:
						validateTransitiveSupertype(rigidSortal, parent);
						break;
				}
			}
		}
	}
	
	private static void validateTransitiveSupertype(IClass rigidSortal, IClass parent) {
		for(ISimpleRelationship relationship :
			parent.toToRelationshipArray())
	{
		if(relationship instanceof IGeneralization){
			IGeneralization generalization = (IGeneralization) relationship;
			IClass transitiveParent = (IClass) generalization.getFrom();
			
			IStereotype stereotype = transitiveParent.toStereotypeModelArray()[0];
			switch(stereotype.getName()){
				case "Kind":
				case "Quantity":
				case "Collective":
				case "Subkind":
				case "Role":
				case "Phase":
				case "RoleMixin":
					ApplicationManager
						.instance()
						.getViewManager()
						.showMessage("Can't have " + rigidSortal.getName() + " with stereotype Kind" +
									" as subtype of " + transitiveParent.getName() + " with stereotype " +
									stereotype.getName());	
					break;
				default:
					validateTransitiveSupertype(rigidSortal, transitiveParent);
					break;
			}
		}
	}
	}
}
