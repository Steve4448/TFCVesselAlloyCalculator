package tfcvesselalloycalculator.vessel;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import tfcvesselalloycalculator.TFCVesselAlloyCalculator;
import tfcvesselalloycalculator.ui.OreTableEntry;

public class VesselSlot extends JLabel {
	public static final int MAX_STACK_SIZE = 16;
	private int oreCount = 0;
	private OreTableEntry oreType = null;
	
	public VesselSlot() {}

	public void set(OreTableEntry oreType, int oreCount) {
		this.oreType = oreType;
		setOreCount(oreCount);
	}

	public int getOreCount() {
		return oreCount;
	}

	public void setOreCount(int oreCount) {
		setOreCount(oreCount, false);
	}
	
	public void setOreCount(int oreCount, boolean updateTableComponent) {
		if(oreType == null)
			return;
		
		int prevValue = this.oreCount;
		this.oreCount = oreCount;
		if(prevValue <= 0 && oreCount > 0) {
			setIcon(oreType);
			oreType.setBackgroundColor(Color.WHITE);
			((DefaultTableModel) TFCVesselAlloyCalculator.oreSelectionTable.getModel()).fireTableCellUpdated(oreType.getRow(), oreType.getColumn());
		} else if(prevValue > 0 && oreCount <= 0) {
			setIcon(null);
			if(updateTableComponent) {
				oreType.setBackgroundColor(Color.GRAY);
				((DefaultTableModel) TFCVesselAlloyCalculator.oreSelectionTable.getModel()).fireTableCellUpdated(oreType.getRow(), oreType.getColumn());
			}
			oreType = null;
		}
		if(oreCount > 0)
			setText(Integer.toString(oreCount));
		else
			setText("");
	}

	public OreTableEntry getOreType() {
		return oreType;
	}
}
