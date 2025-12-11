package com.acme.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDate;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "cardType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MastercardCard.class, name = "MASTERCARD"),
        @JsonSubTypes.Type(value = MastercardTitaniumCard.class, name = "MASTERCARD_TITANIUM"),
        @JsonSubTypes.Type(value = MastercardPlatinumCard.class, name = "MASTERCARD_PLATINUM")
})
public abstract class Card {

    private UUID cardId;
    private LocalDate issuedOn;

    protected Card() { }

    protected Card(LocalDate issuedOn) {
        this.cardId = UUID.randomUUID();
        this.issuedOn = issuedOn;
    }

    public UUID getCardId() {
        return cardId;
    }

    public void setCardId(UUID cardId) {
        this.cardId = cardId;
    }

    public LocalDate getIssuedOn() {
        return issuedOn;
    }

    public void setIssuedOn(LocalDate issuedOn) {
        this.issuedOn = issuedOn;
    }

    public abstract String getLabel();

    public abstract CardLimits getLimits();

}

