package br.ufes.inf.ontoumlplugin.actions;

import br.ufes.inf.ontoumlplugin.OntoUMLPlugin;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import edu.mit.csail.sdg.alloy4viz.AlloyType;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import net.menthor.alloy.AlloyFactory;

public class OntoUMLSpecificationController implements VPActionController {

    @Override
    public void performAction(VPAction vpAction) {
        ViewManager viewManager = ApplicationManager.instance().getViewManager();

        try {
            BrowserLauncher browserLauncher = new BrowserLauncher();
            browserLauncher.openURLinBrowser("http://ontology.com.br/ontouml/spec/");
        } catch (BrowserLaunchingInitializingException|UnsupportedOperatingSystemException e) {
            viewManager.showMessage("ERROR: Can't connect to browser.", OntoUMLPlugin.PLUGIN_ID);
        }
    }

    @Override
    public void update(VPAction vpAction) {

    }
}
