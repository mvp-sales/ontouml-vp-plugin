package br.ufes.inf.ontoumlplugin.shapes;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;

public class RoleShapeController extends BaseShapeModelController {

    public RoleShapeController() {
        this.classBaseName = "RoleClass";
        this.stereotypeName = OntoUMLClassType.ROLE.getText();
    }
}
