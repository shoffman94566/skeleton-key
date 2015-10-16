package com.kp.skey;

public class PasswordParameters {

    public long timestamp;
    public Boolean lowerCase;
    public Boolean upperCase;
    public Boolean symbol;
    public Boolean numerical;
    public String siteName;
    public int size;
    private String hashedSitename;

    public void setSize(int size) { this.size = size; }

    public void setLowerCase(Boolean lowerCase) {
        this.lowerCase = lowerCase;
    }

    public void setUpperCase(Boolean upperCase) {
        this.upperCase = upperCase;
    }

    public void setSymbol(Boolean symbol) {
        this.symbol = symbol;
    }

    public void setNumerical(Boolean numerical) {
        this.numerical = numerical;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setHashedSiteName(String hashedSiteName) {
        this.hashedSitename = hashedSiteName;
    }

    public String convertToJson() {
        StringBuilder stringBuilder = new StringBuilder();
        String composedString = "{ \"hashedSiteName\":\"" + hashedSitename + "\", \"lowerCase\": " + lowerCase.toString() + ", \"upperCase\": " + upperCase.toString()  + ", \"symbol\": " + symbol.toString() + ", \"numerical\": " + numerical.toString() + ", \"timestamp\": " + timestamp + ", \"size\": " + size + " }";
        return composedString;
    }
}
