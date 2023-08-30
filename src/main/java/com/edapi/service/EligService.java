package com.edapi.service;

import com.edapi.response.EligResponse;

public interface EligService {
	
	public EligResponse determineEligibility(Long caseNum);
}
