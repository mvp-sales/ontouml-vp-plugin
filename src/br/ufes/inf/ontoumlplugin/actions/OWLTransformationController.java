package br.ufes.inf.ontoumlplugin.actions;

import RefOntoUML.Package;
import RefOntoUML.parser.OntoUMLParser;
import RefOntoUML.parser.SyntacticVerificator;
import RefOntoUML.util.RefOntoUMLResourceUtil;
import br.ufes.inf.ontoumlplugin.OntoUMLPlugin;
import br.ufes.inf.ontoumlplugin.model.Vp2OntoUmlConverter;
import br.ufes.inf.ontoumlplugin.utils.CommonUtils;
import br.ufes.inf.ontoumlplugin.utils.OwlSettingsMap;
import br.ufes.inf.ontoumlplugin.utils.ResultType;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IProject;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import net.menthor.common.settings.owl.OwlOptions;

import net.menthor.common.settings.owl.OWL2Approach;
import net.menthor.common.settings.owl.OWL2Destination;
import net.menthor.common.settings.owl.OwlAxioms;
import net.menthor.ontouml2simpleowl.OntoUML2SimpleOWL;
import net.menthor.ontouml2temporalowl.auxiliary.OWLMappingTypes;
import net.menthor.ontouml2temporalowl.auxiliary.OWLStructure;
import net.menthor.ontouml2temporalowl.tree.TreeProcessor;
import net.menthor.ontouml2temporalowl.verbose.FileManager;
import net.menthor.ootos.OntoUML2OWL;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class OWLTransformationController implements VPActionController {

    private final ViewManager viewManager = ApplicationManager.instance().getViewManager();
    private Package ontoUmlPackage;

    @Override
    public void performAction(VPAction vpAction) {
        IProject project = ApplicationManager.instance().getProjectManager().getProject();

        viewManager.clearMessages(OntoUMLPlugin.PLUGIN_ID);
        viewManager.removeMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID);

        Vp2OntoUmlConverter vp2OntoUmlConverter = new Vp2OntoUmlConverter(project);

        Observable.fromCallable(vp2OntoUmlConverter::transform)
            .subscribeOn(Schedulers.computation())
            .map(
                ontoUmlPackage -> {
                    this.ontoUmlPackage = ontoUmlPackage;
                    SyntacticVerificator verificator = new SyntacticVerificator();
                    verificator.run(ontoUmlPackage);
                    return verificator;
                }
            )
            .observeOn(Schedulers.trampoline())
            .subscribe(
                syntacticVerificator -> {
                    if (!syntacticVerificator.getMap().isEmpty()){
                        CommonUtils.showModelErrors(syntacticVerificator.getTimingMessage(), syntacticVerificator.getMap(), viewManager);
                    }else {
                        OntoUMLParser parser = new OntoUMLParser(this.ontoUmlPackage);
                        OwlOptions options = OwlSettingsMap.getInstance().getOwlOptions(parser);
                        generateOwl(parser, ontoUmlPackage, "", options);
                    }
                },
                err -> viewManager.showMessage(err.getMessage(), OntoUMLPlugin.PLUGIN_ID)
            );
    }

    @Override
    public void update(VPAction vpAction) {

    }

    public void generateOwl(OntoUMLParser filteredParser, RefOntoUML.Package model, String oclRules, OwlOptions trOpt)
    {
        String errors = "";
        String owlOutput = "";
        OwlAxioms owlOptions = (OwlAxioms) trOpt.getOwlAxioms();
        try {
            if(trOpt.getApproach()==OWL2Approach.SIMPLE)
            {
                owlOutput = OntoUML2SimpleOWL.Transformation(model, owlOptions.getIRI());
            }
            if(trOpt.getApproach()==OWL2Approach.OOTOS)
            {
                OntoUML2OWL ontoUML2OWL = new OntoUML2OWL();
                owlOutput = ontoUML2OWL.Transformation(filteredParser, oclRules, trOpt, CommonUtils.getTempDir());
                errors = ontoUML2OWL.errors;
            }
            if(trOpt.getApproach()==OWL2Approach.REIFICATION || trOpt.getApproach()==OWL2Approach.WORM_VIEW_A0 ||
                    trOpt.getApproach()==OWL2Approach.WORM_VIEW_A1 || trOpt.getApproach()==OWL2Approach.WORM_VIEW_A2)
            {
                OWLMappingTypes mtypes = OWLMappingTypes.REIFICATION;
                if(trOpt.getApproach()==OWL2Approach.WORM_VIEW_A0) mtypes = OWLMappingTypes.WORM_VIEW_A0;
                if(trOpt.getApproach()==OWL2Approach.WORM_VIEW_A1) mtypes = OWLMappingTypes.WORM_VIEW_A1;
                if(trOpt.getApproach()==OWL2Approach.WORM_VIEW_A2) mtypes = OWLMappingTypes.WORM_VIEW_A2;
                TreeProcessor tp = new TreeProcessor(model);
                OWLStructure owl = new OWLStructure(mtypes, tp);
                owl.map(tp);
                owlOutput = owl.verbose(owlOptions.getIRI());
            }
            if(owlOutput.length()>0) {
                JFileChooser fileChooser = viewManager.createJFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Web Ontology Language (*.owl)", "owl");
                fileChooser.setFileFilter(filter);
                fileChooser.setDialogTitle("Select the destination directory");
                fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
                int returnValue = fileChooser.showSaveDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File outputFolder = fileChooser.getCurrentDirectory();
                    String fileName = fileChooser.getSelectedFile().getName();
                    if (!fileName.contains(".owl")) {
                        fileName += ".owl";
                    }
                    File file = new File(outputFolder.getAbsolutePath() + File.separator + fileName);
                    if (file.exists()) {
                        int result = JOptionPane.showConfirmDialog(null, "The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
                        switch (result) {
                            case JOptionPane.YES_OPTION:
                                BufferedWriter out = new BufferedWriter(new FileWriter(file));
                                out.write(owlOutput);
                                viewManager.showMessage("OWL generated successfully", OntoUMLPlugin.PLUGIN_ID);
                                break;
                            case JOptionPane.NO_OPTION:
                            case JOptionPane.CLOSED_OPTION:
                            case JOptionPane.CANCEL_OPTION:
                                break;
                        }
                    } else {
                        BufferedWriter out = new BufferedWriter(new FileWriter(file));
                        out.write(owlOutput);
                        viewManager.showMessage("OWL generated successfully", OntoUMLPlugin.PLUGIN_ID);
                    }


                } else {
                    viewManager.showMessage(errors + "No OWL generated", OntoUMLPlugin.PLUGIN_ID);
                }
            }else{
                viewManager.showMessage(errors + "No OWL generated", OntoUMLPlugin.PLUGIN_ID);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            viewManager.showMessage("Error while generating the OWL for the model. \nDetails: " + ex.getMessage() + errors, OntoUMLPlugin.PLUGIN_ID);
        }
    }

}
