package br.ufes.inf.ontoumlplugin.shapes;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;

public class QuantityShapeController extends BaseShapeModelController {

    public QuantityShapeController() {
        this.classBaseName = "QuantityClass";
        this.stereotypeName = OntoUMLClassType.QUANTITY.getText();
    }
}
