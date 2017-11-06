package br.ufes.inf.ontoumlplugin.project;

import br.ufes.inf.ontoumlplugin.OntoUMLPlugin;
import br.ufes.inf.ontoumlplugin.actions.ValidateOntoUMLModelController;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mvp-sales on 15/06/17.
 */
public class OntoUMLPluginProjectListener implements IProjectListener {

    private final ViewManager viewManager = ApplicationManager.instance().getViewManager();
    private int stateProjectListener = UNSPECIFIED;
    private static final int UNSPECIFIED = 0;
    private static final int NEWED = 1;
    private static final int PRESAVE = 2;
    private static final int SAVED = 3;
    private static final int RENAMED = 4;
    private static final int OPENED = 5;
    private static final int AFTER_OPENED = 6;

    @Override
    public void projectNewed(IProject iProject) {
    	stateProjectListener = NEWED;
    }

    @Override
    public void projectOpened(IProject iProject) {
    	stateProjectListener = OPENED;
    }

    @Override
    public void projectAfterOpened(IProject iProject) {
    	stateProjectListener = AFTER_OPENED;
    }

    @Override
    public void projectPreSave(IProject iProject) {
    	stateProjectListener = PRESAVE;
    }

    @Override
    public void projectSaved(IProject iProject) {
    	stateProjectListener = SAVED;
    	ValidateOntoUMLModelController.validateModel();
    }

    @Override
    public void projectRenamed(IProject iProject) {
    	if(stateProjectListener == NEWED) {
    		addOntoUMLStereotypes(iProject);
    	}
    	stateProjectListener = RENAMED;
    }

    private void addOntoUMLStereotypes(IProject project){

        Map<String, IStereotype> stereotypeMap = new HashMap<>();

        for(IModelElement element : project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_STEREOTYPE))
        {
            IStereotype stereotype = (IStereotype) element;
            stereotypeMap.put(stereotype.getName(), stereotype);
        }
        // Class Stereotypes
        addClassStereotypes(stereotypeMap);
        // Relation Stereotypes
        addNonPartWholeAssociationStereotypes(stereotypeMap);
        // PartWhole Relation Stereotypes
        addPartWholeStereotypes(stereotypeMap);
        viewManager.showMessage("OntoUML Stereotypes loaded successfully", OntoUMLPlugin.PLUGIN_ID);
    }

    private void addClassStereotypes(Map<String, IStereotype> stereotypes){
        String classTypes[] = {"Kind", "Subkind", "Role", "Phase", "Category", "RoleMixin",
                                "Mixin", "Relator", "Mode", "Quality", "Collective", "Quantity",
                                "DataType", "PerceivableQuality", "NonPerceivableQuality","NominalQuality",/*
                                "MeasurementDomain", "DecimalIntervalDimension", "DecimalOrdinalDimension",
                                "DecimalRationalDimension", "IntegerIntervalDimension", "IntegerOrdinalDimension",
                                "IntegerRationalDimension", "StringNominalStructure", "Enumeration",*/ "PrimitiveType"};

        for(String classType : classTypes){
            if(stereotypes.containsKey(classType)){
                IStereotype stereotype = stereotypes.get(classType);
                if (!stereotype.getBaseType().equals(IModelElementFactory.MODEL_TYPE_CLASS)){
                    viewManager.showMessage("Stereotype " + classType + " already existent with base type " + stereotype.getBaseType(), OntoUMLPlugin.PLUGIN_ID);
                }
            }else {
                IStereotype stereotype = IModelElementFactory.instance().createStereotype();
                stereotype.setName(classType);
                stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);
            }
        }

    }

    private void addNonPartWholeAssociationStereotypes(Map<String, IStereotype> stereotypes){
        String associationTypes[] = new String[] {"Formal", "Mediation","Material",
                                                    "Derivation","Characterization",
                                                    "Structuration"};

        for(String associationType : associationTypes){
            if (stereotypes.containsKey(associationType)){
                IStereotype stereotype = stereotypes.get(associationType);
                if (!stereotype.getBaseType().equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION)){
                    viewManager.showMessage("Stereotype " + associationType + " already existent with base type " + stereotype.getBaseType(), OntoUMLPlugin.PLUGIN_ID);
                }
            }else {
                IStereotype stereotype = IModelElementFactory.instance().createStereotype();
                stereotype.setName(associationType);
                stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
            }
        }

    }

    private void addPartWholeStereotypes(Map<String, IStereotype> stereotypes) {

        addPartWholeStereotype(stereotypes, "ComponentOf", "shareable", "essential", "inseparable", "immutableWhole", "immutablePart");
        addPartWholeStereotype(stereotypes, "MemberOf", "shareable", "essential", "inseparable", "immutableWhole", "immutablePart");
        addPartWholeStereotype(stereotypes, "SubCollectionOf", "shareable", "essential", "inseparable", "immutableWhole", "immutablePart");
        addPartWholeStereotype(stereotypes, "SubQuantityOf", "shareable", "essential", "inseparable", "immutableWhole", "immutablePart");

    }

    private void addPartWholeStereotype(Map<String, IStereotype> stereotypes, String name, String... taggedValues){
        if (stereotypes.containsKey(name)){
            return;
        }

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
