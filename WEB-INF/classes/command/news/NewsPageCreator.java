package command.news;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.lang.reflect.Field;
import bean.news.NewsItemBean;

public class NewsPageCreator{
    public List<NewsItemBean> createNewsPages(String newsPageClassName){
        System.out.println("いまから" + newsPageClassName + "をつくるよ！");
        List<NewsItemBean> resultList = new ArrayList<>();
        String xmlUrl = null;
        Class<?> c = null;
        try{
            c = Class.forName(newsPageClassName);
            Field field = c.getField("URL");
            xmlUrl = (String) field.get(null);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        XmlParser xmlParser = null;
        try{
            xmlParser = new XmlParser(xmlUrl);
        }catch(Exception e){
            throw new RuntimeException("XmlParserのインスタンス化で例外", e);
        }
        List<Map<String, String>> items = xmlParser.extractAllElements("item");
        if (items.size() > 22) {
            items.subList(22, items.size()).clear();
        }
        for(Map<String, String> item : items){
            NewsItemBean newsItem = null;
            try{
                newsItem = (NewsItemBean) c.getDeclaredConstructor().newInstance();
            }catch(Exception e){
                throw new RuntimeException("Pluginで例外", e);
            }
            if(newsItem == null){
                throw new RuntimeException("NewsPageのインスタンスがとれませんでした");
            }
            newsItem.setData(
                item.get(NewsItemBean.TITLE_TAG_NAME),
                item.get(NewsItemBean.DATE_TAG_NAME),
                item.get(NewsItemBean.LINK_TAG_NAME),
                item.get(NewsItemBean.DESCRIPTION_TAG_NAME),
                item.get(NewsItemBean.IMAGE_URL_TAG_NAME)
            );
            resultList.add(newsItem);
        }
        resultList.sort(Comparator.comparing(NewsItemBean::getDate).reversed());
        return resultList;
    }
}