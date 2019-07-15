package com.nttdata.de.sky.archive;

import java.io.Serializable;

/**
 * Creates the white space for the paper page.( 4.6.1.2 Create PDF for the letter shop)
 *
 */
public class CustomLine implements Serializable{

    private static final long serialVersionUID = -5791085894022961227L;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public static enum LANGUAGE {
        DE, AT
    }

    private final static String CONST_FIRST    = "$";
    private final static String CONST_BORDER   = "||";

    private String              genJob         = "";
    private final String        layout4        = "1001";
    private final String        variation      = "0";
    private final String        variationInfo  = "ITYX";
    private final String        variationCount = "1";
    private LANGUAGE            countryCode    = LANGUAGE.DE;
    private String              customerId     = "";
    private String              pageNumber     = "";
    private String              totalPages     = "";
    private String              ksPorto        = "";
    private final String        form           = "";
    private String              attachment1    = "";
    private String              attachment2    = "";
    private String              attachment3    = "";
    private String              attachment4    = "";

    private String              zipCode        = "";
    private String              company        = "";

    /**
     * Constructor
     */
    public CustomLine() {
    }

    /**
     * Gets the document id.
     *
     * @return
     */
    public String getGenJob() {
        return genJob;
    }

    /**
     * Sets the document id.
     *
     * @param genJob
     */
    public void setGenJob(String genJob) {
        this.genJob = genJob;
    }

    /**
     * fixed
     *
     * @return
     */
    public String getVariationInfo() {
        return variationInfo;
    }

    /**
     * fixed
     *
     * @return
     */
    public String getVariationCount() {
        return variationCount;
    }

    /**
     * Gets the country code.
     *
     * DE or AT
     *
     * @return
     */
    public LANGUAGE getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the country code.
     * DE or AT
     *
     * @param countryCode
     * @throws Exception
     */
    public void setCountryCode(LANGUAGE countryCode) throws Exception {
        this.countryCode = countryCode;
    }

    /**
     * Gets the customer id.
     *
     * @return
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customer id.
     *
     * @param customerId
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Gets the current page number.
     *
     * @return
     */
    public String getPageNumber() {
        return pageNumber;
    }

    /**
     * Sets the current page number.
     *
     * @param pageNumber
     */
    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Gets the page count.
     *
     * @return
     */
    public String getTotalPages() {
        return totalPages;
    }

    /**
     * Sets the page count.
     *
     * @param totalPages
     */
    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * Gets the dispatch code.
     *
     * @return
     */
    public String getKsPorto() {
        return ksPorto;
    }

    /**
     * Sets the dispatch code.
     *
     * @param ksPorto
     */
    public void setKsPorto(String ksPorto) {
        this.ksPorto = ksPorto;
    }

    /**
     * fixed
     *
     * @return
     */
    public String getForm() {
        return form;
    }

    /**
     * Gets the attachment code 1.
     *
     * @return
     */
    public String getAttachment1() {
        return attachment1;
    }

    /**
     * Sets the attachment code 1.
     *
     * @param attachment
     */
    public void setAttachment1(String attachment) {
        this.attachment1 = attachment;
    }

    /**
     * Gets the attachment code 2.
     *
     * @return
     */
    public String getAttachment2() {
        return attachment2;
    }

    /**
     * Sets the attachment code 2.
     *
     * @param attachment
     */
    public void setAttachment2(String attachment) {
        this.attachment2 = attachment;
    }

    /**
     * Gets the attachment code 3.
     *
     * @return
     */
    public String getAttachment3() {
        return attachment3;
    }

    /**
     * Sets the attachment code 3.
     *
     * @param attachment
     */
    public void setAttachment3(String attachment) {
        this.attachment3 = attachment;
    }

    /**
     * Gets the attachment code 4.
     *
     * @return
     */
    public String getAttachment4() {
        return attachment4;
    }

    /**
     * Sets the attachment code 4.
     *
     * @param attachment
     */
    public void setAttachment4(String attachment) {
        this.attachment4 = attachment;
    }

    /**
     * Gets the customer zip code.
     *
     * @return
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Sets the customer zip code.
     *
     * @param zipCode
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * fixed
     *
     * @return
     */
    public String getLayout4() {
        return layout4;
    }

    /**
     * fixed
     *
     * @return
     */
    public String getVariation() {
        return variation;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return CONST_FIRST + genJob + CONST_FIRST + getLayout4() + CONST_FIRST + variation + CONST_FIRST + variationInfo
                + CONST_FIRST + variationCount + CONST_FIRST + countryCode + CONST_FIRST + CONST_BORDER + customerId + CONST_BORDER + CONST_FIRST
                + pageNumber + CONST_FIRST + totalPages + CONST_FIRST + ksPorto + CONST_FIRST + form + CONST_FIRST + attachment1
                + CONST_FIRST + attachment2 + CONST_FIRST + attachment3 + CONST_FIRST + attachment4;
    }
}
