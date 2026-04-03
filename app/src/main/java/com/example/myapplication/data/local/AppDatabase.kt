package com.example.myapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.data.local.dao.CategoryDao
import com.example.myapplication.data.local.dao.OrderDao
import com.example.myapplication.data.local.dao.ProductDao
import com.example.myapplication.data.local.entity.CategoryEntity
import com.example.myapplication.data.local.entity.OrderDetailEntity
import com.example.myapplication.data.local.entity.OrderEntity
import com.example.myapplication.data.local.entity.ProductEntity
import com.example.myapplication.data.local.entity.UserEntity
import com.example.shoppingapp.data.local.dao.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Calendar


@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        ProductEntity::class,
        OrderEntity::class,
        OrderDetailEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun orderDao(): OrderDao

    private class SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                ioScope.launch {
                    database.userDao().insertUsers(
                        listOf(
                            UserEntity(
                                username = "admin",
                                password = "123456",
                                fullName = "System Admin"
                            ),
                            UserEntity(
                                username = "user1",
                                password = "123456",
                                fullName = "Nguyen Van A"
                            )
                        )
                    )
                    database.categoryDao().insertCategories(
                        listOf(
                            CategoryEntity(1, "Trái cây nội địa"),
                            CategoryEntity(2, "Trái cây nhập khẩu"),
                            CategoryEntity(3, "Trái cây cắt sẵn"),
                            CategoryEntity(4, "Nước ép & Sinh tố")
                        )
                    )
                    database.productDao().insertProducts(
                        listOf(
                            ProductEntity(
                                imageUrl = "https://images.unsplash.com/photo-1567306226416-28f0efdc88ce?w=600",
                                name = "Táo đỏ Việt Nam",
                                description = "Táo giòn ngọt, giàu vitamin",
                                price = 45000.0,
                                categoryId = 1,
                                expiryDateMillis = endOfDayMillis(2026, 4, 18)
                            ),
                            ProductEntity(
                                imageUrl = "https://images.unsplash.com/photo-1574226516831-e1dff420e8f8?w=600",
                                name = "Chuối tiêu",
                                description = "Chuối chín tự nhiên, thơm ngon",
                                price = 30000.0,
                                categoryId = 1,
                                expiryDateMillis = endOfDayMillis(2026, 4, 12)
                            ),
                            ProductEntity(
                                imageUrl = "https://images.unsplash.com/photo-1582515073490-dc4a4f4a91e5?w=600",
                                name = "Xoài cát Hòa Lộc",
                                description = "Xoài ngọt đậm, đặc sản miền Tây",
                                price = 80000.0,
                                categoryId = 1,
                                expiryDateMillis = endOfDayMillis(2026, 4, 25)
                            ),
                            ProductEntity(
                                imageUrl = "https://images.unsplash.com/photo-1619566636858-adf3ef46400b?w=600",
                                name = "Nho Mỹ",
                                description = "Nho nhập khẩu, quả to, mọng nước",
                                price = 150000.0,
                                categoryId = 2,
                                expiryDateMillis = endOfDayMillis(2026, 4, 20)
                            ),
                            ProductEntity(
                                imageUrl = "https://images.unsplash.com/photo-1601004890684-d8cbf643f5f2?w=600",
                                name = "Cherry Úc",
                                description = "Cherry tươi, vị ngọt thanh",
                                price = 250000.0,
                                categoryId = 2,
                                expiryDateMillis = endOfDayMillis(2026, 4, 15)
                            ),
                            ProductEntity(
                                imageUrl = "https://images.unsplash.com/photo-1576402187878-974f70c890a5?w=600",
                                name = "Kiwi New Zealand",
                                description = "Kiwi xanh giàu vitamin C",
                                price = 120000.0,
                                categoryId = 2,
                                expiryDateMillis = endOfDayMillis(2026, 4, 22)
                            ),
                            ProductEntity(
                                imageUrl = "https://images.unsplash.com/photo-1572441710534-680e4b3b7c0b?w=600",
                                name = "Dưa hấu cắt sẵn",
                                description = "Dưa hấu tươi, cắt hộp tiện lợi",
                                price = 40000.0,
                                categoryId = 3,
                                expiryDateMillis = endOfDayMillis(2026, 4, 8)
                            ),
                            ProductEntity(
                                imageUrl = "https://images.unsplash.com/photo-1592928303267-0c0c8c5a2dd6?w=600",
                                name = "Trái cây mix",
                                description = "Hộp trái cây mix nhiều loại",
                                price = 70000.0,
                                categoryId = 3,
                                expiryDateMillis = endOfDayMillis(2026, 4, 10)
                            ),
                            ProductEntity(
                                imageUrl = "https://images.unsplash.com/photo-1576402187878-974f70c890a5?w=600",
                                name = "Dứa cắt sẵn",
                                description = "Dứa ngọt, đã gọt sẵn",
                                price = 35000.0,
                                categoryId = 3,
                                expiryDateMillis = endOfDayMillis(2026, 4, 9)
                            ),
                            ProductEntity(
                                imageUrl = "https://images.unsplash.com/photo-1553531889-56cc480ac5cb?w=600",
                                name = "Nước ép cam",
                                description = "Nước cam tươi nguyên chất",
                                price = 30000.0,
                                categoryId = 4,
                                expiryDateMillis = endOfDayMillis(2026, 4, 7)
                            ),
                            ProductEntity(
                                imageUrl = "https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=600",
                                name = "Sinh tố bơ",
                                description = "Sinh tố bơ béo ngậy, thơm ngon",
                                price = 45000.0,
                                categoryId = 4,
                                expiryDateMillis = endOfDayMillis(2026, 4, 6)
                            ),
                            ProductEntity(
                                imageUrl = "https://images.unsplash.com/photo-1505253716362-afaea1d3d1af?w=600",
                                name = "Nước ép dưa hấu",
                                description = "Giải nhiệt ngày nóng",
                                price = 25000.0,
                                categoryId = 4,
                                expiryDateMillis = endOfDayMillis(2026, 4, 5)
                            ),
                            ProductEntity(
                                imageUrl = "https://images.unsplash.com/photo-1603569283847-aa295f0d016a?w=600",
                                name = "Ổi",
                                description = "Ổi giòn, ít hạt, giàu vitamin",
                                price = 35000.0,
                                categoryId = 1,
                                expiryDateMillis = endOfDayMillis(2026, 4, 16)
                            ),
                            ProductEntity(
                                imageUrl = "https://images.unsplash.com/photo-1589927986089-35812388d1f4?w=600",
                                name = "Cam sành",
                                description = "Cam mọng nước, vị ngọt thanh",
                                price = 50000.0,
                                categoryId = 1,
                                expiryDateMillis = endOfDayMillis(2026, 4, 28)
                            ),
                            ProductEntity(
                                imageUrl = "https://images.unsplash.com/photo-1582284540020-8acbe03f4924?w=600",
                                name = "Lê Hàn Quốc",
                                description = "Lê giòn, ngọt, nhập khẩu",
                                price = 90000.0,
                                categoryId = 2,
                                expiryDateMillis = endOfDayMillis(2026, 5, 2)
                            )
                        )
                    )
                }
            }
        }
    }

    companion object {
        private fun endOfDayMillis(year: Int, month1Based: Int, day: Int): Long {
            return Calendar.getInstance().apply {
                set(year, month1Based - 1, day, 23, 59, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null
        private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shopping_app.db"
                )
                    .addCallback(SeedCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}