package com.acme.elasticsearch.audit.pageprovider;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.elasticsearch.audit.pageprovider.ESDocumentHistoryPageProvider;

public class ESDocumentHistoryConfigurablePageProvider extends ESDocumentHistoryPageProvider {

  protected Log log = LogFactory.getLog(ESDocumentHistoryConfigurablePageProvider.class);

  private static final long serialVersionUID = 1L;

  @Override
  protected String getFixedPart() {
      String query = singleQuery;
      if (getDefinition().getWhereClause() != null && StringUtils.isNotEmpty(getDefinition().getWhereClause().getFixedPart())) {
          query = getDefinition().getWhereClause().getFixedPart();
      }
      if (log.isTraceEnabled()) {
          log.trace("parameters: " + Arrays.deepToString(Arrays.stream(getParameters()).map(Object::toString).toArray(String[]::new)));
          log.trace(query);
      }
      return query;
  }

  @Override
  public Object[] getParameters() {
      super.getParameters();
      if (newParams.length == 3) {
          Object[] params = new Object[] { newParams[0], newParams[2] };
          return params;
      } else {
          Object[] params = new Object[] { newParams[0], "now" };
          return params;
      }
  }
}
