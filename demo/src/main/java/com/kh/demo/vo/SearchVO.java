package com.kh.demo.vo;

import lombok.Data;

@Data
public class SearchVO {
	private Long memberNo;
	private String category;
	private String location;
	private String keyword;
}
