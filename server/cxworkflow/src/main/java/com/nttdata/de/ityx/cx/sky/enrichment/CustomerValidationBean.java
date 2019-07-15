package com.nttdata.de.ityx.cx.sky.enrichment;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.contex.interfaces.document.StreamedDocument;
import de.ityx.contex.interfaces.extag.TagMatch;

import java.util.Arrays;
import java.util.List;

public class CustomerValidationBean implements
		de.ityx.contex.interfaces.extag.Validator {
	private List<Integer> weights = Arrays.asList(11, 13, 16, 21, 28, 41, 58, 81);

	public int validateCustomer(String number) {
		if (number.length() == 10 && number.matches("\\d{10}")) {
			SkyLogger.getItyxLogger().debug("INPUT: " + number);
			int checksum = Integer.parseInt(number.substring(0, 2));
			SkyLogger.getItyxLogger().debug("CHECKSUM: " + checksum);
			if (checksum > 10) {
				String sequential = number.substring(2);
				SkyLogger.getItyxLogger().debug("SEQUENTIAL: " + sequential);
				int calculatedChecksum = calculateChecksum(sequential);
				if (checksum == calculatedChecksum) {
					if (Integer.parseInt(sequential.substring(0, 1)) > 3) {
						return 2;
					}
					return 1;
				}
				SkyLogger.getItyxLogger().error("WRONG CHECKSUM: " + calculatedChecksum);
			}
		}
		return 0;
	}

	private int calculateChecksum(String sequential) {
		int checksum = 0;
		for (int i = 0; i < sequential.length(); i++) {
			checksum += Integer.parseInt(sequential.substring(i, i + 1))
					* weights.get(i);
		}
		return (checksum % 89) + 11;
	}

	public List<Integer> getWeights() {
		return weights;
	}

	public void setWeights(List<Integer> weights) {
		this.weights = weights;
	}


	@Override
	public boolean isValid(StreamedDocument document,TagMatch tm) throws Exception {
		return validateCustomer(tm.getTagValue()) > 0;
	}
}
