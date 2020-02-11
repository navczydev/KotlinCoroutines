/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.raywenderlich.kotlin.coroutines.domain.repository

import com.raywenderlich.kotlin.coroutines.data.api.MovieApiService
import com.raywenderlich.kotlin.coroutines.data.database.MovieDao
import com.raywenderlich.kotlin.coroutines.di.API_KEY
import com.raywenderlich.kotlin.coroutines.data.model.Movie
import com.raywenderlich.kotlin.coroutines.data.model.MoviesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/**
 * Connects to the end entity, and exposes functionality to the user.
 */
class MovieRepositoryImpl(
    private val movieApiService: MovieApiService,
    private val movieDao: MovieDao
) : MovieRepository {

  override fun getMovies(
      onMoviesReceived: (List<Movie>) -> Unit,
      onError: (Throwable) -> Unit
  ) {
    movieApiService.getMovies(API_KEY).enqueue(object : Callback<MoviesResponse> {
      override fun onFailure(call: Call<MoviesResponse>, throwable: Throwable) {
        val savedMovies = movieDao.getSavedMovies()

        /**
         * If there's no internet connection, default to the cached values.
         * Otherwise propagate the error.
         * */
        if (throwable is IOException && savedMovies.isNotEmpty()) {
          onMoviesReceived(savedMovies)
        } else {
          onError(throwable)
        }
      }

      override fun onResponse(call: Call<MoviesResponse>, response: Response<MoviesResponse>) {
        val movies = response.body()?.movies ?: emptyList()

        if (movies.isNotEmpty()) {
          movieDao.saveMovies(movies)
        }
        onMoviesReceived(movies)
      }
    })
  }
}