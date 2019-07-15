package com.nttdata.de.sky.archive;

import com.nttdata.de.sky.archive.CustomLine.LANGUAGE;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import de.ityx.mediatrix.data.Customer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

public class BaseUtils {
    public static HashMap<String, Object> getParameter(int id, String headers) throws Exception {
        HashMap<String, Object> result = new HashMap<String, Object>();
        CustomLine cl = new CustomLine();
        cl.setGenJob(id + "");

        String country= TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CUSTOMER_COUNTRY);
        setHeaderData(cl, country, TagMatchDefinitions.extractXTMHeader(headers, TagMatchDefinitions.CUSTOMER_ID));
        result.put(CustomLine.class.getName(), cl);
        return result;
    }

    public static HashMap<String, Object> getParameter(int id, Customer customer) throws Exception {
       if (customer==null) {
           throw new Exception("Zugriff auf dem Kunden nicht möglich!");
       }
        HashMap<String, Object> result = new HashMap<String, Object>();
        CustomLine cl = new CustomLine();
        cl.setGenJob(id + "");

        setHeaderData(cl, customer.getCountry(), customer.getId() + "");
        result.put(CustomLine.class.getName(), cl);
        return result;
    }

    private static void setHeaderData(CustomLine cl, String country, String customerId) throws Exception {
        LANGUAGE countryCode= LANGUAGE.DE;
        if (country !=null && !country.isEmpty()){
             countryCode=getCountry(country);
        }
        cl.setCountryCode(countryCode);
        cl.setCustomerId(customerId);
    }

    private static LANGUAGE getCountry(String country) {
        if (country.toLowerCase().equals("germany")) {
            return LANGUAGE.DE;
        }
        else if (country.toLowerCase().equals("austria")) {
            return LANGUAGE.AT;
        }
        return LANGUAGE.DE;
    }
    
    public static byte[] readFile(File file) throws Exception {

        InputStream is = new FileInputStream(file);
        byte[] content = new byte[(int) file.length()];

        int offset = 0;
        int numRead = 0;
        while (offset < content.length
                && (numRead = is.read(content, offset, content.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < content.length) {
            throw new Exception("Could not completely read file " + file.getName());
        }

        is.close();
        return content;
    }

}
