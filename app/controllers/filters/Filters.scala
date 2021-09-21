package controllers.filters

import play.api.http.{ DefaultHttpFilters, EnabledFilters }

import javax.inject.Inject

class Filters @Inject() (enabledFilters: EnabledFilters, signatureFilter: SignatureFilter)
    extends DefaultHttpFilters(signatureFilter +: enabledFilters.filters: _*)
