package com.dnanh01.backend.controller;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.dnanh01.backend.exception.OrderException;
import com.dnanh01.backend.exception.UserException;
import com.dnanh01.backend.model.Order;
import com.dnanh01.backend.repository.OrderRepository;
import com.dnanh01.backend.request.PaymentRequest;

import com.dnanh01.backend.response.PaymentSubmitResponse;
import com.dnanh01.backend.service.OrderService;
import com.dnanh01.backend.service.VNPayService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/submitOrder")
    @ResponseBody
    public ResponseEntity<PaymentSubmitResponse> submitOrder(
            HttpServletRequest request,
            @RequestHeader("Authorization") String jwt,
            @RequestBody PaymentRequest req)
            throws UserException {

        Long currentOrderId = req.getCurrentOrderId();

        Optional<Order> opt = orderRepository.findById(currentOrderId);

        Integer totalDiscountedPrice = 0;

        if (opt.isPresent()) {
            totalDiscountedPrice = opt.get().getTotalDiscountedPrice();
        }

        BigDecimal total = new BigDecimal(totalDiscountedPrice);
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(total, baseUrl);

        PaymentSubmitResponse paymentSubmitResponse = new PaymentSubmitResponse(vnpayUrl, jwt, currentOrderId);

        return ResponseEntity.status(HttpStatus.OK).body(paymentSubmitResponse);
    }
    @GetMapping("/vnpay-payment")
    public RedirectView getMapping(HttpServletRequest request) {
        int paymentStatus = vnPayService.orderReturn(request);
        String redirectUrl = (paymentStatus == 1) ? "http://localhost:3000/pay?step=3&result=success" : "http://localhost:3000/pay?step=3&result=failure";
        return new RedirectView(redirectUrl);
    }
    
    @PostMapping("/confirmOrder")
    @ResponseBody
    public ResponseEntity<?> confirmOrder(
            HttpServletRequest request,
            @RequestBody PaymentRequest req)
            throws OrderException {

        Long currentOrderId = req.getCurrentOrderId();
        Order order = orderService.pendingOrder(currentOrderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
    
    @PostMapping("/mobileVnpay")
    @ResponseBody
    public ResponseEntity<PaymentSubmitResponse> MobileVnpay(
            HttpServletRequest request,
            @RequestHeader("Authorization") String jwt,
            @RequestBody PaymentRequest req)
            throws UserException, OrderException {

        Long currentOrderId = req.getCurrentOrderId();
        
        Order order = orderService.pendingOrder(currentOrderId);
        
        Optional<Order> opt = orderRepository.findById(currentOrderId);

        Integer totalDiscountedPrice = 0;

        if (opt.isPresent()) {
            totalDiscountedPrice = opt.get().getTotalDiscountedPrice();
        }

        BigDecimal total = new BigDecimal(totalDiscountedPrice);
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(total, baseUrl);

        PaymentSubmitResponse paymentSubmitResponse = new PaymentSubmitResponse(vnpayUrl, jwt, currentOrderId);

        return ResponseEntity.status(HttpStatus.OK).body(paymentSubmitResponse);
    }
    
}
