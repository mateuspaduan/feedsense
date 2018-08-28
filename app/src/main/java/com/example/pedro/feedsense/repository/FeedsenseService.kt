package com.example.pedro.feedsense.repository

import com.example.pedro.feedsense.models.ReactionModel
import com.example.pedro.feedsense.models.SessionModel
import io.reactivex.Observable
import retrofit2.http.*

interface FeedsenseService {

    @POST("sessions")
    fun createSession(@Body sessionModel: SessionModel): Observable<String>

    @PUT("sessions/{sessionId}/{guestId}")
    fun joinSession(@Path("sessionId") sessionId: String,
                    @Path("guestId") guestId: String): Observable<Any>

    @POST("comments/{sessionId}/{guestId}")
    fun reactToSession(@Path("sessionId") sessionId: String,
                       @Path("guestId") guestId: String,
                       @Body reaction: ReactionModel): Observable<String>
}