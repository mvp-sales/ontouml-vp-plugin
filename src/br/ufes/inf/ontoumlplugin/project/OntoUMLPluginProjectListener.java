package br.ufes.inf.ontoumlplugin.project;

import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;

/**
 * Created by mvp-sales on 15/06/17.
 */
public class OntoUMLPluginProjectListener implements IProjectListener {

    @Override
    public void projectNewed(IProject iProject) {
        addOntoUMLStereotypes();
    }

    @Override
    public void projectOpened(IProject iProject) {

    }

    @Override
    public void projectAfterOpened(IProject iProject) {

    }

    @Override
    public void projectPreSave(IProject iProject) {

    }

    @Override
    public void projectSaved(IProject iProject) {

    }

    @Override
    public void projectRenamed(IProject iProject) {

    }

    private void addOntoUMLStereotypes(){

        // Class Stereotypes
        addClassStereotypes();
        // Relation Stereotypes
        addNonPartWholeAssociationStereotypes();
        // PartWhole Relation Stereotypes
        addPartWholeStereotypes();

    }

    private void addClassStereotypes(){
        String classTypes[] = {"Kind", "Subkind", "Role", "Phase", "Category", "RoleMixin",
                                "Mixin", "Relator", "Mode", "Quality", "Collective", "Quantity",
                                "DataType", "PerceivableQuality", "NonPerceivableQuality","NominalQuality",
                                "MeasurementDomain", "DecimalIntervalDimension", "DecimalOrdinalDimension",
                                "DecimalRationalDimension", "IntegerIntervalDimension", "IntegerOrdinalDimension",
                                "IntegerRationalDimension", "StringNominalStructure", "Enumeration"};

        for(String classType : classTypes){
            IStereotype stereotype = IModelElementFactory.instance().createStereotype();
            stereotype.setName(classType);
            stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);
        }

    }

    private void addNonPartWholeAssociationStereotypes(){
        String associationTypes[] = new String[] {"Formal", "Mediation","Material",
                                                    "Derivation","Characterization",
                                                    "Structuration"};

        for(String associationType : associationTypes){
            IStereotype stereotype = IModelElementFactory.instance().createStereotype();
            stereotype.setName(associationType);
            stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
        }

    }

    private void addPartWholeStereotypes() {

        addPartWholeStereotype("ComponentOf", "shareable");
        addPartWholeStereotype("MemberOf", "essential", "shareable");
        addPartWholeStereotype("SubCollectionOf", "essential", "shareable");
        addPartWholeStereotype("SubQuantityOf", "essential");

    }

    private void addPartWholeStereotype(String name, String... taggedValues){
        IStereotype stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName(name);
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

        ITaggedValueDefinitionContainer taggedValueDefinitionContainer =
                IModelElementFactory.instance().createTaggedValueDefinitionContainer();

        for(String taggedValue : taggedValues){
            ITaggedValueDefinition taggedValueDefinition =
                    taggedValueDefinitionContainer.createTaggedValueDefinition();
            taggedValueDefinition.setType(ITaggedValueDefinition.TYPE_BOOLEAN);
            taggedValueDefinition.setName(taggedValue);
        }

        stereotype.setTaggedValueDefinitions(taggedValueDefinitionContainer);
    }
}
