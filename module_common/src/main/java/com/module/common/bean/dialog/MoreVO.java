package com.module.common.bean.dialog;


import android.os.Parcel;
import android.os.Parcelable;

public class MoreVO implements Parcelable {

    public enum MoreType {
        share(1, "test"),

        ;


        // 成员变量
        private int index;
        private String name;


        MoreType(int index, String name) {
            this.index = index;
            this.name = name;
        }


        public String getName() {
            return name;
        }

        public MoreType setName(String name) {
            this.name = name;
            return this;
        }

        public int getIndex() {
            return index;
        }

        public MoreType setIndex(int index) {
            this.index = index;
            return this;
        }


    }

    public MoreVO() {
    }

    public MoreVO setMoreType(MoreType type) {
        this.moreId = type.getIndex();
        this.name = type.getName();
        return this;
    }

    public int getMoreId() {
        return moreId;
    }

    public MoreVO setMoreId(int moreId) {
        this.moreId = moreId;
        return this;
    }

    public String getName() {
        return name;
    }

    public MoreVO setName(String name) {
        this.name = name;
        return this;
    }

    private int moreId;
    private String name;

    protected MoreVO(Parcel in) {
        moreId = in.readInt();
        name = in.readString();
    }

    public static final Creator<MoreVO> CREATOR = new Creator<MoreVO>() {
        @Override
        public MoreVO createFromParcel(Parcel in) {
            return new MoreVO(in);
        }

        @Override
        public MoreVO[] newArray(int size) {
            return new MoreVO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(moreId);
        dest.writeString(name);
    }
}
