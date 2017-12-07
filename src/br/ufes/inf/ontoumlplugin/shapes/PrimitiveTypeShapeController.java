package br.ufes.inf.ontoumlplugin.shapes;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;

public class PrimitiveTypeShapeController extends BaseShapeModelController{

    public PrimitiveTypeShapeController() {
        this.classBaseName = "PrimitiveTypeClass";
        this.stereotypeName = OntoUMLClassType.PRIMITIVE_TYPE.getText();
    }
}
