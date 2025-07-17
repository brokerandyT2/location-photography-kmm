// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/StellariumMeteorShowerParser.kt
package com.x3squaredcircles.photography.infrastructure.services

import co.touchlab.kermit.Logger
import com.x3squaredcircles.photography.domain.entities.MeteorShower
import com.x3squaredcircles.photography.domain.entities.MeteorShowerActivity
import com.x3squaredcircles.photography.domain.entities.MeteorShowerData
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class StellariumMeteorShowerParser(
    private val logger: Logger
) {
    companion object {
        private const val MIN_ZHR_THRESHOLD = 5
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun parseStellariumData(stellariumJson: String): MeteorShowerData {
        return try {
            val stellariumData = json.decodeFromString<StellariumRoot>(stellariumJson)

            if (stellariumData.showers == null) {
                logger.w("Failed to parse Stellarium data or no showers found")
                return MeteorShowerData()
            }

            val showers = mutableListOf<MeteorShower>()

            stellariumData.showers.forEach { (code, showerData) ->
                try {
                    val parsedShower = parseSingleShower(code, showerData)
                    parsedShower?.let { showers.add(it) }
                } catch (ex: Exception) {
                    logger.w(ex) { "Failed to parse shower $code" }
                }
            }

            logger.i("Successfully parsed ${showers.size} meteor showers from Stellarium data")

            MeteorShowerData(showers)
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to parse Stellarium JSON data" }
            MeteorShowerData()
        }
    }

    private fun parseSingleShower(code: String, showerData: StellariumShower): MeteorShower? {
        val activity = showerData.activity?.firstOrNull() ?: return null

        val startDate = parseDate(activity.start) ?: return null
        val endDate = parseDate(activity.finish) ?: return null
        val peakDate = parseDate(activity.peak) ?: return null

        val zhr = activity.zhr ?: 0
        if (zhr < MIN_ZHR_THRESHOLD) return null

        val radiantRA = parseCoordinate(showerData.radiantAlpha) ?: 0.0
        val radiantDec = parseCoordinate(showerData.radiantDelta) ?: 0.0
        val speed = showerData.speed ?: 0
        val parentBody = cleanParentBody(showerData.parentObj ?: "")

        val meteorActivity = MeteorShowerActivity(
            startDate = startDate,
            peakDate = peakDate,
            endDate = endDate,
            peakZHR = zhr,
            variable = activity.variable ?: ""
        )

        return MeteorShower(
            code = code,
            designation = showerData.designation ?: code,
            activity = meteorActivity,
            radiantRA = radiantRA,
            radiantDec = radiantDec,
            speedKmS = speed,
            parentBody = parentBody
        )
    }

    private fun parseDate(dateStr: String?): LocalDate? {
        if (dateStr.isNullOrEmpty()) return null

        return try {
            val parts = dateStr.split("-")
            if (parts.size != 3) return null

            val month = parts[0].toIntOrNull() ?: return null
            val day = parts[1].toIntOrNull() ?: return null
            val year = if (parts[2].length == 2) 2000 + parts[2].toInt() else parts[2].toIntOrNull() ?: return null

            LocalDate(year, month, day)
        } catch (ex: Exception) {
            logger.w(ex) { "Failed to parse date: $dateStr" }
            null
        }
    }

    private fun parseCoordinate(coordStr: String?): Double? {
        if (coordStr.isNullOrEmpty()) return null

        return try {
            val parts = coordStr.split(":")
            if (parts.isEmpty()) return null

            val hours = parts[0].toDoubleOrNull() ?: return null
            val minutes = if (parts.size > 1) parts[1].toDoubleOrNull() ?: 0.0 else 0.0
            val seconds = if (parts.size > 2) parts[2].toDoubleOrNull() ?: 0.0 else 0.0

            hours + minutes / 60.0 + seconds / 3600.0
        } catch (ex: Exception) {
            logger.w(ex) { "Failed to parse coordinate: $coordStr" }
            null
        }
    }

    private fun cleanParentBody(parentObj: String): String {
        if (parentObj.isEmpty()) return ""

        return parentObj
            .replace("Comet ", "")
            .replace("Minor planet ", "")
            .trim()
    }
}

@Serializable
data class StellariumRoot(
    @SerialName("shortName")
    val shortName: String? = null,
    @SerialName("version")
    val version: Int = 0,
    @SerialName("showers")
    val showers: Map<String, StellariumShower>? = null
)

@Serializable
data class StellariumShower(
    @SerialName("designation")
    val designation: String? = null,
    @SerialName("activity")
    val activity: List<StellariumActivity>? = null,
    @SerialName("radiantAlpha")
    val radiantAlpha: String? = null,
    @SerialName("radiantDelta")
    val radiantDelta: String? = null,
    @SerialName("speed")
    val speed: Int? = null,
    @SerialName("parentObj")
    val parentObj: String? = null,
    @SerialName("driftAlpha")
    val driftAlpha: String? = null,
    @SerialName("driftDelta")
    val driftDelta: String? = null,
    @SerialName("pidx")
    val pidx: Double? = null,
    @SerialName("colors")
    val colors: List<StellariumColor>? = null
)

@Serializable
data class StellariumActivity(
    @SerialName("year")
    val year: String? = null,
    @SerialName("zhr")
    val zhr: Int? = null,
    @SerialName("variable")
    val variable: String? = null,
    @SerialName("start")
    val start: String? = null,
    @SerialName("finish")
    val finish: String? = null,
    @SerialName("peak")
    val peak: String? = null
)

@Serializable
data class StellariumColor(
    @SerialName("color")
    val color: String? = null,
    @SerialName("intensity")
    val intensity: Int = 0
)