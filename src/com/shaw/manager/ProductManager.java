package com.shaw.manager;

import com.shaw.dao.ProductDAO;
import com.shaw.ShawException;
import com.shaw.bean.ProductBean;
import com.shaw.db.ShawDbConnectionException;

import java.util.List;

public class ProductManager {
    private ProductDAO productDAO;

    public ProductManager() {
        productDAO = new ProductDAO();
    }

    public List getProducts() throws ShawException {
        try {
            return productDAO.getProducts();
        } catch(ShawDbConnectionException se) {
            throw new ShawException(se.getMessage());
        }
    }

    public ProductBean getProduct(String partNumber) throws ShawException {
        try {
            return productDAO.getProduct(partNumber);
        } catch(ShawDbConnectionException se) {
            throw new ShawException(se.getMessage());
        }
    }

    public void addProduct(ProductBean product) throws ShawException {
        try {
            productDAO.addProduct(product);
        } catch(ShawDbConnectionException se) {
            throw new ShawException(se.getMessage());
        }
    }

    public void deleteProduct(String partNumber) throws ShawException {
        try {
            productDAO.deleteProduct(partNumber);
        } catch(ShawDbConnectionException se) {
            throw new ShawException(se.getMessage());
        }
    }

    public void updateProduct(String partNumber, ProductBean product) throws ShawException {
        try {
            productDAO.updateProduct(partNumber, product);
        } catch(ShawDbConnectionException se) {
            throw new ShawException(se.getMessage());
        }
    }

    public void updateComments(String partNumber, String comments) throws ShawException {
        try {
            productDAO.updateComments(partNumber, comments);
        } catch(ShawDbConnectionException se) {
            throw new ShawException(se.getMessage());
        }
    }

    public List searchProduct(String criteria) throws ShawException {
        try {
            return productDAO.searchProducts(criteria);
        } catch(ShawDbConnectionException se) {
            throw new ShawException(se.getMessage());
        }
    }
}
