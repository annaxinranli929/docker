package bean.admin;

import bean.Bean;

public class AdminMovieBean implements Bean {
    private String movieId;
    private String movieName;
    private String posterUrl;
    private double evaluation;
    private String summary;
    private String apiId;        // same reason: easy param handling
    private String[] genreIds;     
    private String[] genreNames;
    private Integer runtimeMinutes;

    public AdminMovieBean() {}

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public String getMovieName() { return movieName; }
    public void setMovieName(String movieName) { this.movieName = movieName; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public double getEvaluation() { return evaluation; }
    public void setEvaluation(double evaluation) { this.evaluation = evaluation; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getApiId() { return apiId; }
    public void setApiId(String apiId) { this.apiId = apiId; }
    public String[] getGenreIds() { return genreIds; }
    public void setGenreIds(String[] genreIds) { this.genreIds = genreIds; }

    public String[] getGnreNames() { return genreNames; }
    public void setGenreNames(String[] genreNames) { this.genreNames = genreNames; }

    public Integer getRuntimeMinutes() { return runtimeMinutes; }
    public void setRuntimeMinutes(Integer runtimeMinutes) { this.runtimeMinutes = runtimeMinutes; }
}