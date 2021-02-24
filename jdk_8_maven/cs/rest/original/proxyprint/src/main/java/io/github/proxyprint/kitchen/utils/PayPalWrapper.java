package io.github.proxyprint.kitchen.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import com.paypal.core.LoggingManager;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.printshops.Manager;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by daniel on 24-05-2016.
 */
public class PayPalWrapper {

    public static String PAYPAL_COMPLETED_PAYMENT = "Completed";

    @Autowired
    private Gson GSON;

    // Generate access token in this construct
    public PayPalWrapper() {
    }

    public String generatePayPalAccessToken() throws PayPalRESTException {
        JsonObject response = new JsonObject();

        // Load configurations
        String[] tmp = this.getClass().getResource("/paypal.properties").toString().split(":");
	LoggingManager.info(this.getClass(), (String)tmp[1]);
        OAuthTokenCredential tokenCredential = Payment.initConfig(new File(tmp[1]));

        // Create access token
        String accessToken = tokenCredential.getAccessToken();

        return accessToken;
    }

    /**
     * Pay to a printshop its share of a print request.
     *
     * @param prequest the print request.
     * @param manager  the manager of the printshop.
     * @param pshop    the prinshop where the prequest was sent.
     * @return true/false pending on paypal operation success/insuccess.
     * @throws PayPalRESTException
     */
    public boolean payShareToPrintShop(PrintRequest prequest, Manager manager, PrintShop pshop) throws PayPalRESTException {
        if (prequest != null && manager != null && pshop != null) {
            Payout payout = new Payout();
            PayoutBatch batch = null;

            // Get an access token
            String accessToken = generatePayPalAccessToken();
            PayoutSenderBatchHeader senderBatchHeader = new PayoutSenderBatchHeader();

            // Batch
            Random random = new Random();
            String batchId = prequest.getArrivalTimestamp().toString();
            String emailSubject = "ProxyPrint - Pagamento relativo ao pedido ";
            // UNMARK getFinishedTimestamp() on full integration
            emailSubject += prequest.getArrivalTimestamp().toString() + "::" /*+ prequest.getFinishedTimestamp()*/ + " .";
            senderBatchHeader.setSenderBatchId(batchId).setEmailSubject(emailSubject);

            // Currency (90% of the print request value)
            double amountValue = prequest.getCost() * PrintShop.PRINTSHOPS_PERCENTILS_REVENUE;
            Currency amount = new Currency();
            amount.setValue(String.format("%.2f", amountValue)).setCurrency("EUR");

            // Sender Item
            String senderItemID = prequest.getFinishedTimestamp().toString();
            PayoutItem senderItem = new PayoutItem();
            senderItem.setRecipientType("Email")
                    .setNote("Obrigado por usar o ProxyPrint!")
                    .setReceiver(manager.getEmail())
                    .setSenderItemId(senderItemID).setAmount(amount);

            List<PayoutItem> items = new ArrayList<PayoutItem>();
            items.add(senderItem);

            payout.setSenderBatchHeader(senderBatchHeader).setItems(items);

            APIContext apiContext = new APIContext(accessToken);
            batch = payout.createSynchronous(apiContext);

            LoggingManager.info(this.getClass(), "Payout Batch Processed with ID: " + batch.getBatchHeader().getPayoutBatchId());

            if (batch != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Not working yet.
     * @param c the consumer which cancelled a print request.
     * @param pr the printrequest that the above consumer cancelled.
     * @return true/false uppon success or failure
     * @throws PayPalRESTException
     */
    public boolean refundConsumerCancelledPrintRequest(Consumer c, PrintRequest pr) throws PayPalRESTException {
        if (c != null & pr != null) {
            String accessToken = generatePayPalAccessToken();
            Sale sale = Sale.get(accessToken, /*pr.getPayPalSaleID()*/"1KR725617U497772K");

            Amount amount = new Amount();
            amount.setTotal("2.21");
            amount.setCurrency("EUR");

            Refund refund = new Refund();
            refund.setAmount(amount);

            Refund newRefund = sale.refund(accessToken, refund);

            if(newRefund!=null) {
                LoggingManager.info(this.getClass(), "Refund successfull "+c.getEmail()+"::"+newRefund.toString());
                return true;
            }
        }

        return false;
    }
}
