package ro.neghina.sparkapp.entities;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LangRating implements Serializable {
    private String lang;
    private Integer score;
    private Boolean approved;
}
