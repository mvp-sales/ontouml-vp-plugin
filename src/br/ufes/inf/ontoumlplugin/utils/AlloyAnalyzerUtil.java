package br.ufes.inf.ontoumlplugin.utils;

import edu.mit.csail.sdg.alloy4whole.SimpleGUICustom;

public class AlloyAnalyzerUtil {

    private static SimpleGUICustom tool = new SimpleGUICustom(new String[]{""},true,"");

    public static SimpleGUICustom tool() { return tool; }
}
