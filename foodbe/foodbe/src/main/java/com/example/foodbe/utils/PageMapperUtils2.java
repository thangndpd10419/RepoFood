package com.example.foodbe.utils;

import com.example.foodbe.payload.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class PageMapperUtils2 {

    public <D,E> PageResponse<D> toPageResponseDto(Page<E> page, Function<E,D> mapper){

        // vì dùng generic nên không thể biết chính xác mapper nào để data từ E(entity)-D(dto)
        // ==> cần Function(D,E) hoặc ModelMapper( thư viện) để mappẻr dữ liệu

        List<D> listDto = page.getContent().stream()
                .map(mapper)
                .toList();
// Function ở đây sẽ là  đối tượng được tạo ở sểvice bằng object mapper của mỗi entity .toDto
 //  vơi object F la instance của Funciton sẽ dùng cách trỏ đén method của đối tương mapper và dùng
        //  methof đó để tạo F. khi này vì cơ chế của map() với function interface
        // mapp sẽ tựu động gọi apply để trả ra kết quả đã mapper.

        return PageResponse.<D>builder()
                .content(listDto)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .build();
    }

}
