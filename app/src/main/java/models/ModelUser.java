package models;

public class ModelUser {
    String UserName,YoutubeViews,InstaViews,TicktokViews,Image,uid,Email,CompanyName,OnlineStatus,account;

    public ModelUser(String userName, String youtubeViews, String instaViews, String ticktokViews, String image, String uid, String email, String companyName, String onlineStatus) {
        this.UserName = userName;
        this.YoutubeViews = youtubeViews;
        this.InstaViews = instaViews;
        this.TicktokViews = ticktokViews;
        this.Image = image;
        this.uid = uid;
        this.Email = email;
        this.CompanyName = companyName;
        this.OnlineStatus = onlineStatus;
    }

    public ModelUser(String image, String userName, String companyName, String email, String uid, String account) {
        this.UserName = userName;
        this.CompanyName = companyName;
        this.Email = email;
        this.Image = image;
        this.uid = uid;
        this.account=account;
    }
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public ModelUser(String image, String userName, String youtubeViews, String ticktokViews, String instaViews, String uid, String Account) {
        this.UserName = userName;
        this.YoutubeViews = youtubeViews;
        this.InstaViews = instaViews;
        this.TicktokViews = ticktokViews;
        this.Image = image;
        this.uid = uid;
        this.account=Account;
    }

    public ModelUser(String image, String userName, String company, String email, String uid) {
        this.UserName = userName;
        this.Image = image;
        this.uid = uid;
        this.Email = email;
        this.CompanyName = company;;
    }

    public String getOnlineStatus() {
        return OnlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        OnlineStatus = onlineStatus;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public ModelUser(String userName, String youtubeViews, String instaViews, String ticktokViews, String image, String uid, String Email, String CompanyName) {
        this.UserName = userName;
        this.YoutubeViews = youtubeViews;
        this.InstaViews = instaViews;
        this.TicktokViews = ticktokViews;
        this.Image = image;
        this.uid = uid;
        this.CompanyName=CompanyName;
        this.Email=Email;
    }

    public ModelUser(){
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getYoutubeViews() {
        return YoutubeViews;
    }

    public void setYoutubeViews(String youtubeViews) {
        YoutubeViews = youtubeViews;
    }

    public String getInstaViews() {
        return InstaViews;
    }

    public void setInstaViews(String instaViews) {
        InstaViews = instaViews;
    }

    public String getTicktokViews() {
        return TicktokViews;
    }

    public void setTicktokViews(String ticktokViews) {
        TicktokViews = ticktokViews;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
