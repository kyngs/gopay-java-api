package cz.gopay.api.v3.model.payment.support;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Pavel Valenta (pavel.valenta@gopay.cz)
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class PayerPaymentCard {
    
    @XmlElement(name="")
    private String cardNumber;
    
    @XmlElement(name="")
    private String cardExpiration;
    
    @XmlElement(name="")
    private String cardBrand;
    
    @XmlElement(name="")
    private String cardIssuerCountry;
    
    @XmlElement(name="")
    private String cardIssuerBank;
    
    public String getCardNumber() {
        return cardNumber;
    }
    
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    public String getCardExpiration() {
        return cardExpiration;
    }
    
    public void setCardExpiration(String cardExpiration) {
        this.cardExpiration = cardExpiration;
    }
    
    public String getCardBrand() {
        return cardBrand;
    }
    
    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }
    
    public String getCardIssuerCountry() {
        return cardIssuerCountry;
    }
    
    public void setCardIssuerCountry(String cardIssuerCountry) {
        this.cardIssuerCountry = cardIssuerCountry;
    }
    
    public String getCardIssuerBank() {
        return cardIssuerBank;
    }
    
    public void setCardIssuerBank(String cardIssuerBank) {
        this.cardIssuerBank = cardIssuerBank;
    }
    
    
    @Override
    public String toString() {
        return String
                .format("PayerPaymentCard [cardNumber=%s, cardExpiration=%s, cardBrand=%s, cardIssuerCountry=%s, cardIssuerBank=%s]",
                        cardNumber, cardExpiration, cardBrand, cardIssuerCountry, cardIssuerBank);
    }
    
    
    public PayerPaymentCard build(String cardNumber, String cardExpiration, String cardBrand, String cardIssuerCountry,  String cardIssuerBank) {
        PayerPaymentCard payerPaymentCard = new PayerPaymentCard();
        payerPaymentCard.setCardNumber(cardNumber);
        payerPaymentCard.setCardExpiration(cardExpiration);
        payerPaymentCard.setCardBrand(cardBrand);
        payerPaymentCard.setCardIssuerCountry(cardIssuerCountry);
        payerPaymentCard.setCardIssuerBank(cardIssuerBank);
        
        return this;
    }
    
}
