package bean.news;

import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Locale;

import bean.Bean;
import command.news.HTMLTransformer;

public class NewsItemBean implements Bean {
    public static String URL = "https://news.yahoo.co.jp/rss/media/eiga/all.xml";
    public static String TITLE_TAG_NAME = "title";
    public static String DATE_TAG_NAME = "pubDate";
    public static String LINK_TAG_NAME = "link";
    public static String DESCRIPTION_TAG_NAME = "description";
    public static String IMAGE_URL_TAG_NAME = "image";
    private String title;
    private Date date;
    private String link;
    private String description;
    private String imageUrl;
    public void setData(String title, String date, String link, String description, String imageUrl){
        this.title = HTMLTransformer.transform(title).replace("(映画.com)", "");
        this.link = link;
        this.date = parseDateFromString(HTMLTransformer.transform(date));
        this.imageUrl = HTMLTransformer.transform(imageUrl);
        this.description = HTMLTransformer.transform(description) + " ...";
    }
    public String getTitle(){
        return title;
    }
    public Date getDate(){
        return date;
    }
    public String getLink(){
        return link;
    }
    public String getDescription(){
        return description;
    }
    public String getImageUrl(){
        return imageUrl;
    }
    protected Date parseDateFromString(String str){
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        try{
            date = formatter.parse(str);
        }catch(ParseException e){
            throw new RuntimeException(e);
        }
        return date;
    }
}
