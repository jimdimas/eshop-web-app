package com.jimdimas.api.email;

import com.jimdimas.api.order.Order;
import com.jimdimas.api.order.OrderSingleProduct;
import com.jimdimas.api.product.Product;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationEmailService {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String from;

    private void sendEmail(String recipient,String subject,String content) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,"utf-8");
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setTo(recipient);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content,true);

        javaMailSender.send(mimeMessage);
    }

    public void sendVerificationMail(String recipient,String verificationToken) throws MessagingException {
        String verificationLink="http://localhost:8080/api/v1/auth/verifyEmail?email="+recipient+"&verificationToken="+verificationToken;
        String content = "<p>Hello,this mail was sent by the E-Shop.</p><br>" +
                "<p>Click <a href=\""+verificationLink+"\">here</a> to verify your email.</p>";
        sendEmail(recipient,"E-Shop Email Verification",content);
    }

    public void sendChangePasswordMail(String recipient, String passwordToken) throws MessagingException {
        String changePasswordLink="http://localhost:8080/api/v1/auth/changePassword?email="+recipient+"&passwordToken="+passwordToken;
        String content = "<p>Hello,this mail was sent by the E-Shop.</p><br>" +
                "<p>Click <a href=\""+changePasswordLink+"\">here</a> to reset your password.</p>";
        sendEmail(recipient,"E-Shop Password Reset",content);
    }

    public void sendOrderVerificationMail(String recipient, Order order) throws MessagingException {
        String verifyOrderLink="http://localhost:8080/api/v1/order/verifyOrder?orderId="+order.getOrderId().toString()+"&email="+recipient+"&token="+order.getVerificationToken();
        StringBuilder orderContents= new StringBuilder();
        orderContents.append("<h3>Here is your cart: </h3><br>");
        for (OrderSingleProduct cartProduct:order.getCartProducts()){
            Product actualProduct = cartProduct.getProduct();
            orderContents.append("<p> Product: "+actualProduct.getName()+", Price: "+actualProduct.getPrice()+", Quantity: "+cartProduct.getQuantity()+"</p>");
        }
        orderContents.append("<p> Total price: "+order.getTotalPrice().toString()+"</p>");
        String content = "<p>Hello,this mail was sent by the E-Shop regarding order with id: "+order.getOrderId().toString()+".</p><br>"+
                orderContents.toString()+
                "<p>Click <a href=\""+verifyOrderLink+"\">here</a> to verify your order.</p>";
        sendEmail(recipient,"E-Shop Order Verification",content);
    }
}
