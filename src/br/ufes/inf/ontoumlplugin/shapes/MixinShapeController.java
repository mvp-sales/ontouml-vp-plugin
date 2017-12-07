package br.ufes.inf.ontoumlplugin.shapes;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;

public class MixinShapeController extends BaseShapeModelController {

    public MixinShapeController() {
        this.classBaseName = "MixinClass";
        this.stereotypeName = OntoUMLClassType.MIXIN.getText();
    }
}
