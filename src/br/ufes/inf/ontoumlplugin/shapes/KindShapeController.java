package br.ufes.inf.ontoumlplugin.shapes;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;

import com.vp.plugin.diagram.VPShapeController;
import com.vp.plugin.diagram.VPShapeInfo;

public class KindShapeController implements VPShapeController {

	@Override
	public boolean contains(int arg0, int arg1, VPShapeInfo arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawShape(Graphics2D arg0, Paint arg1, Paint arg2, Stroke arg3, VPShapeInfo arg4) {
		// TODO Auto-generated method stub
		arg0.draw(arg4.getBounds());
	}

}
