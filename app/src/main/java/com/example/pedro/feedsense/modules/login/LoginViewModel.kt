package com.example.pedro.feedsense.modules.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.pedro.feedsense.SingleLiveEvent
import com.example.pedro.feedsense.models.Alert
import com.example.pedro.feedsense.models.User
import com.example.pedro.feedsense.repository.NetworkServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LoginViewModel(private val service: NetworkServices): ViewModel() {

    private var disposable: Disposable? = null

    private val _showHomeScreenForGuest = SingleLiveEvent<String>()
    val showHomeScreenForGuest: LiveData<String>
        get() = _showHomeScreenForGuest

    private val _showHomeScreenForUser = SingleLiveEvent<String>()
    val showHomeScreenForUser: LiveData<String>
        get() = _showHomeScreenForUser

    private val _stopLoading = SingleLiveEvent<Void>()
    val stopLoading: LiveData<Void>
        get() = _stopLoading

    private val _showAlert = SingleLiveEvent<Alert>()
    val showAlert: LiveData<Alert>
        get() = _showAlert

    fun joinSession(sessionId: String) {

        disposable = service.feedsenseService()
                .joinSession(sessionId, "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { _ -> treatJoinSessionWithSuccess(sessionId) },
                        { error -> treatJoinSessionWithFailure(error) }
                )
    }

    fun treatJoinSessionWithSuccess(sessionId: String) {
        _showHomeScreenForGuest.value = sessionId
        _showHomeScreenForGuest.call()
    }

    private fun treatJoinSessionWithFailure(error: Throwable?) {
        _stopLoading.call()
        Log.e("tag", error?.message)
    }

    fun performLogin(email: String, password: String) {
        // perform request for login
        _showHomeScreenForUser.value = email
        _showHomeScreenForUser.call()
    }

    fun register(email: String, password: String, checkPassword: String) {
        if (password == checkPassword && !email.isEmpty() && !password.isEmpty()) {
            val user = User(email, password, true, "")
            performRegisterRequest(user)
            return
        }
        val alert = Alert("Oops!", "Preencha todos os campos corretamente!", "Ok")
        _showAlert.value = alert
        _showAlert.call()
        _stopLoading.call()
    }

    private fun performRegisterRequest(user: User) {
        disposable = service.feedsenseService()
                .registerUser(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { _ -> treatRegisterSuccess(user.email) },
                        { error -> treatRegisterError(error) }
                )
    }

    private fun treatRegisterSuccess(email: String) {
        _showHomeScreenForUser.value = email
        _showHomeScreenForUser.call()
    }

    fun treatRegisterError(error: Throwable?) {
        val alert = Alert("Oops!", error?.message ?: "", "Ok")
        _showAlert.value = alert
        _showAlert.call()
        _stopLoading.call()
    }
}