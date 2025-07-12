-- photographyShared/src/commonMain/sqldelight/com/x3squaredcircles/photographyshared/db/Weather.sq

selectAll:
SELECT * FROM Weather
WHERE isDeleted = 0
ORDER BY lastUpdate DESC;

selectById:
SELECT * FROM Weather
WHERE id = ? AND isDeleted = 0;

selectByLocationId:
SELECT * FROM Weather
WHERE locationId = ? AND isDeleted = 0
ORDER BY lastUpdate DESC
LIMIT 1;

selectByCoordinates:
SELECT * FROM Weather
WHERE ABS(latitude - ?) < 0.001 
  AND ABS(longitude - ?) < 0.001 
  AND isDeleted = 0
ORDER BY lastUpdate DESC
LIMIT 1;

selectByLocationAndTimeRange:
SELECT * FROM Weather
WHERE locationId = ? 
  AND lastUpdate >= ? 
  AND lastUpdate <= ? 
  AND isDeleted = 0
ORDER BY lastUpdate DESC;

selectRecent:
SELECT * FROM Weather
WHERE isDeleted = 0
ORDER BY lastUpdate DESC
LIMIT ?;

selectExpired:
SELECT * FROM Weather
WHERE lastUpdate < ? AND isDeleted = 0
ORDER BY lastUpdate ASC;

insert:
INSERT INTO Weather (
  locationId, latitude, longitude, timezone, timezoneOffset, lastUpdate,
  temperature, feelsLike, humidity, pressure, visibility, uvIndex,
  windSpeed, windDirection, windGust, cloudCover, condition, description,
  icon, sunrise, sunset, isDeleted
) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

update:
UPDATE Weather
SET locationId = ?, latitude = ?, longitude = ?, timezone = ?,
    timezoneOffset = ?, lastUpdate = ?, temperature = ?, feelsLike = ?,
    humidity = ?, pressure = ?, visibility = ?, uvIndex = ?, windSpeed = ?,
    windDirection = ?, windGust = ?, cloudCover = ?, condition = ?,
    description = ?, icon = ?, sunrise = ?, sunset = ?, isDeleted = ?
WHERE id = ?;

softDelete:
UPDATE Weather
SET isDeleted = 1
WHERE id = ?;

softDeleteByLocationId:
UPDATE Weather
SET isDeleted = 1
WHERE locationId = ?;

hasFreshData:
SELECT COUNT(*) > 0 FROM Weather
WHERE locationId = ? 
  AND lastUpdate >= ? 
  AND isDeleted = 0;

hasFreshDataForCoordinates:
SELECT COUNT(*) > 0 FROM Weather
WHERE ABS(latitude - ?) < 0.001 
  AND ABS(longitude - ?) < 0.001 
  AND lastUpdate >= ? 
  AND isDeleted = 0;

cleanupExpired:
UPDATE Weather
SET isDeleted = 1
WHERE lastUpdate < ?;

getCount:
SELECT COUNT(*) FROM Weather
WHERE isDeleted = 0;

lastInsertRowId:
SELECT last_insert_rowid();