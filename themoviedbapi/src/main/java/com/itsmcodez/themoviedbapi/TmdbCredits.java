package com.itsmcodez.themoviedbapi;

import com.itsmcodez.themoviedbapi.model.credits.Credit;
import com.itsmcodez.themoviedbapi.tools.ApiUrl;
import com.itsmcodez.themoviedbapi.tools.TmdbApiClient;
import com.itsmcodez.themoviedbapi.tools.TmdbException;

/**
 * The movie database api for credits. See the
 * <a href="https://developer.themoviedb.org/reference/credit-details">documentation</a> for more info.
 */
public class TmdbCredits {
    protected static final String TMDB_METHOD_CREDITS = "credit";

    private final TmdbApiClient tmdbApiClient;

    /**
     * Create a new TmdbCredits instance to call the credits related TMDb API methods.
     */
    TmdbCredits(TmdbApiClient tmdbApiClient) {
        this.tmdbApiClient = tmdbApiClient;
    }

    /**
     * <p>Get the details for a credit.</p>
     * <p>See the <a href="https://developer.themoviedb.org/reference/credit-details">documentation</a> for more info.</p>
     *
     * @param creditId The credit id.
     * @param language nullable - The language to query the results in. Default: en-US.
     * @return the credit details
     */
    public Credit getDetails(String creditId, String language) throws TmdbException {
        ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_CREDITS, creditId)
            .addLanguage(language);

        return tmdbApiClient.get(apiUrl, Credit.class);
    }
}
