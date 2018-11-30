package yoggaebi.yoseb.yoseptalk.model;

public class UserModel {
    private String imageUrl;
    private String userName;
    private String uid; //채팅하고싶은 사람에 대한 uid
    private String pushToken;
    private String comment;

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
