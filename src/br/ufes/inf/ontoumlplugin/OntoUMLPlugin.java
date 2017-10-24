package br.ufes.inf.ontoumlplugin;

import br.ufes.inf.ontoumlplugin.project.OntoUMLPluginProjectListener;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.VPPlugin;
import com.vp.plugin.VPPluginInfo;

public class OntoUMLPlugin implements VPPlugin {

	public static final String PLUGIN_ID = "br.ufes.inf.ontoumlplugin";
	private OntoUMLPluginProjectListener projectListener = new OntoUMLPluginProjectListener();

	@Override
	public void loaded(VPPluginInfo arg0) {
		// TODO Auto-generated method stub

		if(ApplicationManager.instance().getProjectManager().getProject() != null)
			ApplicationManager
				.instance()
				.getProjectManager()
				.getProject()
				.addProjectListener(projectListener);

	}

	@Override
	public void unloaded() {
		// TODO Auto-generated method stub
		if (ApplicationManager.instance().getProjectManager().getProject() != null) {
			ApplicationManager
				.instance()
				.getProjectManager()
				.getProject()
				.removeProjectListener(projectListener);
		}
	}

}
