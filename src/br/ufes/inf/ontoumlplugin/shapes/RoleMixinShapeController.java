package br.ufes.inf.ontoumlplugin.shapes;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;

public class RoleMixinShapeController extends BaseShapeModelController {

    public RoleMixinShapeController() {
        this.classBaseName = "RoleMixinClass";
        this.stereotypeName = OntoUMLClassType.ROLEMIXIN.getText();
    }
}
