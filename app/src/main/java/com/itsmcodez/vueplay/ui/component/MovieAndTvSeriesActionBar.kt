package com.itsmcodez.vueplay.ui.component

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.domain.model.Movie
import com.itsmcodez.vueplay.domain.model.MovieAndTvSeriesMulti
import com.itsmcodez.vueplay.domain.model.TvSeries

@Composable
fun MovieAndTvSeriesActionBar(
    modifier: Modifier = Modifier,
    movieAndTvSeriesMulti: MovieAndTvSeriesMulti
) {
    val context = LocalContext.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO: Actions bar (Add to list, mark as fav, save to watchlist, share)
        /*OutlinedIconButton(onClick = {}) {
            Icon(
                painter = painterResource(R.drawable.rounded_add_24),
                contentDescription = null
            )
        }
        var isFavorite by remember { mutableStateOf(false) }
        OutlinedIconToggleButton(
            checked = isFavorite,
            onCheckedChange = { isFavorite = it }
        ) {
            Icon(
                painter = painterResource(
                    if(isFavorite) R.drawable.round_favorite_24
                    else R.drawable.rounded_heart_plus_24
                ),
                contentDescription = null
            )
        }
        var isSavedToWatchlist by remember { mutableStateOf(false) }
        OutlinedIconToggleButton(
            checked = isSavedToWatchlist,
            onCheckedChange = { isSavedToWatchlist = it }
        ) {
            Icon(
                painter = painterResource(
                    if(isSavedToWatchlist) R.drawable.round_bookmark_added_24
                    else R.drawable.rounded_bookmark_add_24
                ),
                contentDescription = null
            )
        }*/
        OutlinedIconButton(onClick = {
            val share: (String) -> Unit = {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, it)
                context.startActivity(intent)
            }
            when(movieAndTvSeriesMulti) {
                is Movie -> share("https://www.themoviedb.org/movie/${movieAndTvSeriesMulti.movieId}")
                is TvSeries -> share("https://www.themoviedb.org/tv/${movieAndTvSeriesMulti.seriesId}")
            }
        }) {
            Icon(
                painter = painterResource(R.drawable.rounded_share_24),
                contentDescription = null
            )
        }
    }
}