package br.ufes.inf.ontoumlplugin.model;

import RefOntoUML.*;
import RefOntoUML.Class;
import RefOntoUML.Package;
import RefOntoUML.parser.OntoUMLParser;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;

import java.util.HashMap;
import java.util.Map;

public class OntoUml2VpConverter {

    private Map<Package, IPackage> packageElements;
    private Map<RefOntoUML.Classifier, IModelElement> classifierElements;
    private Map<Generalization, IGeneralization> generalizationElements;
    private final IProject vpProject;

    public OntoUml2VpConverter(IProject vpProject) {
        this.packageElements = new HashMap<>();
        this.classifierElements = new HashMap<>();
        this.generalizationElements = new HashMap<>();
        this.vpProject = vpProject;
    }

    public IModelElement getClassifierElement(String name){
        for(Map.Entry<Classifier, IModelElement> entry : this.classifierElements.entrySet()){
            Classifier elem = entry.getKey();
            if(elem.getName().equals(name)){
                return entry.getValue();
            }
        }
        return null;
    }

    public void transform(Package ontoUmlRootPackage) {
        OntoUMLParser parser = new OntoUMLParser(ontoUmlRootPackage);
        addPackages(parser);
        addClasses(parser);
        addAttributes();
        addAssociations(parser);
        addGeneralizations(parser);
        addGeneralizationSets(parser);
        addComments(parser);
    }

    private void addPackages(OntoUMLParser parser) {
        for (Package modelPackage : parser.getAllInstances(Package.class)) {
            IPackage modelVpPackage = getPackage(modelPackage);

            if (modelPackage.eContainer() != null) {
                Package container = (Package) modelPackage.eContainer();
                IPackage containerPackage = getPackage(container);
                containerPackage.addChild(modelVpPackage);
            }
        }
    }

    private IPackage getPackage(Package ontoUmlPackage) {
        IPackage vpPackage;
        if (!this.packageElements.containsKey(ontoUmlPackage)){
            vpPackage = IModelElementFactory.instance().createPackage();
            vpPackage.setName(ontoUmlPackage.getName());
            this.packageElements.put(ontoUmlPackage, vpPackage);
        }else{
            vpPackage = this.packageElements.get(ontoUmlPackage);
        }
        return vpPackage;
    }

    private void addClasses(OntoUMLParser parser) {
        for (Class ontoUmlClass : parser.getAllInstances(Class.class)) {
            IPackage classPackage = this.packageElements.get((Package) ontoUmlClass.eContainer());
            createClass(ontoUmlClass, classPackage);
        }

        for (DataType ontoUmlClass : parser.getAllInstances(DataType.class)) {
            IPackage classPackage = this.packageElements.get((Package) ontoUmlClass.eContainer());
            createClass(ontoUmlClass, classPackage);
        }
    }

    private void createClass(Classifier c, IPackage vpPackage) {
        IClass vpClass = IModelElementFactory.instance().createClass();
        vpClass.setName(c.getName());
        vpClass = VPModelFactory.setClassStereotype(vpClass, c, this.vpProject);
        this.classifierElements.put(c, vpClass);
        vpPackage.addChild(vpClass);
    }

    private void addAttributes() {
        for (Map.Entry<Classifier, IModelElement> entry : this.classifierElements.entrySet()) {
            Classifier ontoUmlClassifier = entry.getKey();
            IClass vpClass = (IClass) entry.getValue();
            for(RefOntoUML.Property attribute : ontoUmlClassifier.getAttribute()){
                IAttribute vpAttribute = IModelElementFactory.instance().createAttribute();
                vpAttribute.setName(attribute.getName());
                IModelElement attr = getClassifierElement(attribute.getName());
                vpAttribute.setType(attr);
                AssociationMultiplicity multiplicity = new AssociationMultiplicity(attribute.getLower(), attribute.getUpper());
                vpAttribute.setMultiplicity(multiplicity.getMultiplicityString());
                vpClass.addAttribute(vpAttribute);
            }
        }
    }

    private void addAssociations(OntoUMLParser parser) {
        for(Classifier c : parser.getAssociations()){
            IPackage associationPackage = this.packageElements.get((Package) c.eContainer());
            RefOntoUML.Association association = (RefOntoUML.Association) c;
            if(association instanceof RefOntoUML.Meronymic){
                RefOntoUML.Meronymic meronymicAssociation = (RefOntoUML.Meronymic) association;
                createMeronymicAssociation(meronymicAssociation, associationPackage);
            }else if(!(association instanceof RefOntoUML.Derivation) ){
                createAssociation(association, associationPackage);
            }
        }

        for (Derivation d : parser.getAllInstances(Derivation.class)) {
            IPackage associationPackage = this.packageElements.get((Package) d.eContainer());
            createDerivation(d, associationPackage);
        }
    }
    private void createMeronymicAssociation(Meronymic association, IPackage vpPackage){
        RefOntoUML.Property wholeEnd = association.wholeEnd();
        RefOntoUML.Property partEnd = association.partEnd();

        RefOntoUML.Classifier whole = association.whole();
        RefOntoUML.Classifier part = association.part();
        int lowerPart = partEnd.getLower(), upperC1 = partEnd.getUpper();
        int lowerWhole = wholeEnd.getLower(), upperC2 = wholeEnd.getUpper();

        IModelElement wholeVp = this.classifierElements.get(whole),
                partVp = this.classifierElements.get(part);

        // create normal association between subclass to "ClassWithAssociation"
        IAssociation associationModel = IModelElementFactory.instance().createAssociation();
        associationModel.setName(association.getName());
        associationModel.setFrom(partVp);
        associationModel.setTo(wholeVp);
        // specify multiplicity for from & to end
        IAssociationEnd associationFromEnd = (IAssociationEnd) associationModel.getFromEnd();
        associationFromEnd.setMultiplicity(getMultiplicityFromValues(lowerPart, upperC1));
        IAssociationEnd associationToEnd = (IAssociationEnd) associationModel.getToEnd();
        associationToEnd.setMultiplicity(getMultiplicityFromValues(lowerWhole, upperC2));
        associationToEnd.setAggregationKind(
                association.isIsShareable() ?
                        IAssociationEnd.AGGREGATION_KIND_AGGREGATION : IAssociationEnd.AGGREGATION_KIND_COMPOSITED
        );

        associationModel = VPModelFactory.setMeronymicAssociation(associationModel, association, this.vpProject);
        this.classifierElements.put(association, associationModel);
        vpPackage.addChild(associationModel);
    }

    private void createAssociation(Association association, IPackage vpPackage){
        RefOntoUML.Property p1 = association.getOwnedEnd().get(0);
        RefOntoUML.Property p2 = association.getOwnedEnd().get(1);

        RefOntoUML.Classifier c1 = (RefOntoUML.Classifier) p1.getType();
        RefOntoUML.Classifier c2 = (RefOntoUML.Classifier) p2.getType();
        int lowerC1 = p1.getLower(), upperC1 = p1.getUpper();
        int lowerC2 = p2.getLower(), upperC2 = p2.getUpper();

        IModelElement from1 = this.classifierElements.get(c1),
                to1 = this.classifierElements.get(c2);

        // create normal association between subclass to "ClassWithAssociation"
        IAssociation associationModel = IModelElementFactory.instance().createAssociation();
        associationModel.setName(association.getName());
        associationModel.setFrom(from1);
        associationModel.setTo(to1);
        // specify multiplicity for from & to end
        IAssociationEnd associationFromEnd = (IAssociationEnd) associationModel.getFromEnd();
        associationFromEnd.setMultiplicity(getMultiplicityFromValues(lowerC1, upperC1));
        IAssociationEnd associationToEnd = (IAssociationEnd) associationModel.getToEnd();
        associationToEnd.setMultiplicity(getMultiplicityFromValues(lowerC2, upperC2));

        associationModel = VPModelFactory.setAssociationStereotype(associationModel, association, this.vpProject);
        this.classifierElements.put(association, associationModel);
        vpPackage.addChild(associationModel);
    }

    private String getMultiplicityFromValues(int lower, int upper){
        String result = (lower == -1 ? "*" : lower) + ".." + (upper == -1 ? "*" : upper);
        if(lower == 0){
            if(upper == 1){
                result = IAssociationEnd.MULTIPLICITY_ZERO_TO_ONE;
            }else if(upper == -1){
                result = IAssociationEnd.MULTIPLICITY_ZERO_TO_MANY;
            }
        }else if(lower == 1){
            if(upper == 1){
                result = IAssociationEnd.MULTIPLICITY_ONE;
            }else if(upper == -1){
                result = IAssociationEnd.MULTIPLICITY_ONE_TO_MANY;
            }
        }else if(lower == upper){
            result = lower == -1 ? "*" : Integer.toString(lower);
        }
        return result;
    }

    private void createDerivation(Derivation derivation, IPackage vpPackage) {
        IAssociationClass vpAssClass = IModelElementFactory.instance().createAssociationClass();
        vpAssClass.setFrom(this.classifierElements.get(derivation.relator()));
        vpAssClass.setTo(this.classifierElements.get(derivation.material()));
        vpAssClass.addStereotype(OntoUMLRelationshipType.DERIVATION.getText());
        vpPackage.addChild(vpAssClass);
    }

    private void addGeneralizations(OntoUMLParser parser) {
        for(Generalization ontoUmlGen : parser.getAllInstances(Generalization.class)){
            IGeneralization vpGen = IModelElementFactory.instance().createGeneralization();
            IModelElement specific = this.classifierElements.get(ontoUmlGen.getSpecific()),
                    general = this.classifierElements.get(ontoUmlGen.getGeneral());
            vpGen.setFrom(general);
            vpGen.setTo(specific);
            this.generalizationElements.put(ontoUmlGen, vpGen);
        }
    }

    private void addGeneralizationSets(OntoUMLParser parser) {
        for(RefOntoUML.GeneralizationSet genSet : parser.getAllInstances(RefOntoUML.GeneralizationSet.class)){
            IPackage genSetPackage = this.packageElements.get((Package) genSet.eContainer());
            createGeneralizationSet(genSet, genSetPackage);
        }
    }

    private void createGeneralizationSet(GeneralizationSet genSet, IPackage vpPackage){
        IGeneralizationSet vpGenSet = IModelElementFactory.instance().createGeneralizationSet();
        vpGenSet.setName(genSet.getName());
        vpGenSet.setDisjoint(genSet.isIsDisjoint()); vpGenSet.setCovering(genSet.isIsCovering());

        for(Generalization gen : genSet.getGeneralization()){
            vpGenSet.addGeneralization(this.generalizationElements.get(gen));
        }

        vpPackage.addChild(vpGenSet);
    }

    private void addComments(OntoUMLParser parser) {
        for (Element element : parser.getAllInstances(Element.class)) {
            for (Comment c : element.getOwnedComment()) {
                if (element instanceof Classifier) {
                    IModelElement modelElement = this.classifierElements.get((Classifier) element);
                    INOTE note = IModelElementFactory.instance().createNOTE();
                    note.setDescription(c.getBody());
                    IAnchor anchor = IModelElementFactory.instance().createAnchor();
                    anchor.setFrom(modelElement);
                    anchor.setTo(note);
                }
            }
        }
    }


}
