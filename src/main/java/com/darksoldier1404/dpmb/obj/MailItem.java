package com.darksoldier1404.dpmb.obj;

public class MailItem {
    long receiveDate;
    int page;
    int slot;

    public MailItem() {
    }

    public MailItem(long receiveDate, int page, int slot) {
        this.receiveDate = receiveDate;
        this.page = page;
        this.slot = slot;
    }

    public long getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(long receiveDate) {
        this.receiveDate = receiveDate;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public String serialize() {
        return receiveDate + ";" + page + ";" + slot;
    }

    public static MailItem deserialize(String data) {
        String[] parts = data.split(";");
        long receiveDate = Long.parseLong(parts[0]);
        int page = Integer.parseInt(parts[1]);
        int slot = Integer.parseInt(parts[2]);
        return new MailItem(receiveDate, page, slot);
    }
}
