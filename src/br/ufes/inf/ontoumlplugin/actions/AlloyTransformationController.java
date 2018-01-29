package br.ufes.inf.ontoumlplugin.actions;

import RefOntoUML.Package;
import RefOntoUML.parser.OntoUMLParser;
import RefOntoUML.parser.SyntacticVerificator;
import br.ufes.inf.ontoumlplugin.OntoUMLPlugin;
import br.ufes.inf.ontoumlplugin.model.Vp2OntoUmlConverter;
import br.ufes.inf.ontoumlplugin.utils.AlloyAnalyzerUtil;
import br.ufes.inf.ontoumlplugin.utils.AlloyDialog;
import br.ufes.inf.ontoumlplugin.utils.CommonUtils;
import edu.mit.csail.sdg.alloy4whole.SimpleGUICustom;
import io.reactivex.Observable;
import net.menthor.common.file.FileUtil;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IProject;
import io.reactivex.schedulers.Schedulers;
import net.menthor.alloy.AlloyModule;
import net.menthor.ontouml2alloy.OntoUML2Alloy;
import net.menthor.ontouml2alloy.OntoUML2AlloyOptions;
import net.menthor.tocl.parser.TOCLParser;
import net.menthor.tocl.tocl2alloy.TOCL2Alloy;
import net.menthor.tocl.tocl2alloy.TOCL2AlloyOption;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class AlloyTransformationController implements VPActionController {

    private AlloySpec alloySpec;
    private OntoUML2AlloyOptions refOptions = new OntoUML2AlloyOptions();
    private TOCL2AlloyOption oclOptions = new TOCL2AlloyOption();
    private Package ontoUmlPackage;
    private final ViewManager viewManager = ApplicationManager.instance().getViewManager();

    @Override
    public void performAction(VPAction vpAction) {
        refOptions.identityPrinciple = true;
        refOptions.relatorConstraint = true;
        refOptions.weakSupplementation = false;
        refOptions.antiRigidity = false;

        //TOCL2AlloyOption oclOptions = new TOCL2AlloyOption(this.oclOptions.getParser());
        /*oclOptions.setTransformationType(constraintSimulationPanel.getTransformationsTypesListSelected());
        oclOptions.setCommandScope(constraintSimulationPanel.getScopesListSelected());
        oclOptions.setBiwidth(constraintSimulationPanel.getBitWidthListSelected());
        oclOptions.setWorldScope(constraintSimulationPanel.getWorldScopeListSelected());
        oclOptions.setConstraintList(constraintSimulationPanel.getConstraintListSelected());*/

        ApplicationManager.instance().getViewManager().showDialog(new AlloyDialog());


        /*IProject project = ApplicationManager.instance().getProjectManager().getProject();

        viewManager.clearMessages(OntoUMLPlugin.PLUGIN_ID);
        viewManager.removeMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID);

        alloySpec = new AlloySpec( System.getProperty("java.io.tmpdir")+ File.separator + project.getName() + ".als");

        Vp2OntoUmlConverter vp2OntoUmlConverter = new Vp2OntoUmlConverter(project);

        viewManager.showMessage("Loading, please wait...", OntoUMLPlugin.PLUGIN_ID);

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
                        runOntouml(parser);
                        //runOcl(parser);
                        final Timer timer = new Timer(100, null);
                        ActionListener listener = (event) -> {
                            if (AlloyAnalyzerUtil.tool().isInitialized())
                            {
                                AlloyAnalyzerUtil.tool().setTheme(alloySpec.getDirectory() + "standart_theme.thm");
                                AlloyAnalyzerUtil.tool().doOpenFile(alloySpec.getAlloyPath());
                                AlloyAnalyzerUtil.tool().doRun(1);
                                timer.stop();
                            }
                        };
                        timer.addActionListener(listener);
                        timer.start();
                    }
                },
                err -> viewManager.showMessage(err.getMessage(), OntoUMLPlugin.PLUGIN_ID)
            );*/
    }

    @Override
    public void update(VPAction vpAction) {

    }

    private void runOntouml(OntoUMLParser refparser) throws Exception {
        alloySpec.setDomainModel(refparser,refOptions);
        alloySpec.transformDomainModel();
    }

    private void runOcl(OntoUMLParser refparser) {
        try {
            String logMessage = alloySpec.transformConstraints(refparser, oclOptions.getParser(),oclOptions);
            if (logMessage!=null && !logMessage.isEmpty()){
                viewManager.showMessage(logMessage, OntoUMLPlugin.PLUGIN_ID);
            }
        } catch (Exception e) {
            viewManager.showMessage("Current OCL constraints could not be transformed to Alloy.", OntoUMLPlugin.PLUGIN_ID);
        }
    }

    class AlloySpec {

        /** Absolute directory path of alloy specification. */
        public String alsOutDirectory;

        /** File name of alloy specification. */
        private String alsmodelName;

        /** Absolute path of alloy specification. */
        private String alsPath;

        /** Alloy Module */
        private AlloyModule alsModule;
        private OntoUML2Alloy ontouml2alloy;

        /** Additional content of alloy specification. */
        private String additionalContent = new String();

        /** Log details for operations made. */
        private String logDetails = new String();

        /**
         * This constructor basically initialize the path of alloy model, i.e. without any content.
         */
        public AlloySpec(String alloyPath)
        {
            this();

            setAlloyModel(alloyPath);
        }

        public AlloySpec(String alloyPath,OntoUMLParser refparser, OntoUML2AlloyOptions optmodel) throws Exception
        {
            this();

            setAlloyModel(alloyPath,refparser,optmodel);
        }

        public AlloySpec() { }

        /**
         * Private methods
         */
        private void setAlloyModel(String alloyPath, OntoUMLParser refparser, OntoUML2AlloyOptions optmodel) throws Exception
        {
            setAlloyModel(alloyPath);
            setDomainModel(refparser,optmodel);
        }

        private void setAlloyModel(String alloyPath)
        {
            this.alsPath = alloyPath;
            File file = new File(alsPath);
            file.deleteOnExit();

            alsOutDirectory = alsPath.substring(0, alsPath.lastIndexOf(File.separator)+1);
            alsmodelName = alsPath.substring(alsPath.lastIndexOf(File.separator)+1,alsPath.length()).replace(".als","");
        }

        public void setAlloyPath(String alloyPath)
        {
            this.alsPath = alloyPath;
            alsOutDirectory = alsPath.substring(0, alsPath.lastIndexOf(File.separator)+1);
            alsmodelName = alsPath.substring(alsPath.lastIndexOf(File.separator)+1,alsPath.length()).replace(".als","");
        }

        public void setDomainModel(OntoUMLParser refparser, OntoUML2AlloyOptions ontoOptions)
        {
            ontouml2alloy = new OntoUML2Alloy(refparser, alsPath, ontoOptions);
            alsModule = ontouml2alloy.transformer.module;
        }

        public void appendContent(String content) throws IOException
        {
            additionalContent = additionalContent+content;
            FileUtil.writeToFile(alsModule.toString()+"\n"+additionalContent, alsPath);
        }

        /**
         * Transformations
         */
        public void transformDomainModel() throws Exception
        {
            ontouml2alloy.transform();
            FileUtil.writeToFile(alsModule.toString()+"\n"+additionalContent, alsPath);
        }

        public String transformConstraints(OntoUMLParser refparser, TOCLParser toclparser, TOCL2AlloyOption oclOptions) throws IOException
        {
            additionalContent += "\n"+TOCL2Alloy.convertHistoricalRelationships(ontouml2alloy.transformer.factory, ontouml2alloy.transformer.sigObject, toclparser);
            additionalContent += "\n"+TOCL2Alloy.convertTemporalConstraints(toclparser, oclOptions);

            FileUtil.writeToFile(alsModule.toString()+"\n"+additionalContent, alsPath);

            return TOCL2Alloy.log;
        }

        /** Get Log details for made operations. */
        public String getDetails() { return logDetails; }

        /**  Get absolute path of alloy specification. */
        public String getAlloyPath() { return alsPath; }

        /** Get file name of alloy specification. */
        public String getAlloyModelName() {	return alsmodelName; }

        /** Get content of alloy specification. */
        public String getContent() { return alsModule.toString()+"\n"+additionalContent; }

        /** Get the Destination Directory of this model. */
        public String getDirectory() { return alsOutDirectory; }

    }

}
