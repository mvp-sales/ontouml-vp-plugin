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

        ITaggedValueDefinitionContainer taggedValueDefinitionContainer;

        ITaggedValueDefinition taggedValueDefinition;


        // Class Stereotypes
        IStereotype stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("Kind");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("Subkind");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("Role");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("Phase");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("Category");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("RoleMixin");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("Mixin");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("Relator");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("Mode");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("Quality");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("Collective");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("Quantity");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_CLASS);


        // Relation Stereotypes
        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("Formal");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("Mediation");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("Material");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("Derivation");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("Characterization");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("ComponentOf");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
        taggedValueDefinitionContainer = IModelElementFactory.instance().createTaggedValueDefinitionContainer();
        taggedValueDefinition = taggedValueDefinitionContainer.createTaggedValueDefinition();
        taggedValueDefinition.setType(ITaggedValueDefinition.TYPE_BOOLEAN);
        taggedValueDefinition.setName("shareable");
        stereotype.setTaggedValueDefinitions(taggedValueDefinitionContainer);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("SubCollectionOf");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
        taggedValueDefinitionContainer = IModelElementFactory.instance().createTaggedValueDefinitionContainer();
        taggedValueDefinition = taggedValueDefinitionContainer.createTaggedValueDefinition();
        taggedValueDefinition.setType(ITaggedValueDefinition.TYPE_BOOLEAN);
        taggedValueDefinition.setName("essential");
        taggedValueDefinition = taggedValueDefinitionContainer.createTaggedValueDefinition();
        taggedValueDefinition.setType(ITaggedValueDefinition.TYPE_BOOLEAN);
        taggedValueDefinition.setName("shareable");
        stereotype.setTaggedValueDefinitions(taggedValueDefinitionContainer);

        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("MemberOf");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
        taggedValueDefinitionContainer = IModelElementFactory.instance().createTaggedValueDefinitionContainer();
        taggedValueDefinition = taggedValueDefinitionContainer.createTaggedValueDefinition();
        taggedValueDefinition.setType(ITaggedValueDefinition.TYPE_BOOLEAN);
        taggedValueDefinition.setName("essential");
        taggedValueDefinition = taggedValueDefinitionContainer.createTaggedValueDefinition();
        taggedValueDefinition.setType(ITaggedValueDefinition.TYPE_BOOLEAN);
        taggedValueDefinition.setName("shareable");
        stereotype.setTaggedValueDefinitions(taggedValueDefinitionContainer);


        stereotype = IModelElementFactory.instance().createStereotype();
        stereotype.setName("SubQuantityOf");
        stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
        taggedValueDefinitionContainer = IModelElementFactory.instance().createTaggedValueDefinitionContainer();
        taggedValueDefinition = taggedValueDefinitionContainer.createTaggedValueDefinition();
        taggedValueDefinition.setType(ITaggedValueDefinition.TYPE_BOOLEAN);
        taggedValueDefinition.setName("essential");
        stereotype.setTaggedValueDefinitions(taggedValueDefinitionContainer);


    }
}
