package bean.movie;

import java.util.Date;
import java.util.List;
import bean.Bean;

public class ShowDateBean implements Bean {

    private Date date;
    private List<ScheduleBean> schedules;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<ScheduleBean> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ScheduleBean> schedules) {
        this.schedules = schedules;
    }
}