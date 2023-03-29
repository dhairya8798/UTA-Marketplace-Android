package com.uta.market

import android.app.Application
import com.uta.market.data.source.repository.AuthRepoInterface
import com.uta.market.data.source.repository.ProductsRepoInterface

class UTAMarketApp : Application() {
	val authRepository: AuthRepoInterface
		get() = ServiceLocator.provideAuthRepository(this)

	val productsRepository: ProductsRepoInterface
		get() = ServiceLocator.provideProductsRepository(this)

	override fun onCreate() {
		super.onCreate()
	}
}