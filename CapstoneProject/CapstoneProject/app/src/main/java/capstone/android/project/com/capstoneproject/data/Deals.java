package capstone.android.project.com.capstoneproject.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Date;

/**
 * Created by Shashvati on 8/20/2017.
 */

public class Deals implements Parcelable{
    private String brandItems;
    private String dealImgs;
    private String storeNames;
    private String price;
    private String expiryDate;



    public String getBrandItems() {
        return brandItems;
    }

    public void setBrandItems(String brandItems) {
        this.brandItems = brandItems;
    }


    public String getDealImgs() {
        return dealImgs;
    }

    public void setDealImgs(String dealImgs) {
        this.dealImgs = dealImgs;
    }
    public String getStoreNames() {
        return storeNames;
    }

    public void setStoreNames(String storeNames) {
        this.storeNames = storeNames;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Deals() {}
    private Deals(Parcel in){
        price = in.readString();
        storeNames = in.readString();
        dealImgs = in.readString();
        brandItems = in.readString();
        expiryDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(price);
        dest.writeString(storeNames);
        dest.writeString(dealImgs);
        dest.writeString(brandItems);
        dest.writeString(expiryDate);
    }
    public static final Creator<Deals> CREATOR = new Creator<Deals>() {
        @Override
        public Deals createFromParcel(Parcel in) {
            return new Deals(in);
        }

        @Override
        public Deals[] newArray(int size) {
            return new Deals[size];
        }
    };
}
