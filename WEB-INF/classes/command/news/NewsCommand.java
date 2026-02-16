package command.news;

import java.util.List;

import logic.ResponseContext;
import command.AbstractCommand;
import bean.news.NewsItemBean;

public class NewsCommand extends AbstractCommand {
    public ResponseContext execute(ResponseContext resc) {
        NewsPageCreator creator = new NewsPageCreator();
        List<NewsItemBean> newsList = creator.createNewsPages("bean.news.NewsItemBean");

        resc.setResult(newsList);
        resc.setTarget("news");
        return resc;
    }
}