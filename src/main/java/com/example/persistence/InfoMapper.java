package com.example.persistence;

import java.util.List;

import com.example.domain.CleaningInfo;
import com.example.domain.CleaningInfoResult;
import com.example.domain.StaffInfo;
import com.example.domain.StaffInfoResult;

public interface InfoMapper {
	
	public void insert(StaffInfo info);
	
	public void insertCleaning(CleaningInfo info);
	
	public List<StaffInfoResult> selectAll();
	
	public List<CleaningInfoResult> selectAllCleaning();
	
	public void delete(CleaningInfo info);
	
}
