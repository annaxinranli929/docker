package command.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import bean.admin.AdminMovieBean;
import bean.admin.TmdbMovieBean;
import command.AbstractCommand;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dao.ConnectionManager;
import dao.admin.AdminMovieDao;
import logic.RequestContext;
import logic.ResponseContext;

public class AdminImportScheduleCommand extends AbstractCommand<TmdbMovieBean> {

    private static final int MAX_RESULTS = 10;
    private static final String TARGET = "admin_import_schedule";

    @Override
    public ResponseContext execute(ResponseContext resc) {
        RequestContext reqc = getRequestContext();
        ServletContext ctx = ((HttpServletRequest) reqc.getRequest()).getServletContext();

        String action = first(reqc, "action"); // null / search / select / register
        String query  = first(reqc, "query");
        String tmdbIdStr = first(reqc, "tmdbId");

        Map<String, Object> result = new HashMap<>();
        result.put("ok", true);

        if (action == null) return render(resc, result);

        try {
            switch (action) {
                case "search":
                    return handleSearch(resc, ctx, result, query);

                case "select":
                    return handleSelect(resc, ctx, result, query, tmdbIdStr);

                case "register":
                    return handleRegister(resc, ctx, result, query, tmdbIdStr);

                default:
                    return fail(resc, result, "不正なactionです: " + action);
            }
        } catch (Exception e) {
            return fail(resc, result, "エラー: " + e.getMessage());
        }
    }

    // -------------------------
    // handlers
    // -------------------------

    private ResponseContext handleSearch(ResponseContext resc, ServletContext ctx,
                                         Map<String, Object> result, String query) throws Exception {
        if (isBlank(query)) return fail(resc, result, "検索キーワードを入力してください");

        String token = getTmdbToken(ctx);
        List<TmdbMovieBean> movies = searchTmdb(token, query);
        putSearchResult(result, query, movies);
        return render(resc, result);
    }

    private ResponseContext handleSelect(ResponseContext resc, ServletContext ctx,
                                     Map<String, Object> result, String query, String tmdbIdStr) throws Exception {

        // parse tmdbId FIRST
        Integer tmdbId = parseTmdbId(tmdbIdStr, "tmdbId が取得できませんでした（パラメータ名を確認）", resc, result);
        if (tmdbId == null) return render(resc, result);

        if (isBlank(query))
            return fail(resc, result, "query が空です（select時も query を送ってください）");

        String token = getTmdbToken(ctx);

        // rebuild list + pick selected movie
        TmdbMovieBean selected = rebuildAndSelect(result, token, query, tmdbId);
        if (selected == null)
            return fail(resc, result, "選択した映画が検索結果から見つかりませんでした");

        // already registered?
        Integer movieId = movieDao().findMovieIdByApiId(tmdbId);
        if (movieId != null)
            result.put("selectedMovieId", String.valueOf(movieId));

        // fetch runtime
        Integer rt = fetchRuntimeMinutes(token, tmdbId);
        selected.setRuntimeMinutes(rt);

        result.put("selected", selected);
        return render(resc, result);
    }

    private ResponseContext handleRegister(ResponseContext resc, ServletContext ctx,
                                           Map<String, Object> result, String query, String tmdbIdStr) throws Exception {
        Integer tmdbId = parseTmdbId(tmdbIdStr, "tmdbId が取得できませんでした", resc, result);
        if (tmdbId == null) return render(resc, result);
        if (isBlank(query)) return fail(resc, result, "query が空です（register時も query を送ってください）");

        String token = getTmdbToken(ctx);
        TmdbMovieBean selected = rebuildAndSelect(result, token, query, tmdbId);
        if (selected == null) return fail(resc, result, "選択した映画が検索結果から見つかりませんでした");

        AdminMovieDao mdao = movieDao();

        // already exists?
        Integer movieId = mdao.findMovieIdByApiId(tmdbId);
        if (movieId != null) {
            Integer rt = fetchRuntimeMinutes(token, tmdbId);
            selected.setRuntimeMinutes(rt);
            result.put("selected", selected);

            result.put("selectedMovieId", String.valueOf(movieId));
            result.put("message", "既にDB登録済みです（movieId=" + movieId + "）");
            return render(resc, result);
        }

        Integer rt = fetchRuntimeMinutes(token, selected.getTmdbId());
        if (rt == null || rt <= 0) rt = 120;
        selected.setRuntimeMinutes(rt);
        result.put("selected", selected);

        // insert
        AdminMovieBean mb = toAdminMovieBean(selected);
        if (!mdao.insert(mb)) throw new RuntimeException("movie insert failed");

        // fetch new id
        movieId = mdao.findMovieIdByApiId(tmdbId);
        if (movieId == null) throw new RuntimeException("movie inserted but cannot find by api_id");

        result.put("selectedMovieId", String.valueOf(movieId));
        result.put("message", "DBに映画を登録しました（movieId=" + movieId + "）");
        return render(resc, result);
    }


    // -------------------------
    // TMDb search
    // -------------------------
    private List<TmdbMovieBean> searchTmdb(String bearerToken, String query) throws Exception {
        List<TmdbMovieBean> jaList = searchTmdbLang(bearerToken, query, "ja-JP");
        List<TmdbMovieBean> enList = searchTmdbLang(bearerToken, query, "en-US");

        // map EN by tmdbId
        java.util.Map<Integer, String> enOverviewMap = new java.util.HashMap<>();
        for (TmdbMovieBean en : enList) {
            if (!isBlank(en.getOverview())) {
                enOverviewMap.put(en.getTmdbId(), en.getOverview());
            }
        }

        // fill missing JA overview from EN
        for (TmdbMovieBean ja : jaList) {
            if (isBlank(ja.getOverview())) {
                String enOv = enOverviewMap.get(ja.getTmdbId());
                if (!isBlank(enOv)) ja.setOverview(enOv);
            }
        }

        return jaList;
    }

    private List<TmdbMovieBean> searchTmdbLang(String bearerToken, String query, String lang) throws Exception {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String urlStr = "https://api.themoviedb.org/3/search/movie?query=" + encoded
                + "&include_adult=false&language=" + lang + "&page=1";

        HttpURLConnection con = (HttpURLConnection) new URL(urlStr).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + bearerToken);
        con.setRequestProperty("accept", "application/json");
        con.setConnectTimeout(5000);
        con.setReadTimeout(8000);

        int status = con.getResponseCode();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                status >= 200 && status < 300 ? con.getInputStream() : con.getErrorStream(),
                StandardCharsets.UTF_8
        ))) {
            StringBuilder sb = new StringBuilder();
            for (String line; (line = br.readLine()) != null; ) sb.append(line);

            if (status < 200 || status >= 300) {
                throw new RuntimeException("TMDb HTTP " + status + ": " + sb);
            }

            JsonObject root = JsonParser.parseString(sb.toString()).getAsJsonObject();
            JsonArray results = root.has("results") && root.get("results").isJsonArray()
                    ? root.getAsJsonArray("results")
                    : new JsonArray();

            List<TmdbMovieBean> list = new ArrayList<>();
            for (int i = 0; i < results.size() && i < MAX_RESULTS; i++) {
                JsonObject m = results.get(i).getAsJsonObject();
                if (!m.has("id") || m.get("id").isJsonNull()) continue;

                TmdbMovieBean b = new TmdbMovieBean();
                b.setTmdbId(m.get("id").getAsInt());
                b.setTitle(getAsString(m, "title"));
                b.setReleaseDate(getAsString(m, "release_date"));
                b.setVoteAverage(m.has("vote_average") && !m.get("vote_average").isJsonNull()
                        ? m.get("vote_average").getAsDouble() : 0.0);
                b.setOverview(getAsString(m, "overview"));

                if (m.has("genre_ids") && m.get("genre_ids").isJsonArray()) {
                    JsonArray arr = m.getAsJsonArray("genre_ids");
                    String[] ids = new String[arr.size()];
                    for (int j = 0; j < arr.size(); j++) ids[j] = String.valueOf(arr.get(j).getAsInt());
                    b.setGenreIds(ids);
                }

                String posterPath = getAsString(m, "poster_path");
                if (!isBlank(posterPath)) b.setPosterUrl("https://image.tmdb.org/t/p/w185" + posterPath);

                list.add(b);
            }
            return list;

        } finally {
            con.disconnect();
        }
    }

    private Integer fetchRuntimeMinutes(String bearerToken, int tmdbId) throws Exception {
        String urlStr = "https://api.themoviedb.org/3/movie/" + tmdbId + "?language=ja-JP";

        HttpURLConnection con = (HttpURLConnection) new URL(urlStr).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + bearerToken);
        con.setRequestProperty("accept", "application/json");

        int status = con.getResponseCode();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                status >= 200 && status < 300 ? con.getInputStream() : con.getErrorStream(),
                StandardCharsets.UTF_8
        ))) {
            StringBuilder sb = new StringBuilder();
            for (String line; (line = br.readLine()) != null; ) sb.append(line);

            if (status < 200 || status >= 300) {
                throw new RuntimeException("TMDb details HTTP " + status + ": " + sb);
            }

            JsonObject root = JsonParser.parseString(sb.toString()).getAsJsonObject();
            if (root.has("runtime") && !root.get("runtime").isJsonNull()) {
                int rt = root.get("runtime").getAsInt();
                return rt > 0 ? rt : null;
            }
            return null;

        } finally {
            con.disconnect();
        }
    }

    private TmdbMovieBean rebuildAndSelect(Map<String, Object> result, String token, String query, int tmdbId) throws Exception {
        List<TmdbMovieBean> movies = searchTmdb(token, query);
        putSearchResult(result, query, movies);

        for (TmdbMovieBean m : movies) {
            if (m.getTmdbId() == tmdbId) {
                result.put("selected", m);
                return m;
            }
        }
        return null;
    }

    // -------------------------
    // helpers
    // -------------------------

    private AdminMovieDao movieDao() throws Exception {
        AdminMovieDao dao = new AdminMovieDao();
        dao.setConnection(ConnectionManager.getInstance().getConnection());
        return dao;
    }

    private AdminMovieBean toAdminMovieBean(TmdbMovieBean selected) {
        AdminMovieBean mb = new AdminMovieBean();
        mb.setMovieName(selected.getTitle());
        mb.setPosterUrl(isBlank(selected.getPosterUrl()) ? "no-poster" : selected.getPosterUrl());
        mb.setEvaluation(Math.round((selected.getVoteAverage() / 2.0) * 10.0) / 10.0);
        mb.setSummary(selected.getOverview());
        mb.setApiId(String.valueOf(selected.getTmdbId()));
        mb.setGenreIds(selected.getGenreIds());
        mb.setRuntimeMinutes(selected.getRuntimeMinutes());
        return mb;
    }

    private void putSearchResult(Map<String, Object> result, String query, List<TmdbMovieBean> movies) {
        result.put("data", movies);
        result.put("query", query);
    }

    private Integer parseTmdbId(String tmdbIdStr, String emptyMsg, ResponseContext resc, Map<String, Object> result) {
        if (isBlank(tmdbIdStr)) {
            fail(resc, result, emptyMsg);
            return null;
        }
        try {
            return Integer.parseInt(tmdbIdStr);
        } catch (NumberFormatException e) {
            fail(resc, result, "tmdbId の形式が不正です: " + tmdbIdStr);
            return null;
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private ResponseContext fail(ResponseContext resc, Map<String, Object> result, String msg) {
        result.put("ok", false);
        result.put("message", msg);
        return render(resc, result);
    }

    private String getTmdbToken(ServletContext ctx) {
        String token = ctx.getInitParameter("TMDB_BEARER");
        if (isBlank(token)) throw new RuntimeException("TMDB_BEARER is not set");
        return token.trim();
    }

    private ResponseContext render(ResponseContext resc, Map<String, Object> result) {
        resc.setResult(result);
        resc.setTarget(TARGET);
        return resc;
    }

    private String getAsString(JsonObject o, String key) {
        return (o.has(key) && !o.get(key).isJsonNull()) ? o.get(key).getAsString() : "";
    }

    private String first(RequestContext reqc, String key) {
        String[] arr = reqc.getParameter(key);
        if (arr == null || arr.length == 0) return null;
        return arr[0];
    }
}
