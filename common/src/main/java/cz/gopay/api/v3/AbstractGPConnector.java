/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package cz.gopay.api.v3;

import cz.gopay.api.v3.model.access.AccessToken;
import cz.gopay.api.v3.model.access.AuthHeader;
import cz.gopay.api.v3.model.access.OAuth;
import cz.gopay.api.v3.model.common.Currency;
import cz.gopay.api.v3.model.eet.EETReceipt;
import cz.gopay.api.v3.model.eet.EETReceiptFilter;
import cz.gopay.api.v3.model.payment.*;
import cz.gopay.api.v3.model.payment.support.AccountStatement;
import cz.gopay.api.v3.model.payment.support.PaymentInstrumentRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import javax.ws.rs.WebApplicationException;

/**
 * @author Zbynek Novak novak.zbynek@gmail.com
 * @author František Sichinger
 */
public abstract class AbstractGPConnector implements IGPConnector {
	
	public static final String VERSION = "${project.version}";
	
	protected static final Logger logger = LoggerFactory.getLogger(AbstractGPConnector.class);
	
	public static int CONNECTION_POOL_SIZE = 1;
	public static int CONNECTION_SETUP_TO = 1;
	public static int CONNECTION_SERVICE_TO = 1;
	
	protected String apiUrl;
	
	protected AccessToken accessToken;
	
	public AbstractGPConnector(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	
	public AbstractGPConnector(String apiUrl, AccessToken token) {
		this(apiUrl);
		this.accessToken = token;
	}
	
	protected abstract <T> T createRESTClientProxy(String apiUrl, Class<T> proxy);
	
	@Override
	public IGPConnector getAppToken(String clientId, String clientCredentials) throws GPClientException {
		return getAppToken(clientId, clientCredentials, OAuth.SCOPE_PAYMENT_CREATE);
	}
	
	@Override
	public IGPConnector getAppToken(String clientId, String clientCredentials, String scope) throws GPClientException {
		try {
			logger.debug("get-token [" + clientId + "]");
			
			AuthClient simple = createRESTClientProxy(apiUrl, AuthClient.class);
			
			accessToken = simple.loginApplication(AuthHeader.build(clientId, clientCredentials),
					OAuth.GRANT_TYPE_CLIENT_CREDENTIALS, scope != null ? scope : OAuth.SCOPE_PAYMENT_ALL);
			
			logger.debug("get-token [" + clientId + "] -> [" + accessToken.getAccessToken() + "]");
			
		} catch (WebApplicationException e) {
			logger.error("get-token Error [" + clientId + "] RC ["
						 + e.getResponse().getStatus() + "] Ex: " + e.getResponse().getStatusInfo(), e);
			GPExceptionHandler.handleException(e);
		}
		
		return this;
	}
	
	@Override
	public Payment createPayment(BasePayment payment) throws GPClientException {
		try {
			logger.debug("create-payment payer[" + payment.getPayer() + "] -> [" + payment.getTarget() + "]");
			
			PaymentClient paymentClient = createRESTClientProxy(apiUrl, PaymentClient.class);
			
			return paymentClient.createPayment(AuthHeader
							.build(accessToken != null ? accessToken.getAccessToken() : null),
					payment);
			
		} catch (WebApplicationException e) {
			logger.error("create-payment Error [" + payment.getPayer() + "] -> ["
							+ payment.getTarget() + "] RC [" + e.getResponse().getStatus() + "] Ex: " + e.getResponse()
							.getStatusInfo(), e);
			GPExceptionHandler.handleException(e);
		}
		
		return null;
	}
	
	@Override
	public PaymentResult refundPayment(Long id, Long amount) throws GPClientException {
		try {
			logger.debug("refund-payment [" + id + "] amnt[" + amount + "]");
			
			PaymentClient paymentClient = createRESTClientProxy(apiUrl, PaymentClient.class);
			
			PaymentResult r = paymentClient
					.refundPayment(AuthHeader.build(accessToken != null ? accessToken.getAccessToken() : null), id,
							amount);
			return r;
		} catch (WebApplicationException e) {
			logger.error("refund-payment Error [" + id + "] amnt[" + amount + "] RC ["
					+ e.getResponse().getStatus() + "] Ex: " + e.getResponse().getStatusInfo(), e);
			GPExceptionHandler.handleException(e);
		}
		
		return null;
	}
	
	@Override
	public PaymentResult refundPayment(Long id, RefundPayment refundPayment) throws GPClientException {
		try {
			logger.debug("refund-payment [" + id + "] amnt[" + refundPayment + "]");
			
			PaymentClient paymentClient = createRESTClientProxy(apiUrl, PaymentClient.class);
			
			PaymentResult r = paymentClient
					.refundPayment(AuthHeader.build(accessToken != null ? accessToken.getAccessToken() : null),
							id, refundPayment);
			return r;
		} catch (WebApplicationException e) {
			logger.error("refund-payment Error [" + id + "] amnt[" + refundPayment + "] RC ["
							+ e.getResponse().getStatus() + "] Ex: " + e.getResponse().getStatusInfo(), e);
			GPExceptionHandler.handleException(e);
		}
		
		return null;
	}
	
	@Override
	public Payment createRecurrentPayment(Long id, NextPayment nextPayment) throws GPClientException {
		try {
			logger.debug("create-recurrent - parent id[" + id + "] [" + nextPayment.getOrderNumber() + "]");
			
			PaymentClient paymentClient = createRESTClientProxy(apiUrl, PaymentClient.class);
			
			return paymentClient.createRecurrentPayment(
					AuthHeader.build(accessToken != null ? accessToken.getAccessToken() : null), id, nextPayment);
			
		} catch (WebApplicationException e) {
			logger.error("create-recurrent Error parent id[" + id + "] [" + nextPayment
							.getOrderNumber()
							+ "] RC [" + e.getResponse().getStatus() + "] Ex: " + e.getResponse().getStatusInfo(),
					e);
			GPExceptionHandler.handleException(e);
		}
		
		return null;
	}
	
	@Override
	public PaymentResult voidRecurrency(Long id) throws GPClientException {
		try {
			logger.debug("void-recurrency parent id [" + id + "]");
			
			PaymentClient paymentClient = createRESTClientProxy(apiUrl, PaymentClient.class);
			
			return paymentClient.voidRecurrence(AuthHeader
							.build(accessToken != null ? accessToken.getAccessToken() : null),
					id);
			
		} catch (WebApplicationException e) {
			logger.error("void recurrency Error parent id[" + id + "] RC ["
					+ e.getResponse().getStatus() + "] Ex: " + e.getResponse().getStatusInfo(), e);
			GPExceptionHandler.handleException(e);
		}
		
		return null;
	}
	
	@Override
	public PaymentResult capturePayment(Long id) throws GPClientException {
		try {
			logger.debug("capture payment [" + id + "]");
			
			PaymentClient paymentClient = createRESTClientProxy(apiUrl, PaymentClient.class);
			
			return paymentClient.capturePayment(AuthHeader
							.build(accessToken != null ? accessToken.getAccessToken() : null),
					id);
			
		} catch (WebApplicationException e) {
			logger.error("capture payment Error [" + id + "] RC ["
					+ e.getResponse().getStatus() + "] Ex: " + e.getResponse().getStatusInfo(), e);
			GPExceptionHandler.handleException(e);
		}
		
		return null;
	}
	
	@Override
	public PaymentResult capturePayment(Long id, CapturePayment capturePayment) throws GPClientException {
		try {
			logger.debug("capture payment with amount [" + id + "]");
			
			PaymentClient paymentClient = createRESTClientProxy(apiUrl, PaymentClient.class);
			
			return paymentClient.capturePayment(AuthHeader
							.build(accessToken != null ? accessToken.getAccessToken() : null),
					id,
					capturePayment);
			
		} catch (WebApplicationException e) {
			logger.error("capture payment Error [" + id + "] RC ["
					+ e.getResponse().getStatus() + "] Ex: " + e.getResponse().getStatusInfo(), e);
			GPExceptionHandler.handleException(e);
		}
		
		return null;
	}
	
	@Override
	public PaymentResult voidAuthorization(Long id) throws GPClientException {
		try {
			logger.debug("void auth payment [" + id + "]");
			
			PaymentClient paymentClient = createRESTClientProxy(apiUrl, PaymentClient.class);
			
			return paymentClient
					.voidAuthorization(AuthHeader.build(accessToken != null ? accessToken.getAccessToken() : null), id);
			
		} catch (WebApplicationException e) {
			logger.error("void auth payment Error [" + id + "] RC ["
					+ e.getResponse().getStatus() + "] Ex: " + e.getResponse().getStatusInfo(), e);
			GPExceptionHandler.handleException(e);
		}
		
		return null;
	}
	
	@Override
	public Payment paymentStatus(Long id) throws GPClientException {
		try {
			logger.debug("payment-status [" + id + "]");
			
			PaymentClient paymentClient = createRESTClientProxy(apiUrl, PaymentClient.class);
			
			return paymentClient
					.getPayment(AuthHeader.build(accessToken != null ? accessToken.getAccessToken() : null), id);
			
		} catch (WebApplicationException e) {
			logger.error("payment-status Error [" + id + "] RC [" + e.getResponse()
					.getStatus()
					+ "] Ex: " + e.getResponse().getStatusInfo(), e);
			GPExceptionHandler.handleException(e);
		}
		
		return null;
	}
	
	//  TODO   GPMAIN-3351  Nutne snizeni scopu
	@Override
	public PaymentInstrumentRoot getPaymentInstruments(Long goId, Currency currency) throws GPClientException {
		try {
			logger.debug("payment-instruments [" + goId + "][" + currency + "]");
			
			PaymentClient paymentClient = createRESTClientProxy(apiUrl, PaymentClient.class);
			
			return paymentClient
					.getPaymentInstruments(AuthHeader.build(accessToken != null ? accessToken.getAccessToken() : null),
							goId, currency);
			
		} catch (WebApplicationException e) {
			logger.error("payment-instruments Error [" + goId + "][" + currency + "] RC [" + e
							.getResponse()
							.getStatus()
							+ "] Ex: " + e.getResponse().getStatusInfo(), e);
			GPExceptionHandler.handleException(e);
		}
		
		return null;
	}
	
	@Override
	public byte[] generateStatement(AccountStatement accountStatement) throws GPClientException {
		try {
			logger.debug("generate-statement [" + accountStatement + "]");
			
			PaymentClient paymentClient = createRESTClientProxy(apiUrl, PaymentClient.class);
			
			return paymentClient
					.getStatement(AuthHeader.build(accessToken != null ? accessToken.getAccessToken() : null),
							accountStatement);
			
		} catch (WebApplicationException e) {
			logger.error("generate-statement Error [" + accountStatement + "] RC [" + e
					.getResponse()
					.getStatus()
					+ "] Ex: " + e.getResponse().getStatusInfo(), e);
			GPExceptionHandler.handleException(e);
		}
		
		return null;
	}
	
	
	@Override
	public List<EETReceipt> findEETREceiptsByFilter(EETReceiptFilter filter) throws GPClientException {
		try {
			logger.debug("eet-receipt findByFilter " + filter.toString());
			
			PaymentClient paymentClient = createRESTClientProxy(apiUrl, PaymentClient.class);
			
			return paymentClient.findEETReceiptsByFilter(
					AuthHeader.build(accessToken != null ? accessToken.getAccessToken() : null), filter);
			
		} catch (WebApplicationException e) {
			logger.error("eet-receipt findByFilter " + filter.toString() + " RC [" + e
					.getResponse()
					.getStatus()
					+ "] Ex: " + e.getResponse().getStatusInfo(), e);
			GPExceptionHandler.handleException(e);
		}
		return null;
	}
	
	@Override
	public List<EETReceipt> getEETReceiptByPaymentId(Long id) throws GPClientException {
		try {
			logger.debug("eet-receipt findByPaymentId PaymentId=[" + id + "]");
			
			PaymentClient paymentClient = createRESTClientProxy(apiUrl, PaymentClient.class);
			
			return paymentClient.getEETReceiptByPaymentId(
					AuthHeader.build(accessToken != null ? accessToken.getAccessToken() : null), id);
			
		} catch (WebApplicationException e) {
			logger.error("eet-receipt findByPaymentId PaymentId=[" + id + "] RC [" + e
					.getResponse()
					.getStatus()
					+ "] Ex: " + e.getResponse().getStatusInfo(), e);
			GPExceptionHandler.handleException(e);
		}
		return null;
	}
	
	@Override
	public String getApiUrl() {
		return apiUrl;
	}
	
	@Override
	public AccessToken getAccessToken() {
		return accessToken;
	}
	
	@Override
	public void setAccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
	}
	
	protected abstract String getImplementationName();
	
	public String getVersion() {
		return VERSION;
	}
	
}
