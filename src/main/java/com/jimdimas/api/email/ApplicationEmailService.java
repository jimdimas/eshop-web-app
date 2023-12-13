package com.jimdimas.api.email;

import com.jimdimas.api.order.Order;
import com.jimdimas.api.order.OrderSingleProduct;
import com.jimdimas.api.product.Product;
import com.jimdimas.api.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationEmailService {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String from;
    @Value("${spring.properties.host}")
    private String host;

    private void sendEmail(String recipient,String subject,String content) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,"utf-8");
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setTo(recipient);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content,true);

        javaMailSender.send(mimeMessage);
    }

    public void sendVerificationMail(User recipient, String verificationToken) throws MessagingException {
        String verificationLink="http://"+this.host+"/api/v1/auth/verifyEmail?user="+recipient.getUsername()+"&verificationToken="+verificationToken;
        String content = "<p>Hello,this mail was sent by the E-Shop.</p><br>" +
                "<p>Click <a href=\""+verificationLink+"\">here</a> to verify your email.</p>";
        sendEmail(recipient.getEmail(),"E-Shop Email Verification",content);
    }

    public void sendChangePasswordMail(User recipient, String passwordToken) throws MessagingException {
        String changePasswordLink="http://"+this.host+"/api/v1/auth/changePassword?user="+recipient.getUsername()+"&passwordToken="+passwordToken;
        String content = "<p>Hello,this mail was sent by the E-Shop.</p><br>" +
                "<p>Click <a href=\""+changePasswordLink+"\">here</a> to reset your password.</p>";
        sendEmail(recipient.getEmail(),"E-Shop Password Reset",content);
    }

    public void sendOrderVerificationMail(User recipient, Order order) throws MessagingException {
        String verifyOrderLink="http://"+this.host+"/api/v1/order/verifyOrder?orderId="+order.getOrderId().toString()+"&user="+recipient.getUsername()+"&token="+order.getVerificationToken();
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
        sendEmail(recipient.getEmail(),"E-Shop Order Verification",content);
    }

    public void sendOrderUpdateVerificationMail(User recipient, Order updatedOrder) throws MessagingException {
        String verifyOrderLink="http://"+this.host+"/api/v1/order/verifyOrder?orderId="+updatedOrder.getOrderId().toString()+"&user="+recipient.getUsername()+"&token="+updatedOrder.getVerificationToken();
        StringBuilder orderContents= new StringBuilder();
        orderContents.append("<h3>Here is your new cart: </h3><br>");
        for (OrderSingleProduct cartProduct:updatedOrder.getCartProducts()){
            Product actualProduct = cartProduct.getProduct();
            orderContents.append("<p> Product: "+actualProduct.getName()+", Price: "+actualProduct.getPrice()+", Quantity: "+cartProduct.getQuantity()+"</p>");
        }
        orderContents.append("<p> Total price: "+updatedOrder.getTotalPrice().toString()+"</p>");
        String content = "<p>Hello,this mail was sent by the E-Shop regarding the update of your order with id: "+updatedOrder.getOrderId().toString()+".</p><br>"+
                orderContents.toString()+
                "<p>Click <a href=\""+verifyOrderLink+"\">here</a> to verify your updates.</p>";
        sendEmail(recipient.getEmail(),"E-Shop Order Update Verification",content);
    }
}
