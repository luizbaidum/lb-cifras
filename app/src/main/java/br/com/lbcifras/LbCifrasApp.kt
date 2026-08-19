package br.com.lbcifras

import android.app.Application
import br.com.lbcifras.data.importer.ChordLinkImporter
import br.com.lbcifras.data.local.AppDatabase
import br.com.lbcifras.data.repository.OfflineSongRepository
import br.com.lbcifras.domain.repository.SongRepository

class LbCifrasApp : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.build(this)
    }

    val songRepository: SongRepository by lazy {
        OfflineSongRepository(database.songDao())
    }

    val chordLinkImporter: ChordLinkImporter by lazy {
        ChordLinkImporter()
    }
}
