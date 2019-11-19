# nuxeo-configurable-es-audit-page-provider

This plug-in redefines **ES** page provider `DOCUMENT_HISTORY_PROVIDER` in order to easily customize its ES DSL queries through a simgle **page provider** override.

# Requirements

Building requires the following software:

* git
* maven

Running this plugin requires that the **Nuxeo Platform** is configured to store documents' audit trail in `elasticsearch`.

# Build

```
git clone ...
cd nuxeo-configurable-es-audit-page-provider

mvn clean install
```

# Configuration

The default implementation class for page provider `DOCUMENT_HISTORY_PROVIDER` gets either 1 or 2 parameters, depending on the use case. The implementation class brought by this addon make it possible to provide the **ES DSL query** retrieving the log entries. To simplify things, it always have 2 parameters (the implementation class extrapolate the 2nd parameter if not provided), the first parameter being the document's UUID. 

Here is an example using a **Studio XML extension**, which excludes the `download` audit log entries that are not the main blob nor the attachments:
```xml
  <require>org.nuxeo.ecm.platform.audit.PageProviderservice.es.contrib</require>

  <extension target="org.nuxeo.ecm.platform.query.api.PageProviderService" point="providers">

    <genericPageProvider class="com.acme.elasticsearch.audit.pageprovider.ESDocumentHistoryConfigurablePageProvider" name="DOCUMENT_HISTORY_PROVIDER">
      <whereClause docType="BasicAuditSearch">
        <fixedPart>
{
 "bool" : {
      "must" : [
        { "term": { "docUUID": "?" } },
        { "range": { "eventDate": { "lte": "?" } } },
        { "bool": {
          "should": [
            { "bool": {
              "must": [
                { "term": { "eventId": { "value": "download" } } },
                { "bool": {
                  "should": [
                    { "term": { "extended.blobXPath": "file:content" } },
                    { "prefix": { "extended.blobXPath": "files:files" } }
                  ]
                } }
              ]
            } },
            { "bool": {
              "must_not": [ { "term": { "eventId": "download" } } ]
            } }
          ]
        } }
      ]
 }
}
        </fixedPart>
        <predicate operator="BETWEEN" parameter="eventDate">
          <field name="startDate" schema="basicauditsearch"/>
          <field name="endDate" schema="basicauditsearch"/>
        </predicate>
        <predicate operator="IN" parameter="category">
          <field name="eventCategories" schema="basicauditsearch"/>
        </predicate>
        <predicate operator="IN" parameter="eventId">
          <field name="eventIds" schema="basicauditsearch"/>
        </predicate>
        <predicate operator="IN" parameter="principalName">
          <field name="principalNames" schema="basicauditsearch"/>
        </predicate>
      </whereClause>
      <sort ascending="false" column="eventDate"/>
        <pageSize>10</pageSize>
        <maxPageSize>1000</maxPageSize>
    </genericPageProvider>

  </extension>
```
# Installation

```
nuxeoctl mp-install nuxeo-configurable-es-audit-page-provider/nuxeo-configurable-es-audit-page-provider-package/target/nuxeo-configurable-es-audit-page-provider-package*.zip
```

# Support

**These features are not part of the Nuxeo Production platform, they are not supported**

These solutions are provided for inspiration and we encourage customers to use them as code samples and learning resources.

This is a moving project (no API maintenance, no deprecation process, etc.) If any of these solutions are found to be useful for the Nuxeo Platform in general, they will be integrated directly into platform, not maintained here.


# Licensing

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)


# About Nuxeo

Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris.

More information is available at [www.nuxeo.com](http://www.nuxeo.com).
