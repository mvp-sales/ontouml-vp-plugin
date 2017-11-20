package br.ufes.inf.ontoumlplugin.shapes;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.*;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

public class KindShapeController extends BaseShapeModelController {

    public KindShapeController() {
        this.classBaseName = "KindClass";
        this.stereotypeName = "Kind";
    }
}
