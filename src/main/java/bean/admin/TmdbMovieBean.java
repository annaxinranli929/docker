package bean.admin;

import java.util.List;

import bean.Bean; 

public class TmdbMovieBean implements Bean{
    private int tmdbId;
    private String title;
    private String releaseDate;
    private double voteAverage;   // TMDb uses 0-10
    private String overview;
    private String posterUrl;
    private String[] genreIds;
    private Integer runtimeMinutes; // use Integer so null is allowed

    public int getTmdbId() { return tmdbId; }
    public void setTmdbId(int tmdbId) { this.tmdbId = tmdbId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public double getVoteAverage() { return voteAverage; }
    public void setVoteAverage(double voteAverage) { this.voteAverage = voteAverage; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String[] getGenreIds() { return genreIds; }
    public void setGenreIds(String[] genreIds) { this.genreIds = genreIds; }

    public Integer getRuntimeMinutes() { return runtimeMinutes; }
    public void setRuntimeMinutes(Integer runtimeMinutes) { this.runtimeMinutes = runtimeMinutes; }
}

