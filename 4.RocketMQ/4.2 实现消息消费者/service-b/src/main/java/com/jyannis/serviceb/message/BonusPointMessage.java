package com.jyannis.serviceb.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonusPointMessage {

    /**
     * 待加分的用户id
     */
    private Integer userId;

    /**
     * 加的分值
     */
    private Double point;

}
