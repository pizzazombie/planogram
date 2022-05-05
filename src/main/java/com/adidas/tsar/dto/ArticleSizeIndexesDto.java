package com.adidas.tsar.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ArticleSizeIndexesDto {

	private final ArticleDto articleDto;
	private final Set<String> sizeIndexes;

}
