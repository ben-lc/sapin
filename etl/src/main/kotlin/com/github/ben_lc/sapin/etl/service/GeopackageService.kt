package com.github.ben_lc.sapin.etl.service

import com.github.ben_lc.sapin.etl.LocationGeopackageProperties
import com.github.ben_lc.sapin.etl.model.LocationEtl
import com.github.ben_lc.sapin.etl.repository.LocationEtlRepository
import com.github.ben_lc.sapin.etl.util.getAttribute
import com.github.ben_lc.sapin.etl.util.getAttributeOrNullForValue
import java.io.File
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.geotools.data.DataStoreFinder
import org.locationtech.jts.geom.Geometry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GeopackageService(val locationRepo: LocationEtlRepository) {

  val logger: Logger = LoggerFactory.getLogger(GeopackageService::class.java)

  suspend fun loadLocation(gpkgFolder: File, gpkgProps: List<LocationGeopackageProperties>) =
      coroutineScope {
        val params =
            mapOf("dbtype" to "geopkg", "database" to gpkgFolder.absolutePath, "read_only" to true)

        launch {
          val datastore = DataStoreFinder.getDataStore(params)

          gpkgProps.forEach { props ->
            logger.info("Start loading features from: ${props.tableName}")
            val featureIterator = datastore.getFeatureSource(props.tableName).features.features()

            val locations = flow {
              while (featureIterator.hasNext()) {
                val feature = featureIterator.next()
                val geom = (feature.defaultGeometry as Geometry)
                val location =
                    LocationEtl(
                        level = props.level,
                        name = feature.getAttribute<String>(props.nameColumnName)!!,
                        isoId =
                            feature.getAttributeOrNullForValue<String>(props.isoIdColumnName, "NA"),
                        levelName = feature.getAttribute<String>(props.levelNameColumnName),
                        levelNameEn = feature.getAttribute<String>(props.levelNameEnColumnName),
                        geom = geom.toText(),
                        srid = geom.srid,
                        srcId = feature.getAttribute<String>(props.srcIdColumnName)!!,
                        srcParentId = feature.getAttribute<String>(props.srcParentIdColumnName))
                emit(location)
              }
            }
            locationRepo.saveAll(locations)
            logger.info("Complete")
            featureIterator.close()
          }
          datastore.dispose()
        }
      }
}
