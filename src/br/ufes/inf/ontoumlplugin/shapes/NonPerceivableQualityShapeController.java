package br.ufes.inf.ontoumlplugin.shapes;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;

public class NonPerceivableQualityShapeController extends BaseShapeModelController {

    public NonPerceivableQualityShapeController() {
        this.classBaseName = "NonPerceivableQualityClass";
        this.stereotypeName = OntoUMLClassType.NON_PERCEIVABLE_QUALITY.getText();
    }
}
