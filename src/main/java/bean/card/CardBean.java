package bean.card;

import java.util.Map;
import bean.Bean;

public class CardBean implements Bean {
    private int cardId;
    private int userId;
    private String brand;
    private String last4;
    private int expMonth;
    private int expYear;
    private boolean isDefault;

    public int getCardId() { return cardId; }
    public void setCardId(int cardId) { this.cardId = cardId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public int getExpMonth() { return expMonth; }
    public void setExpMonth(int expMonth) { this.expMonth = expMonth; }

    public int getExpYear() { return expYear; }
    public void setExpYear(int expYear) { this.expYear = expYear; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public String getLast4() { return last4; }
    public void setLast4(String last4) { this.last4 = last4; }
    
    public boolean getDefaultFlag() { return isDefault; }
    public void setDefaultFlag(boolean isDefault) { this.isDefault = isDefault; }

}