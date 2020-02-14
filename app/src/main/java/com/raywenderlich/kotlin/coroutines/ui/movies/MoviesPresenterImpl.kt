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
package com.raywenderlich.kotlin.coroutines.ui.movies

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raywenderlich.kotlin.coroutines.domain.repository.MovieRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Handles the business logic calls, reacting to UI events.
 */
/*class MoviesPresenterImpl(private val movieRepository: MovieRepository) :ViewModel(), MoviesPresenter,
    CoroutineScope {*/
class MoviesPresenterImpl(private val movieRepository: MovieRepository) : ViewModel(),
    MoviesPresenter {
    //MAIN-FIRST - don't need to switch back to MAIN
    // if this job's any child get failed or cancelled that will trigger the cancellation of parent
    //which means cancel all the children,cancelling parent will automatically cancel the all children
    //private var parentJob = Job()
    //private val parentJob = SupervisorJob()

   /* private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.e("###", throwable.message ?: "")
        }*/

    //required for Job
    //only for presenter
    /*override fun start() {
        //if(!parentJob.isActive) parentJob = Job()
    }

    override fun stop() {
        //cancel when activity's stop called
        //supervisorJob' it will keep itself alive and kills all children
        parentJob.cancelChildren()
        //used for Job()
        //parentJob.cancel()
    }*/

/*
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + parentJob + coroutineExceptionHandler
*/

    private lateinit var moviesView: MoviesView

    override fun setView(moviesView: MoviesView) {
        this.moviesView = moviesView
    }

    override fun getData() {
        viewModelScope.launch {
            //launch {
            //for testing job creation-recreation
            //delay(5000)
            //fire the network call
            //val result = movieRepository.getMovies()
            //check if data is there process it
            /*if (result.value != null && result.value.isNotEmpty()) {
                moviesView.showMovies(result.value)
            } else if (result.throwable != null) {
                handleError(result.throwable)
            }*/
            val result = runCatching { movieRepository.getMovies() }
            result.onSuccess {
                moviesView.showMovies(it)
            }.onFailure {
                Log.e("###", it.message ?: "")
                handleError(it)
            }
        }
    }

    private fun handleError(throwable: Throwable) {
        moviesView.showError(throwable)
    }
}