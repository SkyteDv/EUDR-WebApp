package com.projects.eudrwebapp.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.projects.eudrwebapp.model.Order;
import org.springframework.stereotype.Service;
import com.lowagie.text.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

@Service
public class PDFService {

    public byte[] generateOrderPDFWithQRCode(Order order, BufferedImage qrImage) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            String logoPath = "src/main/resources/static/images/logo_for_slides.png";
            Image logo = Image.getInstance(logoPath);
            logo.scaleToFit(100, 100);  // Adjust size if needed
            logo.setAlignment(Element.ALIGN_RIGHT);
            document.add(logo);

            Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD);
            Font subTitleFont = new Font(Font.HELVETICA, 15, Font.BOLD);

            document.add(new Paragraph("Delivery Note", titleFont));
            document.add(new Paragraph("Order Number: " + order.getErpReferenceNumber()));

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Supplier Information", subTitleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Supplier Name: " + order.getSupplier().getUsername()));
            document.add(new Paragraph("Location: Example Location"));

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Customer Information", subTitleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Customer Name: " + order.getCustomer().getUsername()));
            document.add(new Paragraph("Location: Example Location"));

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Product Information", subTitleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Category: " + order.getProductCategory()));
            document.add(new Paragraph("Product Name: " + order.getProductName()));
            document.add(new Paragraph("Destination Harbour: " + order.getDestination()));

            document.add(new Paragraph(" "));

            document.add(new Paragraph("Generated DDS-QR Reference:", subTitleFont));
            ByteArrayOutputStream qrOut = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", qrOut);
            Image qr = Image.getInstance(qrOut.toByteArray());
            qr.scaleToFit(150, 150);  // Resize if needed
            document.add(new Paragraph(" ")); // Add spacing
            document.add(qr);

            // Close document
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }
}
