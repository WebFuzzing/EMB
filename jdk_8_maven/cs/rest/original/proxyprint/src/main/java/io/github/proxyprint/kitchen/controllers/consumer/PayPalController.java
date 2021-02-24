package io.github.proxyprint.kitchen.controllers.consumer;

import com.google.gson.Gson;
import com.paypal.core.LoggingManager;
import com.paypal.ipn.IPNMessage;
import io.github.proxyprint.kitchen.Configuration;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.notifications.Notification;
import io.github.proxyprint.kitchen.models.repositories.*;
import io.github.proxyprint.kitchen.utils.NotificationManager;
import io.github.proxyprint.kitchen.utils.PayPalWrapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by daniel on 20-05-2016.
 */
@RestController 
@Transactional
public class PayPalController {
    @Autowired
    private UserDAO users;
    @Autowired
    private ConsumerDAO consumers;
    @Autowired
    private EmployeeDAO employees;
    @Autowired
    private PrintRequestDAO printRequests;
    @Autowired
    private PrintShopDAO printShops;
    @Autowired
    private ManagerDAO managers;
    @Autowired
    private Gson GSON;
    @Autowired
    private NotificationManager notificationManager;

    @ApiOperation(value = "Returns nothing", notes = "This method implements the payment check mechanism given by PayPal. This method acts as callback, it reacts to the change of status of a transaction to Completed (eCheck - complete).")
    @RequestMapping(value = "/paypal/ipn/{printRequestID}", method = RequestMethod.POST)
    protected void someRequestPaymentConfirmationFromPaylPal(@PathVariable(value = "printRequestID") long prid, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String,String> configurationMap =  Configuration.getConfig();
        IPNMessage ipnlistener = new IPNMessage(request,configurationMap);
        boolean isIpnVerified = ipnlistener.validate();
        String transactionType = ipnlistener.getTransactionType();
        Map<String,String> map = ipnlistener.getIpnMap();

        // Parse content of interest from the IPN notification JSON Body -- :D
        String payerEmail = map.get("payer_email");
        Double quantity = Double.valueOf(map.get("mc_gross"));
        String paymentStatus = map.get("payment_status");
        String transactionID = map.get("txn_id");


        if(payerEmail!=null && quantity!=null && paymentStatus!=null) {
            PrintRequest pr = printRequests.findOne(prid);

            if (pr != null) {
                LoggingManager.info(this.getClass(), "******* IPN (name:value) pair : " + map + "  " +
                        "######### TransactionType : " + transactionType + "  ======== IPN verified : " + isIpnVerified);

                Consumer c = pr.getConsumer();

                // Divine Condition for secure request background check
                if(c!=null && c.getPrintRequests().contains(pr) && pr.getCost()==quantity && paymentStatus.equals(PayPalWrapper.PAYPAL_COMPLETED_PAYMENT)) {
                    // The print request may now go to the printshop
                    pr.setStatus(PrintRequest.Status.PENDING);
                    pr.setPayPalSaleID(transactionID);
                    printRequests.save(pr);
                    String message = "O pagamento via PayPal do seu pedido nº #"+pr.getId()+" foi confirmado. Obrigado!";
                    notificationManager.sendNotification(c.getUsername(), new Notification(message));
                    return;
                }
            } else {
                LoggingManager.info(this.getClass(), "PayPal transaction ERROR: consumer with email <" + payerEmail + "> is not registred in ProxyPrint.");
                return;
            }
        } else {
            LoggingManager.info(this.getClass(), "PayPal transaction ERROR: bad IPN JSON body for values payerEmail, quantity, paymentStatus, paymentData");
        }
    }

    @ApiOperation(value = "It confirms that a certain consumer has pay its load up on ProxyPrint.", notes = "Its a route for being remotely called by PayPal servers.")
    @RequestMapping(value = "paypal/ipn/consumer/{consumerID}", method = RequestMethod.POST)
    protected void consumerLoadUpConfirmation(@PathVariable(value = "consumerID") long cid, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String,String> configurationMap =  Configuration.getConfig();
        IPNMessage ipnlistener = new IPNMessage(request,configurationMap);
        boolean isIpnVerified = ipnlistener.validate();
        String transactionType = ipnlistener.getTransactionType();
        Map<String,String> map = ipnlistener.getIpnMap();


        String payerEmail = map.get("payer_email");
        Double quantity = Double.valueOf(map.get("mc_gross"));
        String paymentStatus = map.get("payment_status");
        Consumer consumer = consumers.findOne(cid);

        if(consumer!=null) {
            if(paymentStatus.equals(PayPalWrapper.PAYPAL_COMPLETED_PAYMENT)) {
                LoggingManager.info(this.getClass(), "******* IPN (name:value) pair : " + map + "  Load Balance Confirmation for Consumer #"+consumer.getId());

                // Payment is completed
                consumer.getBalance().addDoubleQuantity(quantity);

                // Send notification to user informing its balance was updated
                String message = "O seu carregamento de "+quantity+" € via PayPal foi confirmado. Obrigado!";
                notificationManager.sendNotification(consumer.getUsername(), new Notification(message));
                return;
            }
            // Send notification to user informing something went wrong
            String message = "O seu carregamento de "+quantity+" € não pode ser confirmado. Por favor contacte proxyprint.pt@gmail.com indicando o seu username e quantia para que lhe possámos devolver o dinheiro.";
            notificationManager.sendNotification(consumer.getUsername(), new Notification(message));
        }
    }

}
