package br.ufes.inf.ontoumlplugin.shapes;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;

public class DataTypeShapeController extends BaseShapeModelController{

    public DataTypeShapeController() {
        this.classBaseName = "DataTypeClass";
        this.stereotypeName = OntoUMLClassType.DATA_TYPE.getText();
    }
}
