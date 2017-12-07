package br.ufes.inf.ontoumlplugin.shapes;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;

public class RelatorShapeController extends BaseShapeModelController {

    public RelatorShapeController() {
        this.classBaseName = "RelatorClass";
        this.stereotypeName = OntoUMLClassType.RELATOR.getText();
    }
}
