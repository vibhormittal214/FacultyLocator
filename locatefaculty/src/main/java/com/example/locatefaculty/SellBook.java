package com.example.locatefaculty;
import java.util.Date;

public class SellBook {
    public String bookname;
    public String price;
    public String condition;
    public String contact;

    public SellBook() {
    }
    public SellBook(String bookname, String price,String condition,String contact) {
        this.bookname = bookname;
        this.price = price;
        this.condition = condition;
        this.contact = contact;
    }


    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }
    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

}