package br.ufes.inf.ontoumlplugin.shapes;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;

import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.*;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

public class KindShapeController implements VPShapeModelCreationController {

    @Override
    public String getShapeType() {
        return IClassDiagramUIModel.SHAPETYPE_CLASS;
    }

    @Override
    public void shapeCreated(IShapeUIModel iShapeUIModel) {
        IProject project = ApplicationManager.instance().getProjectManager().getProject();
        IDiagramUIModel lDiagram = ApplicationManager.instance().getDiagramManager().getActiveDiagram();

        // create model
        IClass lClass = createKindClass(project, lDiagram);

        iShapeUIModel.setModelElement(lClass);
    }

    private IClass createKindClass(IProject project, IDiagramUIModel diagram) {
        IClass lClass; // class will be created in this action
        IModelElement lOwner = diagram.getParentModel();
        if (lOwner != null) {
            // create Class in Owner
            lClass = (IClass) lOwner.createChild(IModelElementFactory.MODEL_TYPE_CLASS);
        }
        else {
            // create Class in Root
            lClass = IModelElementFactory.instance().createClass();
        }
        lClass.addStereotype(OntoUMLClassType.getStereotypeFromString(project, "Kind"));
        lClass.setNickname("KindClass");
        return lClass;
    }
}
