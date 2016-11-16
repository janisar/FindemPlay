import javax.inject.Inject

import play.api.http.DefaultHttpFilters
import play.filters.cors.CORSFilter

/**
  * Created by saarlane on 16/11/16.
  */
class Filters @Inject() (corsFilter: CORSFilter) extends DefaultHttpFilters(corsFilter)
