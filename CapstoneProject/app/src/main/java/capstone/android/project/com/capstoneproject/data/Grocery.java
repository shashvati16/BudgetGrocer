package capstone.android.project.com.capstoneproject.data;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

/**
 * Created by Shashvati on 8/10/2017.
 */

public class Grocery implements Parcelable{
    private String item;
    private String quantity;
    private Deals[] deals;

    public String getItem() {
        return item;
    }

    public void setItems(String item) {
        this.item = item;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    public Deals[] getDeals() {
        return deals;
    }

    public void setDeals(Deals[] deals) {
        this.deals = deals;
    }


    public Grocery(){}
    private Grocery(Parcel in){
        item = in.readString();
        quantity = in.readString();
        deals = in.createTypedArray(Deals.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(item);
        parcel.writeString(quantity);
        parcel.writeTypedArray(deals,i);
    }
    public static final Creator<Grocery> CREATOR = new Creator<Grocery>() {
        @Override
        public Grocery createFromParcel(Parcel in) {
            return new Grocery(in);
        }

        @Override
        public Grocery[] newArray(int size) {
            return new Grocery[size];
        }
    };

}
