package tfcvesselalloycalculator.ui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import tfcvesselalloycalculator.TFCVesselAlloyCalculator;
import tfcvesselalloycalculator.vessel.VesselRecipe;
import tfcvesselalloycalculator.vessel.VesselSlot;

public class SettingsWindow extends javax.swing.JFrame {
	private VesselRecipe.Ore.SizeType selectedSizeType = null;
	private VesselRecipe.Ore selectedOreType = null;
	private VesselRecipe selectedRecipeType = null;
	private VesselRecipe.Ingredient selectedRecipeOreType = null;
	private VesselRecipe.Ore selectedRecipeIngredientOreType = null;
	private int currentOreIdx = 0;
	private int currentRecipeIdx = 0;
	
	public SettingsWindow() {
		initComponents();
		
		/*
			Ore Types Tab
		*/
		
		DefaultTableModel oreTypesTableModel = (DefaultTableModel) oreTypesTable.getModel();
		
		for(int row = 0; row < TFCVesselAlloyCalculator.settings.ores.size(); row++) {
			oreTypesTableModel.addRow(new Object[] {TFCVesselAlloyCalculator.settings.ores.get(row).getName()});
		}
		
		oreTypesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		oreTypesTable.changeSelection(0, 0, false, false);
		oreTypesTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				refreshOreTypeSelection();
			}
		});
		oreTypesTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if(e.getFirstRow() != -1 && e.getColumn() != -1 && selectedOreType != null) {
					selectedOreType.setName((String)oreTypesTable.getValueAt(e.getFirstRow(), e.getColumn()));
					TFCVesselAlloyCalculator.updateResultingString();
					refreshRecipeSelection();
					refreshRecipeOreAddComboBox();
				}
			}
		});
		
		addNewOreTypeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VesselRecipe.Ore newOre = new VesselRecipe.Ore("New Ore Name " + currentOreIdx++);
				selectedOreType = newOre;
				TFCVesselAlloyCalculator.settings.ores.add(newOre);
				oreTypesTableModel.addRow(new Object[] {newOre.getName()});
				oreTypesTable.changeSelection(oreTypesTable.getRowCount()-1, 0, false, false);
				TFCVesselAlloyCalculator.refreshOreModel();
				refreshRecipeOreAddComboBox();
			}
		});
		
		
		removeSelectedOreTypeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selectedOreType == null)
					return;
				
				for(VesselRecipe vesselRecipe : TFCVesselAlloyCalculator.settings.recipes) {
					for(VesselRecipe.Ingredient ingredient : vesselRecipe.getIngredients()) {
						if(ingredient.requiredOre.equals(selectedOreType)) {
							vesselRecipe.getIngredients().remove(ingredient);
							break;
						}
					}
				}
				
				for(VesselSlot slot : TFCVesselAlloyCalculator.vesselContainer.slots) {
					if(slot.getOreType() != null && slot.getOreType().getOre() == selectedOreType) {
						slot.setOreCount(0, false);
					}
				}
				
				TFCVesselAlloyCalculator.settings.ores.remove(selectedOreType);
				
				oreTypesTableModel.removeRow(oreTypesTable.getSelectedRow());
				oreTypesTable.changeSelection(0, 0, false, false);
				refreshOreTypeSelection();
				TFCVesselAlloyCalculator.refreshOreModel();
				TFCVesselAlloyCalculator.updateResultingString();
				refreshRecipeSelection();
				refreshRecipeOreAddComboBox();
			}
		});
		
		refreshOreTypeSelection();
		
		/*
			Ore Sizes Tab
		*/
		
		DefaultTableModel oreSizesTableModel = (DefaultTableModel) oreSizesTable.getModel();
		
		for(int row = 0; row < TFCVesselAlloyCalculator.settings.sizes.size(); row++) {
			oreSizesTableModel.addRow(new Object[] {TFCVesselAlloyCalculator.settings.sizes.get(row).toString()});
		}
		oreSizesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		oreSizesTable.changeSelection(0, 0, false, false);
		oreSizesTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				refreshOreSizeSelection();
			}
		});
		
		oreSizeSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(selectedSizeType != null) {
					selectedSizeType.setAmount((int)oreSizeSpinner.getValue());
					TFCVesselAlloyCalculator.updateResultingString();
				}
			}
		});
		
		refreshOreSizeSelection();
		
		/*
			Recipes Tab
		*/
		
		DefaultTableModel recipesTableModel = (DefaultTableModel) recipesTable.getModel();
		
		for(int row = 0; row < TFCVesselAlloyCalculator.settings.recipes.size(); row++) {
			recipesTableModel.addRow(new Object[] {TFCVesselAlloyCalculator.settings.recipes.get(row).getName()});
		}
		recipesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		recipesTable.changeSelection(0, 0, false, false);
		recipesTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				refreshRecipeSelection();
			}
		});
		
		refreshRecipeSelection();
		
		selectedOrePercentMaximumForRecipeSpinner.setModel(new SpinnerNumberModel(0.0, -1000.0, 1000.0, 0.1));
		selectedOrePercentMaximumForRecipeSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(selectedRecipeType == null || selectedRecipeOreType == null) {
					return;
				}
				selectedRecipeOreType.requiredPercentMax = (double)selectedOrePercentMaximumForRecipeSpinner.getValue();
				TFCVesselAlloyCalculator.updateResultingString();
			}
		});
		
		selectedOrePercentMinimumForRecipeSpinner.setModel(new SpinnerNumberModel(0.0, -1000.0, 1000.0, 0.1));
		selectedOrePercentMinimumForRecipeSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(selectedRecipeType == null || selectedRecipeOreType == null) {
					return;
				}
				selectedRecipeOreType.requiredPercentMin = (double)selectedOrePercentMinimumForRecipeSpinner.getValue();
				TFCVesselAlloyCalculator.updateResultingString();
			}
		});
		
		addNewRecipeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VesselRecipe newRecipe = new VesselRecipe("New Recipe " + currentRecipeIdx++, new VesselRecipe.Ingredient[] {});
				selectedRecipeType = newRecipe;
				TFCVesselAlloyCalculator.settings.recipes.add(newRecipe);
				recipesTableModel.addRow(new Object[] {newRecipe.getName()});
				recipesTable.changeSelection(recipesTable.getRowCount()-1, 0, false, false);
				refreshRecipeSelection();
				TFCVesselAlloyCalculator.updateResultingString();
			}
		});
		
		removeSelectedRecipeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selectedRecipeType == null)
					return;
				TFCVesselAlloyCalculator.settings.recipes.remove(selectedRecipeType);
				
				recipesTableModel.removeRow(recipesTable.getSelectedRow());
				recipesTable.changeSelection(0, 0, false, false);
				refreshRecipeSelection();
				TFCVesselAlloyCalculator.updateResultingString();
			}
		});
		
		oreTypesForRecipeListComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(oreTypesForRecipeListComboBox.getSelectedIndex() >= 0) {
					selectedRecipeIngredientOreType = TFCVesselAlloyCalculator.settings.ores.get(oreTypesForRecipeListComboBox.getSelectedIndex());
				}
			}
		});
		refreshRecipeOreAddComboBox();
		
		addNewOreTypeForRecipeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selectedRecipeIngredientOreType != null && selectedRecipeType != null) {
					for(VesselRecipe.Ingredient curIngredients : selectedRecipeType.getIngredients()) {
						if(curIngredients.requiredOre == selectedRecipeIngredientOreType) {
							JOptionPane.showMessageDialog(addNewOreTypeForRecipeButton, "You cannot add the same ore twice.");
							return;
						}
					}
					VesselRecipe.Ingredient newIngredient = new VesselRecipe.Ingredient(selectedRecipeIngredientOreType, 100, 100);
					selectedRecipeOreType = newIngredient;
					((DefaultListModel<String>)oreTypesForRecipeList.getModel()).addElement(selectedRecipeIngredientOreType.getName());
					oreTypesForRecipeList.setSelectedIndex(oreTypesForRecipeList.getModel().getSize()-1);
					selectedRecipeType.getIngredients().add(newIngredient);
					refreshRecipeOreTypeSelection();
					TFCVesselAlloyCalculator.updateResultingString();
				}
			}
		});
		
		removeSelectedOreForRecipeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(selectedRecipeIngredientOreType != null && selectedRecipeType != null && selectedRecipeOreType != null && oreTypesForRecipeList.getSelectedIndex() >= 0) {
					selectedRecipeType.getIngredients().remove(oreTypesForRecipeList.getSelectedIndex());
					((DefaultListModel<String>)oreTypesForRecipeList.getModel()).removeElementAt(oreTypesForRecipeList.getSelectedIndex());
					oreTypesForRecipeList.setSelectedIndex(oreTypesForRecipeList.getModel().getSize()-1);
					refreshRecipeOreTypeSelection();
					TFCVesselAlloyCalculator.updateResultingString();
				}
			}
		});
		
		recipesTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if(e.getFirstRow() != -1 && e.getColumn() != -1 && selectedRecipeType != null) {
					selectedRecipeType.setName((String)recipesTable.getValueAt(e.getFirstRow(), e.getColumn()));
					TFCVesselAlloyCalculator.updateResultingString();
				}
			}
		});
		
		refreshRecipeOreTypeSelection();
	}
	
	
	private void refreshRecipeSelection() {
		if(recipesTable.getSelectedRow() == -1 || recipesTable.getSelectedColumn() == -1 || recipesTable.getRowCount() == 0) {
			selectedRecipeType = null;
			return;
		}
		String selection = (String) recipesTable.getValueAt(recipesTable.getSelectedRow(), recipesTable.getSelectedColumn());
		for(VesselRecipe type : TFCVesselAlloyCalculator.settings.recipes) {
			if(type.getName().equals(selection)) {
				selectedRecipeType = type;
				break;
			}
		}
		
		DefaultListModel<String> oreTypesForRecipeListModel = new DefaultListModel();
		for(VesselRecipe.Ingredient ingredient : selectedRecipeType.getIngredients()) {
			oreTypesForRecipeListModel.addElement(ingredient.requiredOre.getName());
		}
		oreTypesForRecipeList.setModel(oreTypesForRecipeListModel);
		
		oreTypesForRecipeList.setSelectedIndex(0);
		oreTypesForRecipeList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				refreshRecipeOreTypeSelection();
			}
		});
		
		refreshRecipeOreTypeSelection();
	}
	
	private void refreshRecipeOreTypeSelection() {
		if(oreTypesForRecipeList.getSelectedIndex() == -1 || oreTypesForRecipeList.getModel().getSize() == 0 || selectedRecipeType == null) {
			
			selectedRecipeOreType = null;
			return;
		}
		String selection = (String) oreTypesForRecipeList.getSelectedValue();
		for(VesselRecipe.Ingredient type : selectedRecipeType.getIngredients()) {
			if(type.requiredOre.getName().equals(selection)) {
				selectedRecipeOreType = type;
				break;
			}
		}
		
		selectedOrePercentMaximumForRecipeSpinner.setValue(selectedRecipeOreType.requiredPercentMax);
		selectedOrePercentMinimumForRecipeSpinner.setValue(selectedRecipeOreType.requiredPercentMin);
	}
	
	private void refreshRecipeOreAddComboBox() {
		oreTypesForRecipeListComboBox.removeAllItems();
		for(VesselRecipe.Ore type : TFCVesselAlloyCalculator.settings.ores) {
			oreTypesForRecipeListComboBox.addItem(type.getName());
		}
		
		if(!TFCVesselAlloyCalculator.settings.ores.isEmpty()) {
			oreTypesForRecipeListComboBox.setSelectedIndex(0);
			selectedRecipeIngredientOreType = TFCVesselAlloyCalculator.settings.ores.get(0);
		}
	}
	
	private void refreshOreTypeSelection() {
		if(oreTypesTable.getSelectedRow() == -1 || oreTypesTable.getSelectedColumn() == -1 || oreTypesTable.getRowCount() == 0) {
			selectedOreType = null;
			return;
		}
		String selection = (String) oreTypesTable.getValueAt(oreTypesTable.getSelectedRow(), oreTypesTable.getSelectedColumn());
		for(VesselRecipe.Ore type : TFCVesselAlloyCalculator.settings.ores) {
			if(type.getName().equals(selection)) {
				selectedOreType = type;
				break;
			}
		}
	}
	
	private void refreshOreSizeSelection() {
		String selection = (String) oreSizesTable.getValueAt(oreSizesTable.getSelectedRow(), oreSizesTable.getSelectedColumn());

		for(VesselRecipe.Ore.SizeType type : TFCVesselAlloyCalculator.settings.sizes) {
			if(type.toString().equals(selection)) {
				selectedSizeType = type;
				break;
			}
		}
		oreSizeSpinner.setValue(selectedSizeType.getAmount());
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        oreSizesPane = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        addNewOreTypeButton = new javax.swing.JButton();
        removeSelectedOreTypeButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        oreTypesTable = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        oreSizeSpinner = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        oreSizesTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        recipesTable = new javax.swing.JTable();
        addNewRecipeButton = new javax.swing.JButton();
        removeSelectedRecipeButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        oreTypesForRecipeList = new javax.swing.JList<>();
        oreTypesForRecipeListComboBox = new javax.swing.JComboBox<>();
        addNewOreTypeForRecipeButton = new javax.swing.JButton();
        removeSelectedOreForRecipeButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        selectedOrePercentMinimumForRecipeSpinner = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        selectedOrePercentMaximumForRecipeSpinner = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Settings");
        setAlwaysOnTop(true);

        addNewOreTypeButton.setText("Add");

        removeSelectedOreTypeButton.setText("Remove");

        jScrollPane1.setPreferredSize(null);

        oreTypesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1"
            }
        ));
        oreTypesTable.setColumnSelectionAllowed(true);
        oreTypesTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(oreTypesTable);
        oreTypesTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (oreTypesTable.getColumnModel().getColumnCount() > 0) {
            oreTypesTable.getColumnModel().getColumn(0).setResizable(false);
        }
        oreTypesTable.setTableHeader(null);

        jLabel5.setText("Double click an entry to edit its text.");

        jButton1.setText("How to Add Textures");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addNewOreTypeButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeSelectedOreTypeButton))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addNewOreTypeButton)
                    .addComponent(removeSelectedOreTypeButton)
                    .addComponent(jLabel5)
                    .addComponent(jButton1)))
        );

        oreSizesPane.addTab("Ore Types", jPanel2);

        oreSizeSpinner.setMinimumSize(new java.awt.Dimension(50, 20));
        oreSizeSpinner.setPreferredSize(new java.awt.Dimension(50, 20));

        jLabel1.setText("units");

        jLabel3.setText("Size:");

        oreSizesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        oreSizesTable.setColumnSelectionAllowed(true);
        oreSizesTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(oreSizesTable);
        oreSizesTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (oreSizesTable.getColumnModel().getColumnCount() > 0) {
            oreSizesTable.getColumnModel().getColumn(0).setResizable(false);
        }
        oreSizesTable.setTableHeader(null);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(oreSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addContainerGap(255, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(oreSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(427, Short.MAX_VALUE))
            .addComponent(jScrollPane3)
        );

        oreSizesPane.addTab("Ore Sizes", jPanel1);

        recipesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1"
            }
        ));
        recipesTable.setColumnSelectionAllowed(true);
        recipesTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(recipesTable);
        recipesTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (recipesTable.getColumnModel().getColumnCount() > 0) {
            recipesTable.getColumnModel().getColumn(0).setResizable(false);
        }
        recipesTable.setTableHeader(null);

        addNewRecipeButton.setText("Add");

        removeSelectedRecipeButton.setText("Remove");

        jScrollPane4.setViewportView(oreTypesForRecipeList);

        oreTypesForRecipeListComboBox.setMaximumRowCount(99);
        oreTypesForRecipeListComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        addNewOreTypeForRecipeButton.setText("Add");

        removeSelectedOreForRecipeButton.setText("Remove");

        jLabel2.setText("Percent Minimum: ");

        selectedOrePercentMinimumForRecipeSpinner.setValue(0.00D);

        jLabel4.setText("Percent Maximum: ");

        selectedOrePercentMaximumForRecipeSpinner.setValue(0.00D);

        jLabel6.setText("Double click an entry to edit its text.");

        jLabel7.setText("Required Ores");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(addNewRecipeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(removeSelectedRecipeButton)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(oreTypesForRecipeListComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(addNewOreTypeForRecipeButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(removeSelectedOreForRecipeButton))
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(selectedOrePercentMinimumForRecipeSpinner))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(selectedOrePercentMaximumForRecipeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(removeSelectedRecipeButton)
                            .addComponent(addNewRecipeButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(selectedOrePercentMinimumForRecipeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(selectedOrePercentMaximumForRecipeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(oreTypesForRecipeListComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addNewOreTypeForRecipeButton)
                    .addComponent(removeSelectedOreForRecipeButton)
                    .addComponent(jLabel6)))
        );

        oreSizesPane.addTab("Recipes", jPanel3);

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(oreSizesPane)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(saveButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(oreSizesPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveButton))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
		try(FileWriter writer = new FileWriter("." + File.separator + "TFCVesselAlloyCalculatorSettings.json")) {
			TFCVesselAlloyCalculator.gson.toJson(TFCVesselAlloyCalculator.settings, writer);
			JOptionPane.showMessageDialog(saveButton, "Successfully saved.");
		} catch(IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(saveButton, "Could not save file!", "Error saving", JOptionPane.ERROR_MESSAGE);
		}
    }//GEN-LAST:event_saveButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JOptionPane.showMessageDialog(jButton1, "There are 4 required files per ore:\n" +
		OreTableEntry.getTexturePathForSize("EXAMPLE", VesselRecipe.Ore.SizeType.POOR) + "\n" +
		OreTableEntry.getTexturePathForSize("EXAMPLE", VesselRecipe.Ore.SizeType.SMALL) + "\n" +
		OreTableEntry.getTexturePathForSize("EXAMPLE", VesselRecipe.Ore.SizeType.REGULAR) + "\n" +
		OreTableEntry.getTexturePathForSize("EXAMPLE", VesselRecipe.Ore.SizeType.RICH) + "\n");
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addNewOreTypeButton;
    private javax.swing.JButton addNewOreTypeForRecipeButton;
    private javax.swing.JButton addNewRecipeButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSpinner oreSizeSpinner;
    private javax.swing.JTabbedPane oreSizesPane;
    private javax.swing.JTable oreSizesTable;
    private javax.swing.JList<String> oreTypesForRecipeList;
    private javax.swing.JComboBox<String> oreTypesForRecipeListComboBox;
    private javax.swing.JTable oreTypesTable;
    private javax.swing.JTable recipesTable;
    private javax.swing.JButton removeSelectedOreForRecipeButton;
    private javax.swing.JButton removeSelectedOreTypeButton;
    private javax.swing.JButton removeSelectedRecipeButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JSpinner selectedOrePercentMaximumForRecipeSpinner;
    private javax.swing.JSpinner selectedOrePercentMinimumForRecipeSpinner;
    // End of variables declaration//GEN-END:variables
}
