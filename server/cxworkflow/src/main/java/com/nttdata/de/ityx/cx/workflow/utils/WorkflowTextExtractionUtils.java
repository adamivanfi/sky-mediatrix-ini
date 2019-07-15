package com.nttdata.de.ityx.cx.workflow.utils;

import de.ityx.contex.interfaces.document.CDocument;
import de.ityx.contex.interfaces.document.CPage;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorkflowTextExtractionUtils {

    public static boolean documentBarcodeMatchPattern(CDocument doc, String barcodePattern) {
        return documentBarcodeMatchPattern(doc, Pattern.compile(barcodePattern));
    }

    public static boolean documentBarcodeMatchPattern(CDocument doc, Pattern barcodePattern) {

        //does not work since 2.4
        if (doc.getNote("barcodes") != null) {
            for (String barcode : (List<String>) doc.getNote("barcodes")) {
                if (barcode != null && textMatchPattern(barcode, barcodePattern)) {
                   return true;
                }
            }
        }
        //does not work since 2.4
        if (doc.getNote("barcode") != null) {
             String barcode = (String) (doc.getNote("barcode"));
             if (barcode != null && textMatchPattern(barcode, barcodePattern)) {
                   return true;
             }
        }
        //still workung after 2.4
        for (CPage cpage : doc.getPages()) {
             if (pageBarcodeMatchPattern(cpage, barcodePattern)) {
                   return true;
             }
        }
        return false;
    }
    
    public static boolean pageBarcodeMatchPattern(CPage page, String barcodePattern) {
        return pageBarcodeMatchPattern(page, Pattern.compile(barcodePattern));
    }
    public static boolean pageBarcodeMatchPattern(CPage page, Pattern barcodePattern) {
         if (page.getNote("barcodes") != null) {
            for (String barcode : (List<String>) page.getNote("barcodes")) {
                if (barcode != null && textMatchPattern(barcode, barcodePattern)) {
                    return true;
                }
            }
        }
        if (page.getNote("barcode") != null) {
            String textToCheck = (String) (page.getNote("barcode"));
            return textMatchPattern(textToCheck, barcodePattern);
        }
        return false;
    }

    public static Set<Integer> getPagesMatchesBarcode(CDocument document, String barcodePattern) {
        return getPagesMatchesBarcode(document, Pattern.compile(barcodePattern));
    }

    public static Set<Integer> getPagesMatchesBarcode(CDocument document, Pattern barcodePattern) {
        Set<Integer> ret = new TreeSet<>();
        if (document != null) {
            CPage[] pages = document.getPages();
            for (CPage page : pages) {
                if (pageBarcodeMatchPattern(page, barcodePattern)) {
                    ret.add(page.getPageno());
                }
            }
        }
        return ret;
    }


    public static Set<String> getBarcodesMatchesPattern(CDocument document, Pattern barcodePattern) {
        Set<String> ret = new TreeSet<>();
        if (document != null) {
            CPage[] pages = document.getPages();
            for (CPage page : pages) {
                Set<String> barcodes= getPageBarcodesMatchPattern(page, barcodePattern);
                if (barcodes!=null && !barcodes.isEmpty()) {
                    ret.addAll(barcodes);
                }
            }
        }
        return ret;
    }


    public static Set<String> getPageBarcodesMatchPattern(CPage page, Pattern barcodePattern) {
        Set<String> ret = new TreeSet<>();
        if (page.getNote("barcodes") != null) {
            for (String barcode : (List<String>) page.getNote("barcodes")) {
                if (barcode != null && !barcode.isEmpty()){
                  Set<String> pageRes=getTextMatchPattern(barcode, barcodePattern);
                    if (pageRes!=null && !pageRes.isEmpty()){
                        ret.addAll(pageRes);
                    }
                }
            }
        }
        if (page.getNote("barcode") != null) {
            String textToCheck = (String) (page.getNote("barcode"));
            Set<String> pageRes=getTextMatchPattern(textToCheck, barcodePattern);
            if (pageRes!=null && !pageRes.isEmpty()){
                ret.addAll(pageRes);
            }
        }
        return ret;
    }


    public static Set<String> getTextMatchPattern(String textToCheck, Pattern pattern) {
        Set<String> ret = new TreeSet<>();
        if (textToCheck == null || textToCheck.trim().length() < 3) {
            return null;
        }
        Matcher matcher = pattern.matcher(textToCheck);
        while(matcher.find()){
            if (matcher.groupCount()==0 && matcher.group(0)!=null && !matcher.group(0).isEmpty()){
                ret.add(matcher.group(0)); //add the whole String
            }else{
                for(int i=1; i<=matcher.groupCount();i++){
                    if  (matcher.group(i)!=null && !matcher.group(i).isEmpty()) {
                        ret.add(matcher.group(i));
                    }
                }
            }
        }
        return ret;
    }

    public static boolean pageTextMatchPattern(CPage page, String barcodePattern) {
        return pageTextMatchPattern(page, Pattern.compile(barcodePattern));
    }

    public static boolean pageTextMatchPattern(CPage page, Pattern barcodePattern) {
        String textToCheck = page.getContentAsString();
        return textMatchPattern(textToCheck, barcodePattern);
    }

    public static Set<Integer> getPagesMatchingTextPattern(CDocument document, String barcodePattern) {
        return getPagesMatchingTextPattern(document, Pattern.compile(barcodePattern));
    }

    public static Set<Integer> getPagesMatchingTextPattern(CDocument document, Pattern barcodePattern) {
        Set<Integer> ret = new TreeSet<>();
        if (document != null) {
            CPage[] pages = document.getPages();
            for (CPage page : pages) {
                if (pageTextMatchPattern(page, barcodePattern)) {
                    ret.add(page.getPageno());
                }
            }
        }
        return ret;
    }

    public static boolean textMatchPattern(String textToCheck, Pattern pattern) {
        if (textToCheck == null || textToCheck.trim().length() < 3) {
            return false;
        }
        Matcher matcher = pattern.matcher(textToCheck);
        return matcher.find();
    }
}
