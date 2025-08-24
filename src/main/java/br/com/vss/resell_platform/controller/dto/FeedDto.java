package br.com.vss.resell_platform.controller.dto;

import java.io.Serializable;
import java.util.List;

public record FeedDto(List<FeedItemDto> feedItems,
                      int page,
                      int pageSize,
                      Long totalElements) implements Serializable {
}
