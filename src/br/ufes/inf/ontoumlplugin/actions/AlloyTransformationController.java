package br.ufes.inf.ontoumlplugin.actions;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import net.menthor.ontouml2alloy.OntoUML2AlloyOptions;

import javax.swing.*;

public class AlloyTransformationController implements VPActionController {

    @Override
    public void performAction(VPAction vpAction) {
        OntoUML2AlloyOptions ontoumlOptions = new OntoUML2AlloyOptions();
        ontoumlOptions.identityPrinciple = true;
        ontoumlOptions.relatorConstraint = true;
        ontoumlOptions.weakSupplementation = false;
        ontoumlOptions.antiRigidity = false;


    }

    @Override
    public void update(VPAction vpAction) {

    }
}
