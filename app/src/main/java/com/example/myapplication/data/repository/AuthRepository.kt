package com.example.myapplication.data.repository

import com.example.myapplication.data.local.entity.UserEntity
import com.example.myapplication.data.session.SessionManager
import com.example.shoppingapp.data.local.dao.UserDao


class AuthRepository(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) {
    suspend fun login(username: String, password: String): Result<UserEntity> {
        val user = userDao.login(username.trim(), password)
        return if (user != null) {
            sessionManager.saveLogin(user.id, user.username, user.fullName)
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Sai tài khoản hoặc mật khẩu"))
        }
    }

    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    fun getCurrentFullName(): String = sessionManager.getCurrentFullName()

    fun logout() {
        sessionManager.clearSession()
    }
}