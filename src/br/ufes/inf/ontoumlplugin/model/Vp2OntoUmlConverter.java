package br.ufes.inf.ontoumlplugin.model;

import RefOntoUML.*;
import RefOntoUML.Package;
import RefOntoUML.util.RefOntoUMLFactoryUtil;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;

import java.util.*;

public class Vp2OntoUmlConverter {

    private Map<IModelElement, Classifier> classifierElements;
    private Map<IModelElement, RefOntoUML.Package> modelPackages;
    private Map<IGeneralization, Generalization> generalizationElements;
    private Package rootPackage;
    private IProject vpProject;

    public Vp2OntoUmlConverter(IProject project) {
        this.classifierElements = new HashMap<>();
        this.modelPackages = new HashMap<>();
        this.generalizationElements = new HashMap<>();
        this.vpProject = project;
    }

    public RefOntoUML.Classifier getClassifierElement(String name){
        for(Map.Entry<IModelElement, RefOntoUML.Classifier> entry : this.classifierElements.entrySet()){
            IModelElement elem = entry.getKey();
            if(elem.getName().equals(name)){
                return entry.getValue();
            }
        }
        return null;
    }

    public Package transform() {
        addPackages();
        addClasses();
        addAssociations();
        addGeneralizations();
        addGeneralizationSets();
        addComments();

        return rootPackage;
    }

    private void addPackages() {
        for(IModelElement packageElement :
                vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_PACKAGE))
        {
            IPackage vpPackage = (IPackage) packageElement;
            RefOntoUML.Package ontoUmlPackage = getOrCreatePackage(vpPackage);
            if (vpPackage.getParent() != null){
                IPackage vpParentPackage = (IPackage) vpPackage.getParent();
                Package parentPackage = getOrCreatePackage(vpParentPackage);
                parentPackage.getPackagedElement().add(ontoUmlPackage);
            } else {
                rootPackage = ontoUmlPackage;
            }
            modelPackages.put(vpPackage, ontoUmlPackage);
        }

        if (rootPackage == null) {
            rootPackage = RefOntoUMLFactoryUtil.createPackage(vpProject.getName());
        }
    }

    private Package getOrCreatePackage(IPackage vpPackage) {
        Package ontoUmlPackage;
        if (!this.modelPackages.containsKey(vpPackage)){
            ontoUmlPackage = RefOntoUMLFactoryUtil.createPackage(vpPackage.getName());
            ontoUmlPackage.setName(vpPackage.getName());
            this.modelPackages.put(vpPackage, ontoUmlPackage);
        }else{
            ontoUmlPackage = this.modelPackages.get(vpPackage);
        }
        return ontoUmlPackage;
    }

    private void addClasses(){
        IModelElement[] classElements = vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_CLASS);
        for(IModelElement classElement : classElements)
        {
            IClass vpClass = (IClass) classElement;
            String vpStereotype = vpClass.toStereotypeArray().length != 0?
                    vpClass.toStereotypeArray()[0] :
                    OntoUMLClassType.SUBKIND.getText();

            if (!vpStereotype.equalsIgnoreCase(OntoUMLClassType.PRIMITIVE_TYPE.getText())) continue;

            RefOntoUML.Package containerPackage = vpClass.getParent() == null || modelPackages.get(vpClass.getParent()) == null ? rootPackage : modelPackages.get(vpClass.getParent());
            RefOntoUML.Classifier ontoUmlClass = RefOntoUMLFactory.createOntoUmlClass(containerPackage, vpClass, vpStereotype);
            ontoUmlClass = addOntoUMLAttributes(ontoUmlClass, vpClass);
            this.classifierElements.put(vpClass, ontoUmlClass);
        }

        for(IModelElement classElement : classElements)
        {
            IClass vpClass = (IClass) classElement;
            String vpStereotype = vpClass.toStereotypeArray().length != 0?
                    vpClass.toStereotypeArray()[0] :
                    OntoUMLClassType.SUBKIND.getText();

            if (!vpStereotype.equalsIgnoreCase(OntoUMLClassType.DATA_TYPE.getText())) continue;

            RefOntoUML.Package containerPackage = vpClass.getParent() == null || modelPackages.get(vpClass.getParent()) == null ? rootPackage : modelPackages.get(vpClass.getParent());
            RefOntoUML.Classifier ontoUmlClass = RefOntoUMLFactory.createOntoUmlClass(containerPackage, vpClass, vpStereotype);
            ontoUmlClass = addOntoUMLAttributes(ontoUmlClass, vpClass);
            this.classifierElements.put(vpClass, ontoUmlClass);
        }

        for(IModelElement classElement : classElements)
        {
            IClass vpClass = (IClass) classElement;
            String vpStereotype = vpClass.toStereotypeArray().length != 0?
                    vpClass.toStereotypeArray()[0] :
                    OntoUMLClassType.SUBKIND.getText();

            if (vpStereotype.equalsIgnoreCase(OntoUMLClassType.PRIMITIVE_TYPE.getText()) ||
                    vpStereotype.equalsIgnoreCase(OntoUMLClassType.DATA_TYPE.getText())) continue;

            RefOntoUML.Package containerPackage = vpClass.getParent() == null || modelPackages.get(vpClass.getParent()) == null ? rootPackage : modelPackages.get(vpClass.getParent());
            RefOntoUML.Classifier ontoUmlClass = RefOntoUMLFactory.createOntoUmlClass(containerPackage, vpClass, vpStereotype);
            ontoUmlClass = addOntoUMLAttributes(ontoUmlClass, vpClass);
            this.classifierElements.put(vpClass, ontoUmlClass);
        }
    }

    private RefOntoUML.Classifier addOntoUMLAttributes(RefOntoUML.Classifier classifier, IClass vpClass)
    {
        for(IAttribute attribute : vpClass.toAttributeArray()){
            String className = attribute.getTypeAsString();
            RefOntoUML.Classifier attributeClassifier = getClassifierElement(className);
            AssociationMultiplicity multiplicity = new AssociationMultiplicity(attribute.getMultiplicity());
            RefOntoUMLFactoryUtil.createAttribute(
                    classifier,
                    attributeClassifier,
                    multiplicity.getMinMultiplicity(),
                    multiplicity.getMaxMultiplicity(),
                    attribute.getName(),
                    false
            );
        }

        return classifier;
    }

    private void addAssociations() {
        for(IModelElement associationElement :
                vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_ASSOCIATION))
        {
            IAssociation vpAssociation = (IAssociation) associationElement;
            String vpStereotype = vpAssociation.toStereotypeArray().length > 0 ?
                    vpAssociation.toStereotypeArray()[0] : "";

            OntoUMLRelationshipType relationType = OntoUMLRelationshipType.fromString(vpStereotype);
            RefOntoUML.Package containerPackage =
                    vpAssociation.getParent() == null || modelPackages.get(vpAssociation.getParent()) == null ?
                    rootPackage : modelPackages.get(vpAssociation.getParent());
            Association ontoUmlAssociation;
            switch(relationType) {
                default:
                case COMMON_ASSOCIATION:
                case CHARACTERIZATION:
                case MEDIATION:
                case FORMAL:
                case MATERIAL:
                    RefOntoUML.Classifier source = this.classifierElements.get(vpAssociation.getFrom()),
                            target = this.classifierElements.get(vpAssociation.getTo());
                    ontoUmlAssociation = RefOntoUMLFactory.createCommonAssociation(source, target, containerPackage, vpAssociation, relationType);
                    break;
                case COMPONENT_OF:
                case MEMBER_OF:
                case SUBQUANTITY_OF:
                case SUBCOLLECTION_OF:
                    IAssociationEnd assEndFrom = (IAssociationEnd) vpAssociation.getFromEnd();
                    String aggregationKind = assEndFrom.getAggregationKind();

                    Classifier whole, part;

                    if(aggregationKind.equals(IAssociationEnd.AGGREGATION_KIND_COMPOSITED) ||
                            aggregationKind.equals(IAssociationEnd.AGGREGATION_KIND_SHARED)){
                        whole = this.classifierElements.get(vpAssociation.getFrom());
                        part = this.classifierElements.get(vpAssociation.getTo());
                    }else{
                        aggregationKind = ((IAssociationEnd)vpAssociation.getToEnd()).getAggregationKind();
                        whole = this.classifierElements.get(vpAssociation.getTo());
                        part = this.classifierElements.get(vpAssociation.getFrom());
                    }

                    ontoUmlAssociation = RefOntoUMLFactory.createMeronymicAssociation(whole, part, containerPackage, vpAssociation, aggregationKind, relationType);
                    break;

            }

            this.classifierElements.put(vpAssociation, ontoUmlAssociation);
        }

        for(IModelElement associationElement :
                vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS))
        {
            IAssociationClass vpAssClass = (IAssociationClass) associationElement;
            RefOntoUML.Classifier relator, material;
            if (vpAssClass.getFrom() instanceof IClass) {
                relator = this.classifierElements.get(vpAssClass.getFrom());
                material = this.classifierElements.get(vpAssClass.getTo());
            }else {
                relator = this.classifierElements.get(vpAssClass.getTo());
                material = this.classifierElements.get(vpAssClass.getFrom());
            }

            RefOntoUML.Package containerPackage = vpAssClass.getParent() == null || !this.modelPackages.containsKey(vpAssClass.getParent()) ?
                    rootPackage :
                    this.modelPackages.get(vpAssClass.getParent());

            Derivation derivation = RefOntoUMLFactory.createOntoUMLDerivation(relator, material, containerPackage, vpAssClass);
            this.classifierElements.put(vpAssClass, derivation);
        }
    }

    private void addGeneralizations() {
        for(IModelElement generalizationElement :
                vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_GENERALIZATION))
        {
            IGeneralization vpGeneralization = (IGeneralization) generalizationElement;
            RefOntoUML.Classifier parent = this.classifierElements.get(vpGeneralization.getFrom()),
                    child = this.classifierElements.get(vpGeneralization.getTo());
            Generalization ontoUmlGen = RefOntoUMLFactoryUtil.createGeneralization(child, parent);
            this.generalizationElements.put(vpGeneralization, ontoUmlGen);
        }
    }

    private void addGeneralizationSets() {
        for(IModelElement genSetElement :
                vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET))
        {
            IGeneralizationSet vpGenSet = (IGeneralizationSet) genSetElement;
            Iterator genIterator = vpGenSet.generalizationIterator();
            List<Generalization> genSetList = new ArrayList<>();
            while(genIterator.hasNext()){
                IGeneralization gen = (IGeneralization) genIterator.next();
                RefOntoUML.Generalization ontoUmlGen =
                        this.generalizationElements.get(gen);

                genSetList.add(ontoUmlGen);
            }

            RefOntoUML.Package containerPackage = vpGenSet.getParent() == null || !this.modelPackages.containsKey(vpGenSet.getParent()) ?
                    rootPackage :
                    this.modelPackages.get(vpGenSet.getParent());

            RefOntoUMLFactoryUtil.createGeneralizationSet
            (
                    genSetList,
                    vpGenSet.getName(),
                    vpGenSet.isDisjoint(),
                    vpGenSet.isCovering(),
                    containerPackage
            );
        }
    }

    private void addComments() {
        /*for (IModelElement element : vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_ANCHOR)) {
            IAnchor anchor = (IAnchor) element;
            if (anchor.getTo() instanceof INOTE) {
                INOTE note = (INOTE) anchor.getTo();
                Classifier classifier = classifierElements.get(anchor.getFrom());
                RefOntoUMLFactoryUtil.createComment(note.toString(), classifier);
            } else {
                INOTE note = (INOTE) anchor.getFrom();
                Classifier classifier = classifierElements.get(anchor.getTo());
                IComment[] ccc = note.toCommentArray();
                RefOntoUMLFactoryUtil.createComment(note.toString(), classifier);
            }
        }*/
    }


}
