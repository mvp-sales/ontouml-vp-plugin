package br.ufes.inf.ontoumlplugin.shapes;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;

public class CollectiveShapeController extends BaseShapeModelController {

    public CollectiveShapeController() {
        this.classBaseName = "CollectiveClass";
        this.stereotypeName = OntoUMLClassType.COLLECTIVE.getText();
    }
}
