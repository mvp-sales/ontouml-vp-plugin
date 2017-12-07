package br.ufes.inf.ontoumlplugin.actions;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;
import br.ufes.inf.ontoumlplugin.model.OntoUMLRelationshipType;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;

public class FillEmptyAssociationsController implements VPActionController {

    @Override
    public void performAction(VPAction vpAction) {
        IProject currentProject = ApplicationManager.instance().getProjectManager().getProject();

        for (IModelElement me : currentProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_ASSOCIATION)) {
            IAssociation association = (IAssociation) me;
            if (association.toStereotypeArray().length == 0)
                addStereotypesToAssociation(association);
        }

        for (IModelElement me : currentProject.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS)) {
            IAssociationClass derivation = (IAssociationClass) me;
            if (derivation.getFrom() instanceof IAssociation) {
                derivation.getFrom().addStereotype(OntoUMLRelationshipType.MATERIAL_ASSOCIATION.getText());
            } else if (derivation.getTo() instanceof IAssociation) {
                derivation.getTo().addStereotype(OntoUMLRelationshipType.MATERIAL_ASSOCIATION.getText());
            }
            derivation.addStereotype(OntoUMLRelationshipType.DERIVATION.getText());
        }
    }

    @Override
    public void update(VPAction vpAction) {

    }

    private void addStereotypesToAssociation(IAssociation association) {
        IClass classFrom = (IClass) association.getFrom();
        IClass classTo = (IClass) association.getTo();

        String stereotypeFrom = classFrom.toStereotypeArray().length > 0 ?
                classFrom.toStereotypeArray()[0] : OntoUMLClassType.SUBKIND.getText();
        String stereotypeTo = classTo.toStereotypeArray().length > 0 ?
                classTo.toStereotypeArray()[0] : OntoUMLClassType.SUBKIND.getText();

        if (isCharacterization(stereotypeFrom, stereotypeTo)) {
            association.addStereotype(OntoUMLRelationshipType.CHARACTERIZATION.getText());
        } else if (isMediation(stereotypeFrom, stereotypeTo)) {
            association.addStereotype(OntoUMLRelationshipType.MEDIATION.getText());
        } else if (isSubCollectionOf(stereotypeFrom, stereotypeTo)) {
            association.addStereotype(OntoUMLRelationshipType.SUBCOLLECTION_OF.getText());
        } else if (isSubQuantityOf(stereotypeFrom, stereotypeTo)) {
            association.addStereotype(OntoUMLRelationshipType.SUBQUANTITY_OF.getText());
        }
    }

    private boolean isCharacterization(String stereotypeOne, String stereotypeTwo) {
        return stereotypeOne.equalsIgnoreCase(OntoUMLClassType.MODE.getText()) ||
                stereotypeTwo.equalsIgnoreCase(OntoUMLClassType.MODE.getText());
    }

    private boolean isMediation(String stereotypeOne, String stereotypeTwo) {
        return (stereotypeOne.equalsIgnoreCase(OntoUMLClassType.RELATOR.getText()) &&
                stereotypeTwo.equalsIgnoreCase(OntoUMLClassType.ROLE.getText())) ||
                (stereotypeOne.equalsIgnoreCase(OntoUMLClassType.ROLE.getText()) &&
                    stereotypeTwo.equalsIgnoreCase(OntoUMLClassType.RELATOR.getText()));
    }

    private boolean isSubCollectionOf(String stereotypeOne, String stereotypeTwo) {
        return stereotypeOne.equalsIgnoreCase(OntoUMLClassType.COLLECTIVE.getText()) &&
                stereotypeTwo.equalsIgnoreCase(OntoUMLClassType.COLLECTIVE.getText());
    }

    private boolean isSubQuantityOf(String stereotypeOne, String stereotypeTwo) {
        return stereotypeOne.equalsIgnoreCase(OntoUMLClassType.QUANTITY.getText()) &&
                stereotypeTwo.equalsIgnoreCase(OntoUMLClassType.QUANTITY.getText());
    }
}
