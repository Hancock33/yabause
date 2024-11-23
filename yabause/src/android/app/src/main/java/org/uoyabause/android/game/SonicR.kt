package org.uoyabause.android.game

import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import org.devmiyax.yabasanshiro.R
import org.uoyabause.android.YabauseApplication

/*
  Offset: 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F
00000000: 00 00 00 01 00 00 00 00 00 00 00 00 01 01 06 11    ................
00000010: 00 00 [07 A4] 00 00 [26 B2] 02 00 [69 78] 03 00 [69 78]    ...$..&2..ix..ix
00000020: 04 00 1F 40 00 00 69 78 01 00 69 78 02 00 69 78    ...@..ix..ix..ix
00000030: 03 00 1F 40 04 00 69 78 00 00 69 78 01 00 69 78    ...@..ix..ix..ix
00000040: 02 00 1F 40 03 00 69 78 04 00 69 78 00 00 69 78    ...@..ix..ix..ix
00000050: 01 00 1F 40 02 00 69 78 03 00 69 78 04 00 69 78    ...@..ix..ix..ix
00000060: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00    ................
00000070: 00 00 00 00 00 00 00 00 00 00 00 00 01 01 06 11    ........\> Could not resolve all files for configuration ':classpath'.
   > Could not resolve com.google.firebase:firebase-crashlytics-gradle:3.0.2.
     Required by:
         project :
      > No matching variant of com.google.firebase:firebase-crashlytics-gradle:3.0.2 was found. The consumer was configured to find a library for use during runtime, compatible with Java 11, packaged as a jar, and its dependencies declared externally, as well as attribute 'org.gradle.plugin.api-version' with value '8.6' but:
          - Variant 'apiElements' capability com.google.firebase:firebase-crashlytics-gradle:3.0.2 declares a library, packaged as a jar, and its dependencies declared externally:
              - Incompatible because this component declares a component for use during compile-time, compatible with Java 17 and the consumer needed a component for use during runtime, compatible with Java 11
              - Other compatible attribute:
                  - Doesn't say anything about org.gradle.plugin.api-version (required '8.6')
          - Variant 'runtimeElements' capability com.google.firebase:firebase-crashlytics-gradle:3.0.2 declares a library for use during runtime, packaged as a jar, and its dependencies declared externally:
              - Incompatible because this component declares a component, compatible with Java 17 and the consumer needed a component, compatible with Java 11
              - Other compatible attribute:
                  - Doesn't say anything about org.gradle.plugin.api-version (required '8.6')
          - Variant 'sourcesElements' capability com.google.firebase:firebase-crashlytics-gradle:3.0.2 declares a component for use during runtime, and its dependencies declared externally:
              - Incompatible because this component declares documentation and the consumer needed a library
              - Other compatible attributes:
                  - Doesn't say anything about its target Java version (required compatibility with Java 11)
                  - Doesn't say anything about its elements (required them packaged as a jar)
                  - Doesn't say anything about org.gradle.plugin.api-version (required '8.6').........
00000080: 00 00 1F 40 01 00 69 78 02 00 69 78 03 00 69 78    ...@..ix..ix..ix
00000090: 04 00 1F 40 00 00 69 78 01 00 69 78 02 00 69 78    ...@..ix..ix..ix
000000a0: 03 00 1F 40 04 00 69 78 00 00 69 78 01 00 69 78    ...@..ix..ix..ix
000000b0: 02 00 1F 40 03 00 69 78 04 00 69 78 00 00 69 78    ...@..ix..ix..ix
000000c0: 01 00 1F 40 02 00 69 78 03 00 69 78 04 00 69 78    ...@..ix..ix..ix
000000d0: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00    ................
000000e0: 00 00 00 00 00 00 00 00 00 00 00 00 01 01 06 11    ................
000000f0: 00 00 1F 40 01 00 69 78 02 00 69 78 03 00 69 78    ...@..ix..ix..ix
00000100: 04 00 1F 40 00 00 69 78 01 00 69 78 02 00 69 78    ...@..ix..ix..ix
00000110: 03 00 1F 40 04 00 69 78 00 00 69 78 01 00 69 78    ...@..ix..ix..ix
00000120: 02 00 1F 40 03 00 69 78 04 00 69 78 00 00 69 78    ...@..ix..ix..ix
00000130: 01 00 1F 40 02 00 69 78 03 00 69 78 04 00 69 78    ...@..ix..ix..ix
00000140: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00    ................
*/

class SonicRRecord {
    var lapRecord: Int = 0
    var courseRecord: Int = 0
    var tagRecord: Int = 0
    var balloonRecord: Int = 0
}

class SonicRBackup {
    var records: MutableList<SonicRRecord> = mutableListOf<SonicRRecord>()
    var totalTime: Long = 0

    constructor(bin: ByteArray) {
        totalTime = 0
        for (i in 0..4) {
            var record = SonicRRecord()
            val si = i * 0x10 + 0x10
            record.lapRecord = (((bin[si + 0x02].toInt() shl 8) or (bin[si + 0x03].toInt() and 0xFF)) * 1.6666).toInt() * 10
            record.courseRecord = (((bin[si + 0x6].toInt() shl 8) or (bin[si + 0x7].toInt() and 0xFF))) * 10
            record.tagRecord = (((bin[si + 0xA].toInt() shl 8) or (bin[si + 0xB].toInt() and 0xFF))) * 10
            record.balloonRecord = (((bin[si + 0xE].toInt() shl 8) or (bin[si + 0xF].toInt() and 0xFF))) * 10
            records.add(record)
            totalTime += record.courseRecord
        }
    }
}

class SonicR : BaseGame {

    constructor() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.setDefaultsAsync(R.xml.config)

        leaderBoards = mutableListOf<LeaderBoard>()
        leaderBoards?.add(LeaderBoard("Resort Island", remoteConfig.getString("sonicr_1")))
        leaderBoards?.add(LeaderBoard("Radical City", remoteConfig.getString("sonicr_2")))
        leaderBoards?.add(LeaderBoard("Regal Ruin", remoteConfig.getString("sonicr_3")))
        leaderBoards?.add(LeaderBoard("Reactive Factory", remoteConfig.getString("sonicr_4")))
        leaderBoards?.add(LeaderBoard("Radiant Emerald", remoteConfig.getString("sonicr_5")))
    }

    override fun onBackUpUpdated(before: ByteArray, after: ByteArray) {

        var beforeRecord = SonicRBackup(before)
        var afterRecord = SonicRBackup(after)

        for (i in 0..4) {
            if (afterRecord.records[i].courseRecord < beforeRecord.records[i].courseRecord) {
                val context = YabauseApplication.appContext
                val score = afterRecord.records[i].courseRecord.toLong()
                val account = GoogleSignIn.getLastSignedInAccount(context)
                val gid = leaderBoards?.get(i)?.id
                if (account != null && gid != null) {
                    Games.getLeaderboardsClient(
                        context,
                        account
                    )
                    .submitScore(gid, score)
                }

                val bundle = Bundle()
                bundle.putLong(FirebaseAnalytics.Param.SCORE, score)
                bundle.putString("leaderboard_id", leaderBoards?.get(i)?.id)
                val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.POST_SCORE, bundle)
                leaderBoards?.get(i)?.id?.let { this.uievent.onNewRecord(it) }
            }
        }
    }
}
