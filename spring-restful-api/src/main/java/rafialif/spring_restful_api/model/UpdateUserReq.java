package rafialif.spring_restful_api.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserReq {
    @Size(max = 100)
    private String name;

    @Size(max = 100)
    private String password;
}
