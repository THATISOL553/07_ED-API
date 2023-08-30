package com.edapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.edapi.response.EligResponse;
import com.edapi.service.EligService;

@RestController
public class EdRestController {

	@Autowired
	private EligService eligService;
	
	@GetMapping("/eligibility/{caseNum}")
	public EligResponse determineEligibility(@PathVariable Long caseNum) {
		EligResponse eligResponse = eligService.determineEligibility(caseNum);
		return eligResponse;
	}
}
