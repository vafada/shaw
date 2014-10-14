package com.shaw.manager;

import com.shaw.ShawException;
import com.shaw.Utility;
import com.shaw.db.ShawDbConnectionException;
import com.shaw.dao.PartsInputDAO;
import com.shaw.bean.PartsInputBean;
import com.shaw.bean.PartsBean;
import com.shaw.bean.DateRangeWrapper;
import com.shaw.bean.InventoryReportBean;
import com.shaw.bean.ProductBean;

import java.util.Iterator;

public class PartsInputManager {
    private PartsInputDAO partsInputDAO;

    public PartsInputManager() {
        partsInputDAO = new PartsInputDAO();
    }

    public void addPartsInput(PartsInputBean partInput) throws ShawException {
        InventoryReportManager reportManager = new InventoryReportManager();
        ProductManager productManager = new ProductManager();

        try {
            Iterator iter = partInput.getPartsList().iterator();
            while(iter.hasNext()) {
                PartsBean parts = (PartsBean) iter.next();
                ProductBean product = productManager.getProduct(parts.getPartNumber());
                double unitPrice = product.getUnitPrice();
                if(product.getUnitPrice() != 0) {
                    unitPrice = ((unitPrice + parts.getUnitPrice()) / 2);
                    parts.setUnitPrice(unitPrice);
                }
            }


            partsInputDAO.addPartsInput(partInput);

            Iterator partsListIter = partInput.getPartsList().iterator();
            while(partsListIter.hasNext()) {
                PartsBean part = (PartsBean) partsListIter.next();
                String partNumber = part.getPartNumber();

                DateRangeWrapper dateRange = Utility.getLastSevenMonths();

                InventoryReportBean reportBean = reportManager.getInventoryReportOfProduct(partNumber, dateRange.getStartDate(), dateRange.getEndDate());

                if("".equalsIgnoreCase(reportBean.getProduct().getComments()) == false && reportBean.isMAD() == false) {
                    productManager.updateComments(partNumber, "");
                }
            }
        } catch(ShawDbConnectionException sdbe) {
            throw new ShawException(sdbe.getMessage());
        }
    }   
}
