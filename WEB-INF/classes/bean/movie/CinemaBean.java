package bean.movie;

import java.util.List;

import bean.Bean;

public class CinemaBean implements Bean {
    private String cinemaId;
    private String cinemaName;
    private List<ShowDateBean> dates;

    public String getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(String cinemaId) {
        this.cinemaId = cinemaId;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public List<ShowDateBean> getDates() {
        return dates;
    }

    public void setDates(List<ShowDateBean> dates) {
        this.dates = dates;
    }
}