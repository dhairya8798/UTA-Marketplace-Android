package com.uta.market.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.uta.market.data.Product
import com.uta.market.data.UserData
import com.uta.market.data.utils.DateTypeConvertors
import com.uta.market.data.utils.ListTypeConverter
import com.uta.market.data.utils.ObjectListTypeConvertor

@Database(entities = [UserData::class, Product::class], version = 2)
@TypeConverters(ListTypeConverter::class, ObjectListTypeConvertor::class, DateTypeConvertors::class)
abstract class AppDatabase : RoomDatabase() {
	abstract fun userDao(): UserDao
	abstract fun productsDao(): ProductsDao

	companion object {
		@Volatile
		private var INSTANCE: AppDatabase? = null

		fun getInstance(context: Context): AppDatabase =
			INSTANCE ?: synchronized(this) {
				INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
			}

		private fun buildDatabase(context: Context) =
			Room.databaseBuilder(
				context.applicationContext,
				AppDatabase::class.java, "AppDB"
			)
				.fallbackToDestructiveMigration()
				.allowMainThreadQueries()
				.build()
	}
}