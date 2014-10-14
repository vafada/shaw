package com.shaw.ui.admin;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.builder.PanelBuilder;
import com.shaw.manager.ProductManager;
import com.shaw.ShawException;
import com.shaw.bean.ProductBean;
import com.shaw.ui.InfoPanel;
import com.shaw.ui.report.InventoryPanel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ProductTab extends JPanel {
    private static final int NEW = 1;
    private static final int EDIT = 2;

    private ProductManager productManager;

    //private JLabel page;
    private SearchResultRenderer rowRenderer;
    private JTable table;
    private ProductTableModel productModel;

    private JTextField partNumber;
    private JTextField description;
    private JTextField location;
    private JTextField quantity;
    private JTextField pcp;
    private JTextField miscelleneous;
    private JTextField otherNumber;


    private JButton addUpdate;
    private InfoPanel errorPanel;
    private JPanel bottomPanel;

    private int editRowIndex;
    private String editPartNumber;

    private int ACTION = NEW;

    public ProductTab() {
        initComponents();
        buildUI();
    }

    private void initComponents() {
        rowRenderer = new SearchResultRenderer();
        productModel = new ProductTableModel();
        productManager = new ProductManager();
        table = new JTable(productModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Object.class, rowRenderer);

        //page = new JLabel("page 0 / 0");
        partNumber = new JTextField(30);
        description = new JTextField(30);
        location = new JTextField(30);
        pcp = new JTextField(30);
        quantity = new JTextField(30);
        miscelleneous = new JTextField(30);
        otherNumber = new JTextField(30);

        errorPanel = new InfoPanel();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        //Table

        JPanel searchPanel = new JPanel(new FlowLayout());
        final JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);

        final JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final String search = searchField.getText().trim();
                rowRenderer.setCurrentString(search);
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
                        moveToItem(search);
                    }
                });

            }
        });
        searchPanel.add(searchButton);

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    searchButton.doClick();
                }
            }
        });

        add(searchPanel, BorderLayout.NORTH);
        /*
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        JPanel pagingPanel = new JPanel(new BorderLayout());
        JButton prev = new JButton("< prev");
        prev.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                productModel.prev();
                updatePage();
            }
        });

        JButton next = new JButton("next >");
        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                productModel.next();
                updatePage();
            }
        });
        pagingPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pagingPanel.add(prev, BorderLayout.WEST);
        pagingPanel.add(next, BorderLayout.EAST);
        pagingPanel.add(page, BorderLayout.CENTER);
        page.setHorizontalAlignment(JLabel.CENTER);
        centerPanel.add(pagingPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        */
        add(new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        bottomPanel = new JPanel(new BorderLayout());

        FormLayout layout = new FormLayout("left:max(40dlu;pref), 5dlu, fill:max(100dlu;min), 10dlu, left:max(40dlu;pref), 5dlu, fill:max(100dlu;min)",
                "p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p");
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();

        CellConstraints cc = new CellConstraints();

        int y = 1;

        builder.addLabel("Part number:", cc.xy(1, y));
        builder.add(partNumber, cc.xy(3, y));

        builder.addLabel("PCP:", cc.xy(5, y));
        builder.add(pcp, cc.xy(7, y));

        y += 2;

        builder.addLabel("Description:", cc.xy(1, y));
        builder.add(description, cc.xy(3, y));

        builder.addLabel("Misc.:", cc.xy(5, y));
        builder.add(miscelleneous, cc.xy(7, y));

        y += 2;

        builder.addLabel("Location:", cc.xy(1, y));
        builder.add(location, cc.xy(3, y));

        builder.addLabel("Other no.:", cc.xy(5, y));
        builder.add(otherNumber, cc.xy(7, y));

        y += 2;

        builder.addLabel("Quantity:", cc.xy(1, y));
        builder.add(quantity, cc.xy(3, y));

        y += 2;

        FlowLayout buttonLayout = new FlowLayout(FlowLayout.LEFT);
        JPanel buttonPanel = new JPanel(buttonLayout);

        JButton button = new JButton("New");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ACTION = NEW;
                addUpdate.setText("Add");
                clearFields();
            }
        });
        buttonPanel.add(button);

        addUpdate = new JButton("Add");
        addUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (validateInput()) {
                    ProductBean product = new ProductBean();
                    product.setPartNumber(partNumber.getText().trim());
                    product.setDescription(description.getText().trim());
                    product.setLocation(location.getText().trim());
                    if (quantity.getText().trim().length() != 0)
                        product.setQuantity(Integer.parseInt(quantity.getText().trim()));
                    else
                        product.setQuantity(0);

                    if (pcp.getText().trim().length() != 0)
                        product.setPcp(Double.parseDouble(pcp.getText().trim()));
                    else
                        product.setPcp(0);

                    product.setMiscelleneous(miscelleneous.getText().trim());
                    product.setOtherNumber(otherNumber.getText().trim());

                    try {
                        if (ACTION == NEW) {
                            productManager.addProduct(product);
                            productModel.addProduct(product);
                            //updatePage();
                        } else if (ACTION == EDIT) {
                            productManager.updateProduct(editPartNumber, product);
                            productModel.updateProduct(editRowIndex, product);
                        }
                        clearFields();
                        removeErrorPanel();
                    } catch (ShawException se) {
                        showErrorPanel(se.getMessage());
                    }
                }
            }
        });
        buttonPanel.add(addUpdate);

        button = new JButton("Delete");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (editRowIndex != -1) {
                    ProductBean product = productModel.getProduct(editRowIndex);
                    try {
                        productManager.deleteProduct(product.getPartNumber());
                        productModel.removeProduct(editRowIndex);
                        clearFields();
                        removeErrorPanel();
                        editRowIndex = -1;
                        //updatePage();
                    } catch (ShawException se) {
                        showErrorPanel(se.getMessage());
                    }
                }
            }
        });
        buttonPanel.add(button);

        builder.add(buttonPanel, cc.xyw(1, y, 3));

        /* builder.addLabel("Go to:", cc.xy(5, y));
         final JTextField goTo = new JTextField();
         builder.add(goTo, cc.xy(7, y));
         goTo.addKeyListener(new KeyAdapter() {
             public void keyReleased(KeyEvent e) {
                 if (e.getKeyCode() == 10) {

                 }
             }
         });                                                         */

        bottomPanel.add(buttonPanel.getParent(), BorderLayout.WEST);

        add(bottomPanel, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }

                if (e.getSource() == table.getSelectionModel()) {
                    int row = table.getSelectedRow();
                    fillForm(row);
                }
            }
        });
    }

    /*
    private void updatePage() {
        int totalPage = productModel.getPageCount();
        int current = productModel.getPageOffset();

        if (totalPage != 0)
            page.setText("page " + (current + 1) + " / " + totalPage);
        else
            page.setText("page 0 / 0");

    }
    */

    private void fillForm(int rowIndex) {
        if (rowIndex != -1) {
            ACTION = EDIT;

            addUpdate.setText("Update");

            ProductBean product = productModel.getProduct(rowIndex);
            partNumber.setText(product.getPartNumber());
            description.setText(product.getDescription());
            location.setText(product.getLocation());
            quantity.setText(String.valueOf(product.getQuantity()));
            pcp.setText(String.valueOf(product.getPcp()));
            miscelleneous.setText(product.getMiscelleneous());
            otherNumber.setText(product.getOtherNumber());

            editRowIndex = rowIndex;
            editPartNumber = product.getPartNumber();

            removeErrorPanel();
        } else {
            clearFields();
            removeErrorPanel();
        }

    }

    private boolean validateInput() {
        if (partNumber.getText().trim().length() > 20) {
            showErrorPanel("Part number must be less than 20 characters");
            return false;
        }
        if (partNumber.getText().trim().length() == 0) {
            showErrorPanel("Invalid part number");
            return false;
        }
        if (quantity.getText().trim().length() != 0) {
            try {
                Integer.parseInt(quantity.getText().trim());
            } catch (NumberFormatException nfe) {
                showErrorPanel("Invalid quantity");
                return false;
            }
        }
        if (pcp.getText().trim().length() != 0) {
            try {
                Double.parseDouble(pcp.getText().trim());
            } catch (NumberFormatException nfe) {
                showErrorPanel("Invalid pcp");
                return false;
            }
        }
        removeErrorPanel();
        return true;
    }

    private void clearFields() {
        ACTION = NEW;
        addUpdate.setText("Add");
        partNumber.setText("");
        description.setText("");
        location.setText("");
        quantity.setText("");

        pcp.setText("");
        miscelleneous.setText("");
        otherNumber.setText("");
    }

    private void showErrorPanel(String error) {
        errorPanel.setErrorText(error);
        bottomPanel.add(errorPanel, BorderLayout.CENTER);
        bottomPanel.updateUI();
    }

    private void removeErrorPanel() {
        bottomPanel.remove(errorPanel);
        bottomPanel.updateUI();
    }

    public void buildData() {
        try {
            productModel.setProductList(productManager.getProducts());
            //updatePage();
        } catch (ShawException se) {
            se.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error getting product list",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void moveToItem(String itemNumber) {
        int count = productModel.getRowCount();
        for (int i = 0; i < count; i++) {
            String val = String.valueOf(productModel.getValueAt(i, 0));
            if (val.equalsIgnoreCase(itemNumber)) {
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

    class SearchResultRenderer extends DefaultTableCellRenderer {
        private String currentString;

        public void setCurrentString(String currentString) {
            this.currentString = currentString;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            String data = productModel.getValueAt(row, 0).toString();

            if (isSelected) {
                c.setBackground(Color.YELLOW);
            } else {
                if (currentString == null || currentString.equals(""))
                    c.setBackground(Color.WHITE);
                else {
                    if (data.equalsIgnoreCase(currentString)) {
                        c.setBackground(Color.PINK);
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
            }
            return c;
        }
    }
}


