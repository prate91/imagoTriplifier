
package cnr.isti.aimh.imago.pojo.full;

import java.util.HashMap;
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
    "birthDate",
    "deathDate",
    "floruitDate",
    "episcopusDate"
})
@Generated("jsonschema2pojo")
public class AuthorDate {

    @JsonProperty("birthDate")
    private Date birthDate;
    @JsonProperty("deathDate")
    private Date deathDate;
    @JsonProperty("floruitDate")
    private Date floruitDate;
    @JsonProperty("episcopusDate")
    private Date episcopusDate;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("birthDate")
    public Date getBirthDate() {
        return birthDate;
    }

    @JsonProperty("birthDate")
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    @JsonProperty("deathDate")
    public Date getDeathDate() {
        return deathDate;
    }

    @JsonProperty("deathDate")
    public void setDeathDate(Date deathDate) {
        this.deathDate = deathDate;
    }

    @JsonProperty("floruitDate")
    public Date getFloruitDate() {
        return floruitDate;
    }

    @JsonProperty("floruitDate")
    public void setFloruitDate(Date floruitDate) {
        this.floruitDate = floruitDate;
    }

    @JsonProperty("episcopusDate")
    public Date getEpiscopusDate() {
        return episcopusDate;
    }

    @JsonProperty("episcopusDate")
    public void setEpiscopusDate(Date episcopusDate) {
        this.episcopusDate = episcopusDate;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    @Override
    public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("birthDate");
    sb.append("\n");
    sb.append("\t\t\t\t");
    sb.append(((this.birthDate == null)?"<null>":this.birthDate));
    sb.append("\n");
    sb.append("\t\tdeathDate");
    sb.append("\n");
    sb.append("\t\t\t\t");
    sb.append(((this.deathDate == null)?"<null>":this.deathDate));
    sb.append("\n");
    sb.append("\t\tfloruitDate");
    sb.append("\n");
    sb.append("\t\t\t\t");
    sb.append(((this.floruitDate == null)?"<null>":this.floruitDate));
    sb.append("\n");
    sb.append("\t\tepiscopusDate");
    sb.append("\n");
    sb.append("\t\t\t\t");
    sb.append(((this.episcopusDate == null)?"<null>":this.episcopusDate));
    
    return sb.toString();
    }
}
