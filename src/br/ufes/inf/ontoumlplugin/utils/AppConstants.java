package br.ufes.inf.ontoumlplugin.utils;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;
import br.ufes.inf.ontoumlplugin.model.OntoUMLRelationshipType;

public class AppConstants {
    public static final String IS_EXTENSIONAL = "isExtensional";

    public static final String[] CLASS_STEREOTYPES = {
            OntoUMLClassType.KIND.getText(),
            OntoUMLClassType.SUBKIND.getText(),
            OntoUMLClassType.CATEGORY.getText(),
            OntoUMLClassType.COLLECTIVE.getText(),
            OntoUMLClassType.QUANTITY.getText(),
            OntoUMLClassType.ROLE.getText(),
            OntoUMLClassType.PHASE.getText(),
            OntoUMLClassType.MIXIN.getText(),
            OntoUMLClassType.MODE.getText(),
            OntoUMLClassType.ROLEMIXIN.getText(),
            OntoUMLClassType.RELATOR.getText(),
            OntoUMLClassType.NON_PERCEIVABLE_QUALITY.getText(),
            OntoUMLClassType.PERCEIVABLE_QUALITY.getText(),
            OntoUMLClassType.NOMINAL_QUALITY.getText(),
            OntoUMLClassType.DATA_TYPE.getText()
    };

    public static final String[] ASSOCIATION_STEREOTYPES = {
            OntoUMLRelationshipType.FORMAL.getText(),
            OntoUMLRelationshipType.MEDIATION.getText(),
            OntoUMLRelationshipType.MATERIAL.getText(),
            OntoUMLRelationshipType.DERIVATION.getText(),
            OntoUMLRelationshipType.CHARACTERIZATION.getText()
    };

    public static final String[] MERONYMIC_ASSOCIATIONS_TAGGED_VALUES = {
            "shareable",
            "essential",
            "inseparable",
            "immutablePart",
            "immutableWhole"
    };
}
