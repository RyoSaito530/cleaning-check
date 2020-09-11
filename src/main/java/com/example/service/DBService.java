package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.CleaningInfo;
import com.example.domain.CleaningInfoResult;
import com.example.domain.StaffInfo;
import com.example.domain.StaffInfoResult;
import com.example.persistence.InfoMapper;

@Service
public class DBService {

	@Autowired
	private InfoMapper mapper;

	// スタッフ情報登録処理メソッド
	public void insertStaffInfo(StaffInfo info) {

		mapper.insert(info);
	}
	
	// 清掃チェック情報登録処理メソッド
	public void insertCleaningInfo(CleaningInfo info) {

		mapper.insertCleaning(info);
	}
	
	// 清掃チェック情報登録処理メソッド
	public void searchCleaningInfoDelete(CleaningInfo info) {

		mapper.delete(info);
	}
	
	// 全スタッフデータ検索処理メソッド
	public List<StaffInfoResult> searchStaffInfo() {

		return mapper.selectAll();
	}
	
	// 全清掃チェックデータ検索処理メソッド
	public List<CleaningInfoResult> searchCleaningInfo() {

		return mapper.selectAllCleaning();
	}

}
