package de.ityx.sky.outbound.common;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.nttdata.de.sky.archive.BaseUtils;
import com.nttdata.de.sky.pdf.PdfTemplate;

import java.io.File;
import java.io.FileOutputStream;
/**
 * Created by meinusch on 26.03.15.
 */
public class PdfUtils {


	public static PdfTemplate createEmptyTemplate() throws Exception {
		PdfTemplate template = new PdfTemplate();
		File file = null;
		try {
			file = File.createTempFile("empty", ".pdf");
			FileOutputStream fos = new FileOutputStream(file);

			//final Document document = new Document(PageSize.A4, 36, 72, 50, 160);
			final Document document = new Document(PageSize.A4, 20, 40, 60, 80);
			PdfWriter writer = PdfWriter.getInstance(document, fos);
			document.open();
			document.add(new Paragraph(" "));
			document.close();
			template.setPdf(BaseUtils.readFile(file));
		}
		finally {
			if (file != null) {
				file.delete();
			}
		}
		return template;
	}
}
