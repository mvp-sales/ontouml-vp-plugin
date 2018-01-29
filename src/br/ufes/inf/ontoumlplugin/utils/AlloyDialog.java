package br.ufes.inf.ontoumlplugin.utils;

import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class AlloyDialog implements IDialogHandler {

    private Component component;
    private final JCheckBox identityPrincipleUi = new JCheckBox("Identity Principle");
    private boolean identityPrinciple;
    private final JCheckBox relatorConstraintUi = new JCheckBox("Relator Constraint");
    private boolean relatorConstraint;
    private final JCheckBox weakSupplementationUi = new JCheckBox("Weak Supplementation");
    private boolean weakSupplementation;
    private final JCheckBox antiRidigityUi = new JCheckBox("Anti-rigidity");
    private boolean antiRigidity;

    public AlloyDialog() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Transformation customization options"));
        panel.add(identityPrincipleUi);
        panel.add(relatorConstraintUi);
        panel.add(weakSupplementationUi);
        panel.add(antiRidigityUi);

        identityPrincipleUi.addItemListener(
            event -> identityPrinciple = identityPrincipleUi.isSelected()
        );

        component = panel;
    }

    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public void prepare(IDialog iDialog) {
        iDialog.setBounds(20, 20, 400, 400);
    }

    @Override
    public void shown() {

    }

    @Override
    public boolean canClosed() {
        return true;
    }
}
