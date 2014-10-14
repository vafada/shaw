package com.shaw.ui.sales;

import com.shaw.manager.SalesManager;
import com.shaw.ShawException;
import com.shaw.Utility;
import com.shaw.ui.InfoPanel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.builder.PanelBuilder;
import com.toedter.calendar.JCalendar;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class BrowseSalesDialog extends JDialog {
   // private JLabel page;
    private BrowseSalesModel model = null;
    private InfoPanel infoPanel;
    private int selectedId = -1;
    private JPanel bottomPanel;
    private SalesManager salesManager;
    private MonthChooser monthChooser;
    private YearChooser yearChooser;
    private JTextField totalSales;
    private JTextField totalCost;
    private JTextField dateField;
    private JTextField customerField;
    private JTextField invoiceField;
    private JRadioButton monthly;
    private JRadioButton daily;
    private JRadioButton customer;
    private JRadioButton invoice;

    public BrowseSalesDialog(JFrame parent) {
        super(parent, true);
        salesManager = new SalesManager();
        initComponents();
        buildUI();
    }

    private void initComponents() {
        //page = new JLabel("page 0 / 0");
        infoPanel = new InfoPanel(true);
        monthChooser = new MonthChooser();
        yearChooser = new YearChooser();
        model = new BrowseSalesModel();

        ButtonGroup buttonGroup = new ButtonGroup();
        monthly = new JRadioButton("Monthly:", true);
        buttonGroup.add(monthly);
        daily = new JRadioButton("Daily:", false);
        buttonGroup.add(daily);
        customer = new JRadioButton("Customer:", false);
        buttonGroup.add(customer);
        invoice = new JRadioButton("Invoice:", false);
        buttonGroup.add(invoice);

        dateField = new JTextField(20);
        customerField = new JTextField(15);
        invoiceField = new JTextField(15);
    }

    private JPanel createSearchPanel() {
        FormLayout layout = new FormLayout("left:p, 5dlu, left:p, 15dlu, left:p, 5dlu, left:p",
                "p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p");

        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();

        CellConstraints cc = new CellConstraints();

        int y = 1;
        builder.add(monthly, cc.xy(1, y));
        JPanel datePanel = new JPanel(new FlowLayout());
        datePanel.add(monthChooser);
        monthChooser.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                monthly.setSelected(true);
            }
        });
        datePanel.add(yearChooser);
        yearChooser.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                monthly.setSelected(true);
            }
        });
        builder.add(datePanel, cc.xy(3, y));

        y += 2;

        builder.add(daily, cc.xy(1, y));

        JPanel dailyPanel = new JPanel(new BorderLayout());
        JButton dateImage = new JButton(Utility.readImageIcon("calendar.gif"));
        dateImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                daily.setSelected(true);
                JDialog dialog = new JDialog(BrowseSalesDialog.this, true);
                JCalendar calendar = new JCalendar();
                calendar.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals("calendar")) {
                            Calendar calendar = (Calendar) evt.getNewValue();
                            dateField.setText(Utility.formatDate(calendar.getTime()));
                        }
                    }
                });
                dialog.getContentPane().add(calendar);
                dialog.pack();
                Dimension paneSize = dialog.getSize();
                Dimension screenSize = dialog.getToolkit().getScreenSize();
                dialog.setLocation((screenSize.width - paneSize.width) / 2, (screenSize.height - paneSize.height) / 2);
                dialog.setVisible(true);

            }
        });

        dailyPanel.add(dateField, BorderLayout.CENTER);
        dateField.setText(Utility.formatDate(new Date()));
        dateField.setEditable(false);
        dailyPanel.add(dateImage, BorderLayout.EAST);
        builder.add(dailyPanel, cc.xy(3, y));

        y = 1;

        builder.add(customer, cc.xy(5, y));
        builder.add(customerField, cc.xy(7, y));
        customerField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                customer.setSelected(true);
            }
        });

        y += 2;

        builder.add(invoice, cc.xy(5, y));
        builder.add(invoiceField, cc.xy(7, y));
        invoiceField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                invoice.setSelected(true);
            }
        });

        y += 2;

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });
        builder.add(searchButton, cc.xy(1, y));
        getRootPane().setDefaultButton(searchButton);

        return builder.getPanel();
    }

    private void buildUI() {
        JPanel datePanel = createSearchPanel();


        getContentPane().setLayout(new BorderLayout());
        /*
        monthChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateData();
            }
        });

        yearChooser.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateData();
            }
        });


        /*
        try {
            model = new BrowseSalesModel(salesManager.getSales(monthChooser.getMonth(), yearChooser.getYear()));
        } catch (ShawException se) {
            JOptionPane.showMessageDialog(BrowseSalesDialog.this,
                    "Error getting list.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            model = new BrowseSalesModel(new ArrayList());
            se.printStackTrace();
        }
        */
        final JTable table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumn col = table.getColumnModel().getColumn(0);
        col.setPreferredWidth(100);



        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(datePanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        /*
        JPanel pagingPanel = new JPanel(new BorderLayout());
        JButton prev = new JButton("< prev");
        prev.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.prev();
                int totalPage = model.getPageCount();
                int current = model.getPageOffset();

                page.setText("page " + (current + 1) + " / " + totalPage);
                bottomPanel.remove(infoPanel);
                bottomPanel.updateUI();
            }
        });

        JButton next = new JButton("next >");
        next.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.next();
                int totalPage = model.getPageCount();
                int current = model.getPageOffset();

                page.setText("page " + (current + 1) + " / " + totalPage);

                bottomPanel.remove(infoPanel);
                bottomPanel.updateUI();
            }
        });

        int totalPage = model.getPageCount();
        int current = model.getPageOffset();

        if (totalPage != 0)
            page.setText("page " + (current + 1) + " / " + totalPage);
        else
            page.setText("page 0 / 0");

        pagingPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pagingPanel.add(prev, BorderLayout.WEST);
        pagingPanel.add(next, BorderLayout.EAST);
        pagingPanel.add(page, BorderLayout.CENTER);
        page.setHorizontalAlignment(JLabel.CENTER);
        centerPanel.add(pagingPanel, BorderLayout.SOUTH);
        */

        getContentPane().add(centerPanel, BorderLayout.CENTER);

        bottomPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton open = new JButton("Open");
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    infoPanel.setErrorText("No sales selected");
                    bottomPanel.add(infoPanel, BorderLayout.CENTER);
                    bottomPanel.updateUI();
                } else {
                    selectedId = model.salesId(selectedRow);
                    BrowseSalesDialog.this.dispose();
                }
            }
        });


        buttonPanel.add(open);
        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedId = -1;
                BrowseSalesDialog.this.dispose();
            }
        });
        buttonPanel.add(close);

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JLabel labelCost = new JLabel("Total Cost:");
        totalCost = new JTextField(10);
        totalCost.setText(Utility.formatDouble(model.getTotalCost()));
        totalCost.setEditable(false);
        totalCost.setHorizontalAlignment(JTextField.RIGHT);

        totalPanel.add(labelCost);
        totalPanel.add(totalCost);

        JLabel totalLabel = new JLabel("Total Sales:");
        totalSales = new JTextField(10);
        totalSales.setText(Utility.formatDouble(model.getTotalSales()));
        totalSales.setEditable(false);
        totalSales.setHorizontalAlignment(JTextField.RIGHT);

        totalPanel.add(totalLabel);
        totalPanel.add(totalSales);

        bottomPanel.add(totalPanel, BorderLayout.EAST);
        bottomPanel.add(buttonPanel, BorderLayout.WEST);

        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Point p = e.getPoint();
                    int row = table.rowAtPoint(p);
                    selectedId = model.salesId(row);
                    BrowseSalesDialog.this.dispose();

                }
            }
        });


        pack();
        Dimension screenSize = getToolkit().getScreenSize();

        int height = (int) (screenSize.getHeight() * .70);
        int width = (int) (screenSize.getWidth() * .60);
        setTitle("Browse Sales Report");
        setSize(width, height);
        locateOnScreen(this);
        setVisible(true);
    }

    /**
     * Locates the given component on the screen's center.
     */
    protected void locateOnScreen(Component component) {
        Dimension paneSize = component.getSize();
        Dimension screenSize = component.getToolkit().getScreenSize();
        component.setLocation((screenSize.width - paneSize.width) / 2,
                (screenSize.height - paneSize.height) / 2);
    }

    public int getSelectedId() {
        return selectedId;
    }

    private void search() {
        java.util.List data = new ArrayList();
        try {
            if (monthly.isSelected()) {
                data = salesManager.getSales(monthChooser.getMonth(), yearChooser.getYear());
            } else if (daily.isSelected()) {
                Date date = Utility.parseDate(dateField.getText());
                data = salesManager.getSales(date);
            } else if (customer.isSelected()) {
                data = salesManager.getSalesByCustomer(customerField.getText());
            } else if (invoice.isSelected()) {
                data = salesManager.getSalesByInvoice(invoiceField.getText());
            }
            updateData(data);
        } catch(ParseException pe) {
            JOptionPane.showMessageDialog(BrowseSalesDialog.this,
                    "Error parsing date.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (ShawException se) {
            JOptionPane.showMessageDialog(BrowseSalesDialog.this,
                    "Error getting list.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            se.printStackTrace();
        }
    }

    private void updateData(java.util.List data) {
        /*
        java.util.List data = new ArrayList();
        try {
            data = salesManager.getSales(monthChooser.getMonth(), yearChooser.getYear());
        } catch (ShawException se) {
            JOptionPane.showMessageDialog(BrowseSalesDialog.this,
                    "Error getting list.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            model = new BrowseSalesModel();
            se.printStackTrace();

        }
        */
        model.setData(data);
        /*
        int totalPage = model.getPageCount();
        int current = model.getPageOffset();

        if (totalPage != 0)
            page.setText("page " + (current + 1) + " / " + totalPage);
        else
            page.setText("page 0 / 0");
            */

        totalSales.setText(Utility.formatDouble(model.getTotalSales()));
        totalCost.setText(Utility.formatDouble(model.getTotalCost()));
    }

    class YearChooser extends JSpinner {
        private SpinnerModel model;

        public YearChooser() {
            super();


            Calendar calendar = Calendar.getInstance();
            int min = calendar.getMinimum(Calendar.YEAR);
            int max = calendar.getMaximum(Calendar.YEAR);
            int step = 1;
            int initValue = calendar.get(Calendar.YEAR);
            model = new SpinnerNumberModel(initValue, min, max, step);

            setModel(model);

            JFormattedTextField tf = ((JSpinner.DefaultEditor) getEditor()).getTextField();
            tf.setEditable(false);
            tf.setBackground(Color.white);
            DefaultFormatterFactory factory = (DefaultFormatterFactory) tf.getFormatterFactory();
            NumberFormatter formatter = (NumberFormatter) factory.getDefaultFormatter();
            formatter.setFormat(new DecimalFormat("#"));
            tf.setValue(new Integer(initValue));
        }

        public int getYear() {
            return ((Integer) model.getValue()).intValue();
        }
    }

    class MonthChooser extends JComboBox {
        public MonthChooser() {
            super();
            initNames();
        }

        private void initNames() {

            DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
            String[] monthNames = dateFormatSymbols.getMonths();

            if (getItemCount() == 12) {
                removeAllItems();
            }
            for (int i = 0; i < 12; i++) {
                addItem(monthNames[i]);
            }

            this.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
        }

        public int getMonth() {
            return getSelectedIndex() + 1;
        }
    }
}
