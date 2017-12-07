package br.ufes.inf.ontoumlplugin.shapes;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;

public class ModeShapeController extends BaseShapeModelController{

    public ModeShapeController() {
        this.classBaseName = "ModeClass";
        this.stereotypeName = OntoUMLClassType.MODE.getText();
    }
}
