package br.ufes.inf.ontoumlplugin.shapes;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;

public class PerceivableQualityShapeController extends BaseShapeModelController {

    public PerceivableQualityShapeController() {
        this.classBaseName = "PerceivableQualityClass";
        this.stereotypeName = OntoUMLClassType.PERCEIVABLE_QUALITY.getText();
    }
}
