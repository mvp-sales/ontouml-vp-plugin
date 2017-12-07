package br.ufes.inf.ontoumlplugin.utils;

import RefOntoUML.Element;
import br.ufes.inf.ontoumlplugin.OntoUMLPlugin;
import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;
import br.ufes.inf.ontoumlplugin.model.OntoUMLRelationshipType;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommonUtils {

    private static ViewManager viewManager = ApplicationManager.instance().getViewManager();

    private static void highlightDiagramElement(String nameElement) {
        DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();

        for(IDiagramUIModel openedDiagram : diagramManager.getOpenedDiagrams()) {
            if(openedDiagram instanceof IClassDiagramUIModel) {
                for(IDiagramElement diagramElement : openedDiagram.toDiagramElementArray()) {
                    IModelElement modelElement = diagramElement.getMetaModelElement();
                    if(modelElement.getName() != null && modelElement.getName().equals(nameElement)) {
                        diagramManager.highlight(diagramElement);
                        return;
                    }
                }
            }
        }
    }

    public static void showModelErrors(String resultMessage, Map<Element, ArrayList<String>> erroredElements, ViewManager viewManager) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        JLabel resultLabel = new JLabel(resultMessage);
        container.add(resultLabel);
        for(Element elem: erroredElements.keySet()){
            JPanel box = new JPanel();
            box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
            String elementName = elem.toString().replaceAll("«.*»", "").trim();
            if(elementName.isEmpty()) {
                JLabel label = new JLabel(elem.toString());
                box.add(label);
            }else {
                JButton button = new JButton(elem.toString());
                button.addActionListener(
                        event -> CommonUtils.highlightDiagramElement(button.getText().replaceAll("«.*»", "").trim())
                );
                box.add(button);
            }

            for(String message: erroredElements.get(elem)){
                JLabel label = new JLabel(message);
                box.add(label);
            }
            box.setBorder(BorderFactory.createEmptyBorder(8,4,8,4));
            box.doLayout();
            container.add(box);
        }
        container.doLayout();
        JScrollPane containerFather = new JScrollPane(container);
        viewManager.showMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID, "Error log", containerFather);
    }

    public static void addOntoUMLStereotypes(IProject project){

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
        viewManager.showMessage("OntoUML stereotypes added successfully to project " + project.getName(), OntoUMLPlugin.PLUGIN_ID);
    }

    private static void addClassStereotypes(Map<String, IStereotype> stereotypes){
        String classTypes[] = AppConstants.CLASS_STEREOTYPES;

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

                if (classType.equalsIgnoreCase(OntoUMLClassType.COLLECTIVE.getText())) {
                    ITaggedValueDefinitionContainer taggedValueDefinitionContainer =
                            IModelElementFactory.instance().createTaggedValueDefinitionContainer();

                    ITaggedValueDefinition taggedValueDefinition =
                            taggedValueDefinitionContainer.createTaggedValueDefinition();
                    taggedValueDefinition.setType(ITaggedValueDefinition.TYPE_BOOLEAN);
                    taggedValueDefinition.setName(AppConstants.IS_EXTENSIONAL);
                    taggedValueDefinition.setDefaultValue("False");

                    stereotype.setTaggedValueDefinitions(taggedValueDefinitionContainer);
                }
            }
        }

    }

    private static void addNonPartWholeAssociationStereotypes(Map<String, IStereotype> stereotypes){
        String associationTypes[] = AppConstants.ASSOCIATION_STEREOTYPES;

        for(String associationType : associationTypes){
            if (stereotypes.containsKey(associationType)){
                IStereotype stereotype = stereotypes.get(associationType);
                if (!stereotype.getBaseType().equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION)){
                    viewManager.showMessage("Stereotype " + associationType + " already existent with base type " + stereotype.getBaseType(), OntoUMLPlugin.PLUGIN_ID);
                }
            }else {
                IStereotype stereotype = IModelElementFactory.instance().createStereotype();
                stereotype.setName(associationType);
                if (associationType.equalsIgnoreCase(OntoUMLRelationshipType.DERIVATION.getText())) {
                    stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS);
                }else {
                    stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
                }
            }
        }

    }

    private static void addPartWholeStereotypes(Map<String, IStereotype> stereotypes) {

        addPartWholeStereotype(stereotypes, OntoUMLRelationshipType.COMPONENT_OF.getText(), AppConstants.MERONYMIC_ASSOCIATIONS_TAGGED_VALUES);
        addPartWholeStereotype(stereotypes, OntoUMLRelationshipType.MEMBER_OF.getText(), AppConstants.MERONYMIC_ASSOCIATIONS_TAGGED_VALUES);
        addPartWholeStereotype(stereotypes, OntoUMLRelationshipType.SUBCOLLECTION_OF.getText(), AppConstants.MERONYMIC_ASSOCIATIONS_TAGGED_VALUES);
        addPartWholeStereotype(stereotypes, OntoUMLRelationshipType.SUBQUANTITY_OF.getText(), AppConstants.MERONYMIC_ASSOCIATIONS_TAGGED_VALUES);

    }

    private static void addPartWholeStereotype(Map<String, IStereotype> stereotypes, String name, String... taggedValues){
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
