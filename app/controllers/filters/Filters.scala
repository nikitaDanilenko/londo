package controllers.filters

import play.api.http.{ DefaultHttpFilters, EnabledFilters }

import javax.inject.Inject

class Filters @Inject() (defaultFilters: EnabledFilters, signatureFilter: SignatureFilter)
    extends DefaultHttpFilters(signatureFilter +: defaultFilters.filters: _*)
