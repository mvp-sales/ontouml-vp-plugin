package br.ufes.inf.ontoumlplugin.utils;

import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.synth.Region;
import java.awt.*;

public class AlloyDialog implements IDialogHandler {

    private Component component;
    private final JCheckBox identityPrincipleUi = new JCheckBox("Identity Principle");
    private final JTextArea identityPrincipleText = new JTextArea("Mark this option if you want to visualize objects that do not have identity principle. If not, leave this option unchecked.");
    private boolean identityPrinciple;

    private final JCheckBox relatorConstraintUi = new JCheckBox("Relator Constraint");
    private final JTextArea relatorConstraintText = new JTextArea("Mark this option if in your model all relators mediate at least two distinct objects. If this is not true, leave this option unchecked.");
    private boolean relatorConstraint;

    private final JCheckBox weakSupplementationUi = new JCheckBox("Weak Supplementation");
    private final JTextArea weakSupplementationText = new JTextArea("Mark this option if in your model all wholes have two or more parts. If this is not true, leave this option unchecked.");
    private boolean weakSupplementation;

    private final JCheckBox antiRidigityUi = new JCheckBox("Anti-rigidity");
    private final JTextArea antiRidigityText = new JTextArea("Mark this option if you want to enforce the visualization of anti-rigid objects. If not, leave this option unchecked.");
    private boolean antiRigidity;

    public AlloyDialog() {
        identityPrincipleText.setLineWrap(true);
        relatorConstraintText.setLineWrap(true);
        weakSupplementationText.setLineWrap(true);
        antiRidigityText.setLineWrap(true);


        JPanel panel = new JPanel(new BorderLayout());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Transformation customization options"));
        panel.add(identityPrincipleUi); panel.add(identityPrincipleText);
        panel.add(relatorConstraintUi); panel.add(relatorConstraintText);
        panel.add(weakSupplementationUi); panel.add(weakSupplementationText);
        panel.add(antiRidigityUi); panel.add(antiRidigityText);

        identityPrincipleUi.addItemListener(
            event -> identityPrinciple = identityPrincipleUi.isSelected()
        );

        relatorConstraintUi.addItemListener(
            event -> relatorConstraint = relatorConstraintUi.isSelected()
        );

        weakSupplementationUi.addItemListener(
            event -> weakSupplementation = weakSupplementationUi.isSelected()
        );

        antiRidigityUi.addItemListener(
            event -> antiRigidity = antiRidigityUi.isSelected()
        );

        component = panel;
    }

    public boolean isIdentityPrincipleChecked() {
        return identityPrinciple;
    }

    public boolean isRelatorConstraintChecked() {
        return identityPrinciple;
    }

    public boolean isWeakSupplementationChecked() {
        return identityPrinciple;
    }

    public boolean isAntiRigidityChecked() {
        return identityPrinciple;
    }

    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public void prepare(IDialog iDialog) {
        iDialog.setBounds(600, 300, 400, 400);
    }

    @Override
    public void shown() {

    }

    @Override
    public boolean canClosed() {
        return true;
    }
}
