package com.grocery.management.Method;

import com.grocery.management.Model.InvoiceBean;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

public class GenerateReport {

    public void generate( Collection<InvoiceBean> personBeans){
        File file = new File("D:\\Report\\MyReports\\invoice.jrxml");
        HashMap<String, Object> map = new HashMap<>();
        map.put("pk", "Pradum");

        try {

            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource jrbean = new JRBeanCollectionDataSource(personBeans);
            JasperPrint print = JasperFillManager.fillReport(jasperReport, map, jrbean);
            JasperExportManager.exportReportToPdfFile(print, "D:\\Report\\MyReports\\rr.pdf");

            JasperViewer.viewReport(print);
            System.out.println("successful");

        } catch (JRException e) {
            e.printStackTrace();
        }

    }
}
