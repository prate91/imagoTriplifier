
package cnr.isti.aimh.imago.pojo.full;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "iri",
    "lat",
    "lon",
    "name",
    "alias",
    "country"
})
@Generated("jsonschema2pojo")
public class Place {

    @JsonProperty("iri")
    private String iri;
    @JsonProperty("lat")
    private String lat;
    @JsonProperty("lon")
    private String lon;
    @JsonProperty("name")
    private String name;
    @JsonProperty("alias")
    private List<String> alias;
    @JsonProperty("country")
    private Country country;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("iri")
    public String getIri() {
        return iri;
    }

    @JsonProperty("iri")
    public void setIri(String iri) {
        this.iri = iri;
    }

    @JsonProperty("lat")
    public String getLat() {
        return lat;
    }

    @JsonProperty("lat")
    public void setLat(String lat) {
        this.lat = lat;
    }

    @JsonProperty("lon")
    public String getLon() {
        return lon;
    }

    @JsonProperty("lon")
    public void setLon(String lon) {
        this.lon = lon;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("alias")
    public List<String> getAlias() {
        return alias;
    }

    @JsonProperty("alias")
    public void setAlias(List<String> alias) {
        this.alias = alias;
    }

    @JsonProperty("country")
    public Country getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(Country country) {
        this.country = country;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
