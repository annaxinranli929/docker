package bean.price;

import bean.Bean;

public class PriceBean implements Bean {
    private int priceId;
    private String category;
    private int price;

    public int getPriceId() {
        return priceId;
    }   
    public void setPriceId(int priceId) {
        this.priceId = priceId;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
}