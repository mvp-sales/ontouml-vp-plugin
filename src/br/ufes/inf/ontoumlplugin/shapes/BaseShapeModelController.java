package br.ufes.inf.ontoumlplugin.shapes;

import RefOntoUML.parser.OntoUMLParser;
import br.ufes.inf.ontoumlplugin.model.OntoUMLClassType;
import br.ufes.inf.ontoumlplugin.utils.CommonUtils;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.diagram.VPShapeModelCreationController;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

public abstract class BaseShapeModelController implements VPShapeModelCreationController {

    protected String classBaseName;
    protected String stereotypeName;

    @Override
    public String getShapeType() {
        return IClassDiagramUIModel.SHAPETYPE_CLASS;
    }

    @Override
    public void shapeCreated(IShapeUIModel iShapeUIModel) {
        IProject project = ApplicationManager.instance().getProjectManager().getProject();
        IDiagramUIModel lDiagram = ApplicationManager.instance().getDiagramManager().getActiveDiagram();
        if (!OntoUMLClassType.doesProjectHaveStereotype(project, stereotypeName)) {
            CommonUtils.addOntoUMLStereotypes(project);
        }

        IClass classElement = (IClass) iShapeUIModel.getModelElement();
        createModelElement(classElement, project, lDiagram);
    }

    private IClass createModelElement(IClass lClass, IProject project, IDiagramUIModel diagramModel) {
        IModelElement[] lPeers;
        IModelElement lOwner = diagramModel.getParentModel();
        if (lOwner != null) {
            // create Class in Owner
            lPeers = lOwner.toChildArray(IModelElementFactory.MODEL_TYPE_CLASS);
        }
        else {
            // create Class in Root
            lPeers = ApplicationManager.instance().getProjectManager().getProject().toModelElementArray(IModelElementFactory.MODEL_TYPE_CLASS);
        }
        lClass.addStereotype(OntoUMLClassType.getStereotypeFromString(project, stereotypeName));

        String lClassName = classBaseName;
        int lIndex = 2;
        boolean lLoop = true;
        while (lLoop) {
            lLoop = false;
            int lCount = lPeers == null ? 0 : lPeers.length;
            for (int i = 0; i < lCount; i++) {
                String lName = lPeers[i].getNickname();
                if (lClassName.equals(lName)) {
                    lClassName = classBaseName + lIndex;
                    lIndex++;
                    lLoop = true;
                    i = lCount;
                }
            }
        }
        lClass.setNickname(lClassName);
        return lClass;
    }
}
