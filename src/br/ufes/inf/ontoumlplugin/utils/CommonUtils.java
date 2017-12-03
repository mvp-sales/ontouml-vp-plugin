package br.ufes.inf.ontoumlplugin.utils;

import br.ufes.inf.ontoumlplugin.OntoUMLPlugin;
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

    public static void highlightDiagramElement(String nameElement) {
        DiagramManager diagramManager = ApplicationManager.instance().getDiagramManager();

        for(IDiagramUIModel openedDiagram : diagramManager.getOpenedDiagrams()) {
            if(openedDiagram instanceof IClassDiagramUIModel) {
                for(IDiagramElement diagramElement : openedDiagram.toDiagramElementArray()) {
                    if(diagramElement.getModelElement().getName().equals(nameElement)) {
                        diagramManager.highlight(diagramElement);
                        return;
                    }
                }
            }
        }
    }

    public static void showModelErrors(String resultMessage, Map<String, ArrayList<String>> erroredElements, ViewManager viewManager) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        JLabel resultLabel = new JLabel(resultMessage);
        container.add(resultLabel);
        for(String elem: erroredElements.keySet()){
            JPanel box = new JPanel();
            box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
            String elementName = elem.replaceAll("«.*»", "").trim();
            if(elementName.isEmpty()) {
                JLabel label = new JLabel(elem);
                box.add(label);
            }else {
                JButton button = new JButton(elem);
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
        viewManager.showMessage("OntoUML Stereotypes loaded successfully", OntoUMLPlugin.PLUGIN_ID);
    }

    private static void addClassStereotypes(Map<String, IStereotype> stereotypes){
        String classTypes[] = {"Kind", "Subkind", "Role", "Phase", "Category", "RoleMixin",
                "Mixin", "Relator", "Mode", "Quality", "Collective", "Quantity",
                "DataType", "PerceivableQuality", "NonPerceivableQuality","NominalQuality"};

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

                if (classType.equalsIgnoreCase("collective")) {
                    ITaggedValueDefinitionContainer taggedValueDefinitionContainer =
                            IModelElementFactory.instance().createTaggedValueDefinitionContainer();

                    ITaggedValueDefinition taggedValueDefinition =
                            taggedValueDefinitionContainer.createTaggedValueDefinition();
                    taggedValueDefinition.setType(ITaggedValueDefinition.TYPE_BOOLEAN);
                    taggedValueDefinition.setName("isExtensional");
                    taggedValueDefinition.setDefaultValue("False");

                    stereotype.setTaggedValueDefinitions(taggedValueDefinitionContainer);
                }
            }
        }

    }

    private static void addNonPartWholeAssociationStereotypes(Map<String, IStereotype> stereotypes){
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
                if (associationType.equalsIgnoreCase("derivation")) {
                    stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS);
                }else {
                    stereotype.setBaseType(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
                }
            }
        }

    }

    private static void addPartWholeStereotypes(Map<String, IStereotype> stereotypes) {

        addPartWholeStereotype(stereotypes, "ComponentOf", "shareable", "essential", "inseparable", "immutableWhole", "immutablePart");
        addPartWholeStereotype(stereotypes, "MemberOf", "shareable", "essential", "inseparable", "immutableWhole", "immutablePart");
        addPartWholeStereotype(stereotypes, "SubCollectionOf", "shareable", "essential", "inseparable", "immutableWhole", "immutablePart");
        addPartWholeStereotype(stereotypes, "SubQuantityOf", "shareable", "essential", "inseparable", "immutableWhole", "immutablePart");

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
