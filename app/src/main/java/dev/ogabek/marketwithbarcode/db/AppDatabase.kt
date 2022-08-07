package dev.ogabek.marketwithbarcode.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.ogabek.marketwithbarcode.model.Product

@Database(entities = [Product::class], version = 1, exportSchema = true)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getProductsDao(): ProductDao

    companion object {

        private var DB_INSTANCE: AppDatabase? = null

        fun getAppDBInstance(context: Context): AppDatabase {
            if (DB_INSTANCE == null) {
                DB_INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "narsalar"
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return DB_INSTANCE!!
        }

    }

}