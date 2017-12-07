package br.ufes.inf.ontoumlplugin.shapes;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;

public class PhaseShapeController extends BaseShapeModelController {

    public PhaseShapeController() {
        this.classBaseName = "PhaseClass";
        this.stereotypeName = OntoUMLClassType.PHASE.getText();
    }
}
