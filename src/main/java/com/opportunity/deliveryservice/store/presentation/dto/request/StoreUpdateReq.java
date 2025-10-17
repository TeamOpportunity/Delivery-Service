package com.opportunity.deliveryservice.store.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreUpdateReq {

    private List<Integer> categoryIds;
    private String city;
    private String gu;
    private String detailAddress;
    private String content;
    private String name;
    private int minOrderPrice;
    private String startTime; // "HH:mm" format
    private String endTime;   // "HH:mm" format
}
