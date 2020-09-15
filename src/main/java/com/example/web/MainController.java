package com.example.web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.CleaningInfo;
import com.example.domain.CleaningInfoResult;
import com.example.domain.StaffInfo;
import com.example.domain.StaffInfoResult;
import com.example.service.DBService;

@Controller
public class MainController {
	
	@Autowired
	private DBService service;

	@Autowired
	private MailSender mailSender;

	@ModelAttribute("inputForm")
	public InputForm setForm() {
		return new InputForm();
	}

	@RequestMapping("/top")
	public String top(@ModelAttribute("inputForm") InputForm form, Model model) {
		
		// スタッフ情報一覧
		List<StaffInfoResult> searchList = service.searchStaffInfo();
		
		// Modelオブジェクトに検索結果を格納
		model.addAttribute("searchList", searchList);
		
		return "top";
	}
	
	@RequestMapping("/home")
	public String home() {
		return "home";
	}
	
	@RequestMapping("/list")
	public String list(@ModelAttribute("inputForm") InputForm form, Model model) {
		
		// 清掃チェック情報一覧
		List<CleaningInfoResult> searchList = service.searchCleaningInfo();
		
		for (CleaningInfoResult all : searchList) {
			all.setInpDate(all.getInpDate().substring(0, 16));
			all.setUpdDate(all.getUpdDate().substring(0, 16));
		}
		
		// Modelオブジェクトに検索結果を格納
		model.addAttribute("searchList", searchList);
		
		int count = searchList.size();
		
		form.setCount(count);
		
		return "list";
	}
	
	@RequestMapping("newStaff")
	public String newStaff() {
		return "newStaff";
	}

	@RequestMapping("/end")
	public String end() {
		return "end";
	}

	@RequestMapping(value = "/send")
	public String sendmail(@Validated @ModelAttribute("inputForm") InputForm form, BindingResult result, Model model) {
		
		if (form.getStaff_name().equals("")) {
			
			result.reject("errors.invalid.select");
			
			// スタッフ情報一覧
			List<StaffInfoResult> searchList = service.searchStaffInfo();
			
			// Modelオブジェクトに検索結果を格納
			model.addAttribute("searchList", searchList);
			
			return "top";
		}
		
		if (form.getTowel_men() == null) {
			form.setTowel_men("×");
		}
		
		if (form.getTowel_women() == null) {
			form.setTowel_women("×");
		}
		
		if (form.getAlcohol_men() == null) {
			form.setAlcohol_men("×");
		}
		
		if (form.getAlcohol_women() == null) {
			form.setAlcohol_women("×");
		}
		
		if (form.getLocker_men() == null) {
			form.setLocker_men("×");
		}
		
		if (form.getLocker_women() == null) {
			form.setLocker_women("×");
		}
		
		if (form.getPowder_men() == null) {
			form.setPowder_men("×");
		}
		
		if (form.getPowder_women() == null) {
			form.setPowder_women("×");
		}
		
		if (form.getToilet_men() == null) {
			form.setToilet_men("×");
		}
		
		if (form.getToilet_women() == null) {
			form.setToilet_women("×");
		}

		Calendar cal1 = Calendar.getInstance();

		// Date型に変換
		Date date1 = cal1.getTime();

		// 表示形式を指定
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdformat2 = new SimpleDateFormat("HH");
		String fdate1 = sdformat.format(date1); // 年月日
		String fdate2 = sdformat2.format(date1); // 時

		String body = "担当スタッフ：" + form.getStaff_name() + "\n" + "タオル（男）：" + form.getTowel_men() + "\n" + "タオル（女）："
				+ form.getTowel_women() + "\n" + "アルコール（男）：" + form.getAlcohol_men() + "\n" + "アルコール（女）："
				+ form.getAlcohol_women() + "\n" + "ロッカー（男）：" + form.getLocker_men() + "\n" + "ロッカー（女）："
				+ form.getLocker_women() + "\n" + "パウダー（男）：" + form.getPowder_men() + "\n" + "パウダー（女）："
				+ form.getPowder_women() + "\n" + "トイレ（男）：" + form.getToilet_men() + "\n" + "トイレ（女）："
				+ form.getToilet_women();

		SimpleMailMessage msg = new SimpleMailMessage();

		msg.setTo("n.ise@zexis-net.jp");
		msg.setSubject(fdate1 + "清掃チェック報告");
		msg.setText(fdate2 + "時\n---------------------------\n" + body + "\n---------------------------");

		mailSender.send(msg);
		
		// データ登録に利用するドメインクラスのインスタンス化
		CleaningInfo info = new CleaningInfo();
		
		// Formクラスの値をドメインクラスにコピー
		BeanUtils.copyProperties(form, info);

		// データ登録を行うためのサービス処理呼び出し
		service.insertCleaningInfo(info);

		return "redirect:/end?finish";
	}

	// リダイレクト後に呼び出される処理メソッド
	@RequestMapping(value = "/end", params = "finish")
	public String endFinish() {
		return "end";
	}
	
	// 「登録」ボタン押下時の処理メソッド
	@RequestMapping(value = "/home", params = "insert")
	public String insert(@Validated @ModelAttribute("inputForm") InputForm form, BindingResult result) {
		
		if (form.getFirst_name().equals("") || form.getLast_name().equals("")) {
			result.reject("errors.invalid.name");
			return "newStaff";
		}
		
		if (form.getStaff_id() == 0) {
			result.reject("errors.invalid.number");
			return "newStaff";
		}
		
		// スタッフ情報一覧
		List<StaffInfoResult> searchList = service.searchStaffInfo();
		
		for (StaffInfoResult all : searchList) {
			
			if (form.getStaff_id() == all.getStaffId()) {
				result.reject("errors.invalid.double");
				return "newStaff";
			}
			
		}

		// データ登録に利用するドメインクラスのインスタンス化
		StaffInfo info = new StaffInfo();

		// Formクラスの値をドメインクラスにコピー
		info.setStaff_id(form.getStaff_id());
		info.setStaff_name(form.getLast_name() + "　" + form.getFirst_name());

		// データ登録を行うためのサービス処理呼び出し
		service.insertStaffInfo(info);

		// 完了画面へのリダイレクト
		return "redirect:/insert?finish";
	}

	// リダイレクト後に呼び出される処理メソッド
	@RequestMapping(value = "/insert", params = "finish")
	public String insertsFinish(@ModelAttribute("inputForm") InputForm form) {
		
		form.setCreate("ok");
		
		return "newStaff";
	}
	
	/*
	 * 「削除」ボタン押下時の処理メソッド
	 */
	@RequestMapping(value = { "/home" }, params = "delete")
	public String delete(@ModelAttribute("inputForm") InputForm form, Model model) {

		// データ検索に利用するドメインクラスのインスタンス化
		CleaningInfo info = new CleaningInfo();

		// Formクラスの値をドメインクラスにコピー
		info.setCleaning_id(form.getCleaning_id());

		// データ検索を行うためのサービス処理呼び出し
		service.searchCleaningInfoDelete(info);

		// 完了画面へのリダイレクト
		return "redirect:/delete?finish";
	}

	// リダイレクト後に呼び出される処理メソッド
	@RequestMapping(value = "/delete", params = "finish")
	public String deleteFinish(@ModelAttribute("inputForm") InputForm form, Model model) {
		
		// 清掃チェック情報一覧
		List<CleaningInfoResult> searchList = service.searchCleaningInfo();
		
		for (CleaningInfoResult all : searchList) {
			all.setInpDate(all.getInpDate().substring(0, 16));
			all.setUpdDate(all.getUpdDate().substring(0, 16));
		}
		
		// Modelオブジェクトに検索結果を格納
		model.addAttribute("searchList", searchList);
		
		int count = searchList.size();
		
		form.setCount(count);

		return "list";
	}

}
