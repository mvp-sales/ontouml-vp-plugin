package br.ufes.inf.ontoumlplugin.shapes;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;

public class NominalQualityShapeController extends BaseShapeModelController {

    public NominalQualityShapeController() {
        this.classBaseName = "NominalQualityClass";
        this.stereotypeName = OntoUMLClassType.NOMINAL_QUALITY.getText();
    }
}
