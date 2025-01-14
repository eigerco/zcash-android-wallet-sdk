package cash.z.ecc.android.sdk.internal.db.derived

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import cash.z.ecc.android.sdk.exception.CompactBlockProcessorException
import cash.z.ecc.android.sdk.internal.NoBackupContextWrapper
import cash.z.ecc.android.sdk.internal.Twig
import cash.z.ecc.android.sdk.internal.TypesafeBackend
import cash.z.ecc.android.sdk.internal.db.ReadOnlySupportSqliteOpenHelper
import cash.z.ecc.android.sdk.internal.model.Checkpoint
import cash.z.ecc.android.sdk.model.BlockHeight
import cash.z.ecc.android.sdk.model.ZcashNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

internal class DerivedDataDb private constructor(
    zcashNetwork: ZcashNetwork,
    private val sqliteDatabase: SupportSQLiteDatabase
) {
    val accountTable = AccountTable(sqliteDatabase)

    val transactionTable = TransactionTable(zcashNetwork, sqliteDatabase)

    val allTransactionView = AllTransactionView(zcashNetwork, sqliteDatabase)

    val txOutputsView = TxOutputsView(zcashNetwork, sqliteDatabase)

    suspend fun close() {
        withContext(Dispatchers.IO) {
            sqliteDatabase.close()
        }
    }

    companion object {
        // Database migrations are managed by librustzcash.  This is a hard-coded value to ensure that Android's
        // SqliteOpenHelper is happy
        private const val DATABASE_VERSION = 8

        @Suppress("LongParameterList")
        suspend fun new(
            context: Context,
            backend: TypesafeBackend,
            databaseFile: File,
            zcashNetwork: ZcashNetwork,
            checkpoint: Checkpoint,
            seed: ByteArray?,
            numberOfAccounts: Int,
            recoverUntil: BlockHeight?
        ): DerivedDataDb {
            runCatching {
                val result = backend.initDataDb(seed)
                if (result < 0) {
                    throw CompactBlockProcessorException.Uninitialized()
                }
            }.onFailure {
                throw CompactBlockProcessorException.Uninitialized(it)
            }

            val database = ReadOnlySupportSqliteOpenHelper.openExistingDatabaseAsReadOnly(
                NoBackupContextWrapper(
                    context,
                    databaseFile.parentFile!!
                ),
                databaseFile,
                DATABASE_VERSION
            )

            val dataDb = DerivedDataDb(zcashNetwork, database)

            // If a seed is provided, fill in the accounts.
            seed?.let { checkedSeed ->
                // toInt() should be safe because we expect very few accounts
                val missingAccounts = numberOfAccounts - dataDb.accountTable.count().toInt()
                require(missingAccounts >= 0) {
                    "Unexpected number of accounts: $missingAccounts"
                }
                repeat(missingAccounts) {
                    runCatching {
                        backend.createAccountAndGetSpendingKey(
                            seed = checkedSeed,
                            treeState = checkpoint.treeState(),
                            recoverUntil = recoverUntil
                        )
                    }.onFailure {
                        Twig.error(it) { "Create account failed." }
                    }
                }
            }

            return dataDb
        }
    }
}
