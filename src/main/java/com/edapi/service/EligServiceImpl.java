package com.edapi.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edapi.entity.ChildrenEntity;
import com.edapi.entity.CitizenApiEntity;
import com.edapi.entity.CoTriggerEntity;
import com.edapi.entity.DcCasesEntity;
import com.edapi.entity.EducationEntity;
import com.edapi.entity.EligDtlsEntity;
import com.edapi.entity.IncomeEntity;
import com.edapi.entity.PlanEntity;
import com.edapi.repository.ChildRepository;
import com.edapi.repository.CitizenAppRepository;
import com.edapi.repository.CoTriggerRepository;
import com.edapi.repository.DcCasesRepository;
import com.edapi.repository.EducationRepository;
import com.edapi.repository.EligDtlsRepository;
import com.edapi.repository.IncomeRepository;
import com.edapi.repository.PlanRepository;
import com.edapi.response.EligResponse;

@Service
public class EligServiceImpl implements EligService {

	@Autowired
	private DcCasesRepository dcCasesRepo;

	@Autowired
	private PlanRepository planRepo;

	@Autowired
	private IncomeRepository incomeRepo;

	@Autowired
	private ChildRepository childRepo;

	@Autowired
	private CitizenAppRepository appRepo;

	@Autowired
	private EducationRepository eduRepo;

	@Autowired
	private EligDtlsRepository eligDtlsRepo;
	
	@Autowired
	private CoTriggerRepository coTriggerRepo;

	@Override
	public EligResponse determineEligibility(Long caseNum) {
		Optional<DcCasesEntity> caseEntity = dcCasesRepo.findById(caseNum);
		Integer planId = null;
		String planName = null;
		Integer appId = null;

		if (caseEntity.isPresent()) {
			planId = caseEntity.get().getPlanId();
			appId = caseEntity.get().getAppId();
		}

		Optional<PlanEntity> planEntity = planRepo.findById(planId);
		if (planEntity.isPresent()) {
			PlanEntity plan = planEntity.get();
			planName = plan.getPlanName();
		}
		Optional<CitizenApiEntity> findById = appRepo.findById(appId);
		Integer age =0;
		CitizenApiEntity citizenApiEntity =null;
		if (findById.isPresent()) {
			citizenApiEntity = findById.get();
			LocalDate dob = citizenApiEntity.getDob();
			LocalDate now = LocalDate.now();
			age = Period.between(dob, now).getYears();
		}
		EligResponse eligResponse = executePlanConditions(caseNum, planName, age);
		EligDtlsEntity eligEntity = new EligDtlsEntity();
		BeanUtils.copyProperties(eligResponse, eligEntity);

		eligEntity.setCaseNum(caseNum);
		eligEntity.setHolderName(citizenApiEntity.getFullName());
		eligEntity.setHolderSsn(citizenApiEntity.getSsn());
		eligDtlsRepo.save(eligEntity);
		
		CoTriggerEntity coEntity = new CoTriggerEntity();
		coEntity.setCaseNum(caseNum);
		coEntity.setTrgStatus("Pending");
		
		coTriggerRepo.save(coEntity);
		return eligResponse;
	}

	private EligResponse executePlanConditions(Long caseNum, String planName, Integer age) {
		EligResponse response = new EligResponse();
		response.setPlanName(planName);

		IncomeEntity income = incomeRepo.findByCaseNum(caseNum);
		if ("SNAP".equals(planName)) {
			Double empIncome = income.getEmpIncome();
			if (empIncome <= 300) {
				response.setPlanStatus("Approved");
			} else {
				response.setPlanStatus("Denied");
				response.setDenialReason("High Income");
			}

		} else if ("CCAP".equals(planName)) {

			boolean ageCondition = true;
			boolean kidsCountCondition = false;
			List<ChildrenEntity> childs = childRepo.findByCaseNum(caseNum);
			if (!childs.isEmpty()) {
				kidsCountCondition = true;
				for (ChildrenEntity entity : childs) {
					Integer childAge = entity.getChildAge();
					if (childAge > 16) {
						ageCondition = false;
						break;
					}
				}

			}
			if (income.getEmpIncome() <= 300 && kidsCountCondition && ageCondition) {
				response.setPlanStatus("Approved");
			} else {
				response.setPlanStatus("Denied");
				response.setDenialReason("Not satisfied busines rules");
			}

		} else if ("Medicaid".equals(planName)) {
			Double empIncome = income.getEmpIncome();
			Double propertyIncome = income.getPropertyIncome();
			if (empIncome <= 300 && propertyIncome == 0) {
				response.setPlanStatus("Approved");
			} else {
				response.setPlanStatus("Denied");
				response.setDenialReason("High Income");
			}

		} else if ("Medicare".equals(planName)) {
			
				if (age >= 65) {
					response.setPlanStatus("Approved");
				} else {
					response.setPlanStatus("Denied");
					response.setDenialReason("Age not matched");
				}
			

		}else if ("NJW".equals(planName)) {
			EducationEntity educationEntity = eduRepo.findByCaseNum(caseNum);
			Integer graduationYear = educationEntity.getGraduationYear();
			int currentYear = LocalDate.now().getYear();
			if (income.getEmpIncome() <= 0 && graduationYear < currentYear) {
				response.setPlanStatus("Approved");
			} else {
				response.setPlanStatus("Denied");
				response.setDenialReason("Rules not satisfied");
			}
		}
		if (response.getPlanStatus() == "Approved") {

			response.setPlanStartDate(LocalDate.now());
			response.setPlanEndDate(LocalDate.now().plusMonths(6));
			response.setBenefitAmt(350.00);
		}

		return response;

	}
}
