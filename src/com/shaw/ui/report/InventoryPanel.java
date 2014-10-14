package com.shaw.ui.report;

import com.jgoodies.forms.factories.Borders;
import com.shaw.manager.InventoryReportManager;
import com.shaw.ShawException;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InventoryPanel extends JPanel {
    private static final String FILTERS[] = {"Part No.", "Description", "Location", "PCP", "Quantity", "Unit Cost", "Total Cost"};
    private InventoryReportModel tableModel;
    private JTable table;
    private JTextField searchField;
    private InventoryReportManager manager;
    private SearchResultRenderer rowRenderer;
    private JComboBox searchFilter;
    private int currentIndex;
    private JScrollPane scrollPane;

    public InventoryPanel() {
        initComponents();
        buildUI();
    }

    private void initComponents() {
        manager = new InventoryReportManager();

        searchFilter = new JComboBox(FILTERS);

        tableModel = new InventoryReportModel();

        rowRenderer = new SearchResultRenderer();
        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, rowRenderer);
        searchField = new JTextField(20);


    }

    private void buildUI() {
        setLayout(new BorderLayout());

        scrollPane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(searchField);

        searchPanel.add(searchFilter);

        final JButton filterButton = new JButton("Search");
        filterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentIndex = searchFilter.getSelectedIndex();
                rowRenderer.setCurrentString(null);
                table.repaint();
                try {
                    java.util.List list = manager.searchInventoryReport(tableModel.getStartDate(), tableModel.getEndDate(), searchField.getText().trim(), false);
                    tableModel.setSearchList(list);
                } catch (ShawException se) {
                    se.printStackTrace();
                }
            }
        });
        searchPanel.add(filterButton);

        final JButton highlightButton = new JButton("Highlight");
        highlightButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentIndex = searchFilter.getSelectedIndex();
                rowRenderer.setCurrentString(searchField.getText().trim());

                Thread appThread = new Thread() {
                    public void run() {
                        try {
                            SwingUtilities.invokeAndWait(new Runnable() {
                                public void run() {
                                    table.repaint();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                appThread.start();

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        moveToFirstHighlight();
                    }
                });


            }
        });
        searchPanel.add(highlightButton);

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    filterButton.doClick();
                }
            }
        });


        JButton generate = new JButton("Show All");
        generate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableModel.showAll();
                rowRenderer.setCurrentString(null);
            }
        });
        searchPanel.add(generate);


        final JButton MADButton = new JButton("MAD");
        MADButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableModel.showMAD();
                //updatePage();
            }
        });
        searchPanel.add(MADButton);
        searchPanel.setBorder(Borders.DIALOG_BORDER);
        add(searchPanel, BorderLayout.NORTH);

        // Disable auto resizing
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        //set column widths
        TableColumn col = table.getColumnModel().getColumn(0);
        col.setPreferredWidth(100);
        col = table.getColumnModel().getColumn(1);
        col.setPreferredWidth(200);
        col = table.getColumnModel().getColumn(2);
        col.setPreferredWidth(150);
        col = table.getColumnModel().getColumn(17);
        col.setPreferredWidth(200);
    }

    private void moveToFirstHighlight() {
        int count = tableModel.getRowCount();
        for (int i = 0; i < count; i++) {
            TableCellRenderer cellRenderer = table.getCellRenderer(i, 0);
            Component component = cellRenderer.getTableCellRendererComponent(table, null, false, false, i, 0);

            Color color = component.getBackground();

            if (color.getRGB() == Color.PINK.getRGB()) {
                if (!(table.getParent() instanceof JViewport)) {
                    return;
                }
                JViewport viewport = (JViewport) table.getParent();

                // This rectangle is relative to the table where the
                // northwest corner of cell (0,0) is always (0,0).
                Rectangle rect = table.getCellRect(i, 0, true);

                // The location of the view relative to the table
                Rectangle viewRect = viewport.getViewRect();

                // Translate the cell location so that it is relative
                // to the view, assuming the northwest corner of the
                // view is (0,0).
                rect.setLocation(rect.x - viewRect.x, rect.y - viewRect.y);

                // Calculate location of rect if it were at the center of view
                int centerX = (viewRect.width - rect.width) / 2;
                int centerY = (viewRect.height - rect.height) / 2;

                // Fake the location of the cell so that scrollRectToVisible
                // will move the cell to the center
                if (rect.x < centerX) {
                    centerX = -centerX;
                }
                if (rect.y < centerY) {
                    centerY = -centerY;
                }
                rect.translate(centerX, centerY);

                // Scroll the area into view.
                viewport.scrollRectToVisible(rect);
                return;
            }
        }
    }

    public void generateReports() {
        try {
            java.util.List list = manager.getInventoryReport(tableModel.getStartDate(), tableModel.getEndDate());
            tableModel.setInventory(list);
        } catch (ShawException se) {
            se.printStackTrace();
        }
    }

    class SearchResultRenderer extends DefaultTableCellRenderer {
        private String currentString;

        public void setCurrentString(String currentString) {
            this.currentString = currentString;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            String data = tableModel.getValueAt(row, currentIndex).toString();

            if (isSelected) {
                c.setBackground(Color.YELLOW);
            } else {
                if (currentString == null || currentString.equals(""))
                    c.setBackground(Color.WHITE);
                else {
                    if (currentIndex != 4) {
                        if (data.toUpperCase().indexOf(currentString.toUpperCase()) > -1) {
                            c.setBackground(Color.PINK);
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    } else {
                        try {
                            double intVal = Double.parseDouble(data.trim());
                            double searchVal = Double.parseDouble(currentString.trim());
                            if (intVal == searchVal) {
                                c.setBackground(Color.PINK);
                            } else {
                                c.setBackground(Color.WHITE);
                            }
                        } catch (NumberFormatException nfe) {
                            c.setBackground(Color.WHITE);
                        }
                    }
                }
            }
            return c;
        }
    }
}
