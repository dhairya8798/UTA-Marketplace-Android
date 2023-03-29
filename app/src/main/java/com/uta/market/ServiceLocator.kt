package com.uta.market

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.uta.market.data.AppSessionManager
import com.uta.market.data.source.ProductDataSource
import com.uta.market.data.source.UserDataSource
import com.uta.market.data.source.local.ProductsLocalDataSource
import com.uta.market.data.source.local.AppDatabase
import com.uta.market.data.source.local.UserLocalDataSource
import com.uta.market.data.source.remote.AuthRemoteDataSource
import com.uta.market.data.source.remote.ProductsRemoteDataSource
import com.uta.market.data.source.repository.AuthRepoInterface
import com.uta.market.data.source.repository.AuthRepository
import com.uta.market.data.source.repository.ProductsRepoInterface
import com.uta.market.data.source.repository.ProductsRepository

object ServiceLocator {
	private var database: AppDatabase? = null
	private val lock = Any()

	@Volatile
	var authRepository: AuthRepoInterface? = null
		@VisibleForTesting set

	@Volatile
	var productsRepository: ProductsRepoInterface? = null
		@VisibleForTesting set

	fun provideAuthRepository(context: Context): AuthRepoInterface {
		synchronized(this) {
			return authRepository ?: createAuthRepository(context)
		}
	}

	fun provideProductsRepository(context: Context): ProductsRepoInterface {
		synchronized(this) {
			return productsRepository ?: createProductsRepository(context)
		}
	}

	@VisibleForTesting
	fun resetRepository() {
		synchronized(lock) {
			database?.apply {
				clearAllTables()
				close()
			}
			database = null
			authRepository = null
		}
	}

	private fun createProductsRepository(context: Context): ProductsRepoInterface {
		val newRepo =
			ProductsRepository(ProductsRemoteDataSource(), createProductsLocalDataSource(context))
		productsRepository = newRepo
		return newRepo
	}

	private fun createAuthRepository(context: Context): AuthRepoInterface {
		val appSession = AppSessionManager(context.applicationContext)
		val newRepo =
			AuthRepository(createUserLocalDataSource(context), AuthRemoteDataSource(), appSession)
		authRepository = newRepo
		return newRepo
	}

	private fun createProductsLocalDataSource(context: Context): ProductDataSource {
		val database = database ?: AppDatabase.getInstance(context.applicationContext)
		return ProductsLocalDataSource(database.productsDao())
	}

	private fun createUserLocalDataSource(context: Context): UserDataSource {
		val database = database ?: AppDatabase.getInstance(context.applicationContext)
		return UserLocalDataSource(database.userDao())
	}
}