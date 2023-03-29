package pl.jordii.mctiktokgiftactions.rest.objects;

public class Gift {

    public GiftData data;
    public String uniqueId;
    public String userId;
    public String type;

    public class GiftData {
        public String giftId;
        public String giftName;
    }
}
