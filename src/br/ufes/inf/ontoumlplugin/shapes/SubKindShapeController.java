package br.ufes.inf.ontoumlplugin.shapes;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;

public class SubKindShapeController extends BaseShapeModelController {

    public SubKindShapeController() {
        this.classBaseName = "SubKindClass";
        this.stereotypeName = OntoUMLClassType.SUBKIND.getText();
    }
}
