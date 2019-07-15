/**
 * 
 */
package com.nttdata.de.sky.ityx.common;

/**
 * Digits 1-11 will be a unique number (for the scan service provider). Each
 * provider has to make sure to generate unique numbers for each barcode. This
 * can be achieved by using incrementing numbers. Provider: Digits 12 and 13
 * will identify the scan service provider. Each scan service provider will have
 * a unique value. This prevents different scan service providers from
 * generating the same barcodes. Meta: Digits 14 and 15 will be used to carry
 * information about the document. At the moment the only information is
 * wheather or not a smartcard is enclosed with the document. 0 0 : A smartcard
 * is not enclosed 0 1 : A smartcard is enclosed (This allows further attributes
 * to be added in the future by using a binary encoding.) Reserved: The digit is
 * reserved and not used in the current implementation.
 * 11111111111 11 11
 * 
 * @author DHIFLM
 * 
 */
public enum SMCTypes {
	SMCTYPE_00_OHNE_SMC_HARDWARE, SMCTYPE_01_MIT_SMARTCARD, SMCTYPE_02_MIT_HARDWARE, SMCTYPE_03_MIT_SMC_HARDWARE, SMCTYPE_04_MIT_ORIGINALDOKUMENT, SMCTYPE_05_MIT_BARGELD, SMCTYPE_06_MIT_SCHECK, SMCTYPE_07_TV_DIGITAL, SMCTYPE_08_BETRUG, SMCTYPE_09_BSD;

	@Override
	public String toString() {
		switch (this) {
		case SMCTYPE_00_OHNE_SMC_HARDWARE:
			return "ohne SMC/Hardware";
		case SMCTYPE_01_MIT_SMARTCARD:
			return "Smartcard";
		case SMCTYPE_02_MIT_HARDWARE:
			return "Hardware";
		case SMCTYPE_03_MIT_SMC_HARDWARE:
			return "SMC und Hardware";
		case SMCTYPE_04_MIT_ORIGINALDOKUMENT:
			return "Originaldokument";
		case SMCTYPE_05_MIT_BARGELD:
			return "Bargeld";
		case SMCTYPE_06_MIT_SCHECK:
			return "Scheck";
		case SMCTYPE_07_TV_DIGITAL:
			return "TV Digital";
		case SMCTYPE_08_BETRUG:
			return "Betrugsverdacht";
		case SMCTYPE_09_BSD:
			return "Rückläufer BSD";
		default:
			break;
		}
		return "Unbekannt";
	}

	public static int max() {
		return 9;
	}
}
