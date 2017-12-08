package br.ufes.inf.ontoumlplugin.model;

import RefOntoUML.Association;
import RefOntoUML.Classifier;
import RefOntoUML.Derivation;
import RefOntoUML.Package;
import RefOntoUML.util.RefOntoUMLFactoryUtil;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;

import java.util.HashMap;
import java.util.Map;

public class Vp2OntoUmlConverter {

    private Map<IModelElement, Classifier> classifierElements;
    private Map<IModelElement, RefOntoUML.Package> modelPackages;
    private Package rootPackage;
    private IProject vpProject;

    public Vp2OntoUmlConverter(IProject project) {
        this.classifierElements = new HashMap<>();
        this.modelPackages = new HashMap<>();
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

    public Package transform(IProject entry) {
        addPackages();

        return null;
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
        for(IModelElement classElement : vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_CLASS))
        {
            IClass vpClass = (IClass) classElement;
            String vpStereotype = vpClass.toStereotypeArray().length != 0?
                    vpClass.toStereotypeArray()[0] :
                    OntoUMLClassType.SUBKIND.getText();

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

            RefOntoUML.Classifier source = this.classifierElements.get(vpAssociation.getFrom()),
                    target = this.classifierElements.get(vpAssociation.getTo());

            RefOntoUML.Package containerPackage = vpAssociation.getParent() == null || modelPackages.get(vpAssociation.getParent()) == null ? rootPackage : modelPackages.get(vpAssociation.getParent());

            Association association = RefOntoUMLFactory.createOntoUMLAssociation(wrapper, vpAssociation, vpStereotype);
            this.classifierElements.put(vpAssociation, association);
        }

        for(IModelElement associationElement :
                vpProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS))
        {
            IAssociationClass vpAssociationClass = (IAssociationClass) associationElement;
            Derivation derivation = RefOntoUMLFactory.createOntoUMLDerivation(wrapper, vpAssociationClass);
            this.classifierElements.put(vpAssociationClass, derivation);
        }
    }


}
