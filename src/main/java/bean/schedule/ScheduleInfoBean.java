package bean.schedule;

import bean.Bean;
import java.sql.Timestamp;

public class ScheduleInfoBean implements Bean {

    private String movieName;
    private String posterUrl;
    private String screenName;
    private Timestamp startTime;
    private Timestamp endTime;

    public ScheduleInfoBean(
        String movieName,
        String posterUrl,
        String screenName,
        Timestamp startTime,
        Timestamp endTime
    ) {
        this.movieName = movieName;
        this.posterUrl = posterUrl;
        this.screenName = screenName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getMovieName() { return movieName; }
    public String getPosterUrl() { return posterUrl; }
    public String getScreenName() { return screenName; }
    public Timestamp getStartTime() { return startTime; }
    public Timestamp getEndTime() { return endTime; }
}
