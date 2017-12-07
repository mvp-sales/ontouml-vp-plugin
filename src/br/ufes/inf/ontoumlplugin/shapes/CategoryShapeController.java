package br.ufes.inf.ontoumlplugin.shapes;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;

public class CategoryShapeController extends BaseShapeModelController {

    public CategoryShapeController() {
        this.classBaseName = "CategoryClass";
        this.stereotypeName = OntoUMLClassType.CATEGORY.getText();
    }
}
